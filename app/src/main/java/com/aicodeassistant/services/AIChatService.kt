package com.aicodeassistant.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aicodeassistant.R
import com.aicodeassistant.data.remote.ApiModels.*
import com.aicodeassistant.data.remote.ApiService
import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.MessageRole
import com.aicodeassistant.domain.model.ToolCall
import com.aicodeassistant.domain.model.ToolCallStatus
import com.aicodeassistant.domain.repository.ChatRepository
import com.aicodeassistant.domain.repository.ModelRepository
import com.aicodeassistant.domain.repository.ToolRepository
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

class AIChatService : Service() {

    private val CHANNEL_ID = "ai_chat_service"
    private val NOTIFICATION_ID = 1001

    private var chatRepository: ChatRepository? = null
    private var modelRepository: ModelRepository? = null
    private var toolRepository: ToolRepository? = null
    private var apiService: ApiService? = null
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val messageChannel = Channel<ChatMessage>(Channel.UNLIMITED)
    private var currentModel: AIModel? = null
    private var currentSessionId: Long = 0
    private var isProcessing = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeRepositories()
    }

    private fun initializeRepositories() {
        val appComponent = com.aicodeassistant.AICodeApplication.getAppComponent(this)
        chatRepository = appComponent.chatRepository
        modelRepository = appComponent.modelRepository
        toolRepository = appComponent.toolRepository
        
        // Initialize API service with default model
        scope.launch {
            currentModel = modelRepository?.getDefaultModel()
            setupApiService()
        }
    }

    private fun setupApiService() {
        currentModel?.let { model ->
            val retrofit = Retrofit.Builder()
                .baseUrl(model.baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "AI Chat Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background AI chat processing"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val intent = Intent(this, com.aicodeassistant.ui.main.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI Code Assistant")
            .setContentText("Processing chat...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    // Public API for UI to send messages
    suspend fun sendMessage(sessionId: Long, message: ChatMessage): Flow<ChatMessage> = flow {
        currentSessionId = sessionId
        isProcessing = true
        
        // Save user message
        val savedUserMessage = chatRepository?.addMessage(message)
        savedUserMessage?.let { emit(it) }

        // Get conversation history
        val messages = chatRepository?.observeMessages(sessionId)
            ?.take(1)
            ?.first() ?: emptyList()

        // Prepare API request
        val model = currentModel ?: modelRepository?.getDefaultModel() ?: return@flow
        val apiMessages = buildApiMessages(messages, model)
        val tools = getAvailableTools()

        val request = ChatCompletionRequest(
            model = model.modelId,
            messages = apiMessages,
            max_tokens = model.maxTokens,
            temperature = model.temperature,
            top_p = model.topP,
            stream = model.isStream,
            tools = tools,
            tool_choice = if (tools.isNotEmpty()) "auto" else null,
            seed = model.extraParams["seed"] as? Int,
            reasoning_effort = model.extraParams["reasoning_effort"] as? String,
            extraBody = model.extraParams["chat_template_kwargs"] as? Map<String, Any>
        )

        val authHeader = "Bearer ${model.apiKey}"
        
        // Stream response
        val responseFlow = if (model.baseUrl.contains("nvidia")) {
            apiService?.chatCompletion(authHeader, request)
        } else {
            apiService?.chatCompletionCustom("${model.baseUrl}/chat/completions", authHeader, request)
        }

        responseFlow?.collect { responseBody ->
            processStreamResponse(responseBody, sessionId)
        }

        isProcessing = false
    }.flowOn(Dispatchers.IO)

    private fun buildApiMessages(messages: List<ChatMessage>, model: AIModel): List<ChatMessage> {
        val apiMessages = mutableListOf<ChatMessage>()
        
        // Add system prompt if exists
        // TODO: Get system prompt from session
        
        for (msg in messages) {
            when (msg.role) {
                MessageRole.USER -> {
                    if (msg.images.isNotEmpty() && model.supportsVision) {
                        val parts = mutableListOf<ContentPart>()
                        if (msg.content.isNotBlank()) {
                            parts.add(ContentPart.Text(msg.content))
                        }
                        for (imageUri in msg.images) {
                            parts.add(ContentPart.ImageUrl(ImageUrlData(imageUri)))
                        }
                        apiMessages.add(ChatMessage.user(parts))
                    } else {
                        apiMessages.add(ChatMessage.user(msg.content))
                    }
                }
                MessageRole.ASSISTANT -> {
                    if (msg.toolCalls.isNotEmpty()) {
                        apiMessages.add(ChatMessage.assistant(msg.content, msg.toolCalls))
                    } else {
                        apiMessages.add(ChatMessage.assistant(msg.content))
                    }
                }
                MessageRole.TOOL -> {
                    msg.toolCallId?.let { id ->
                        apiMessages.add(ChatMessage.tool(msg.content, id))
                    }
                }
                MessageRole.SYSTEM -> {
                    apiMessages.add(ChatMessage.system(msg.content))
                }
            }
        }
        return apiMessages
    }

    private fun getAvailableTools(): List<Tool> {
        // TODO: Get from toolRepository and convert to API format
        return emptyList()
    }

    private fun processStreamResponse(responseBody: ResponseBody, sessionId: Long) {
        val source = responseBody.byteStream().bufferedReader()
        var buffer = ""
        var currentMessage: ChatMessage? = null
        var currentToolCalls: MutableMap<Int, ToolCall> = mutableMapOf()

        try {
            source.use { reader ->
                reader.forEachLine { line ->
                    if (line.startsWith("data: ")) {
                        val data = line.substring(6)
                        if (data == "[DONE]") {
                            finishMessage(currentMessage, sessionId)
                            return@forEachLine
                        }
                        try {
                            val response = gson.fromJson(data, ChatCompletionResponse::class.java)
                            response.choices.forEach { choice ->
                                if (choice.delta != null) {
                                    processDelta(choice.delta!!, sessionId, currentMessage, currentToolCalls)
                                } else if (choice.message != null) {
                                    processMessage(choice.message!!, sessionId)
                                }
                            }
                        } catch (e: Exception) {
                            // Ignore parse errors
                        }
                    }
                }
            }
        } finally {
            finishMessage(currentMessage, sessionId)
        }
    }

    private fun processDelta(
        delta: Delta,
        sessionId: Long,
        currentMessage: ChatMessage?,
        currentToolCalls: MutableMap<Int, ToolCall>
    ) {
        // Handle content delta
        delta.content?.let { content ->
            if (currentMessage == null) {
                currentMessage = ChatMessage.assistant(sessionId, content).copy(isStreaming = true)
            } else {
                currentMessage = currentMessage.copy(
                    content = currentMessage.content + content,
                    isStreaming = true
                )
            }
            // Emit partial update
            // TODO: Send to UI via callback/channel
        }

        // Handle tool calls delta
        delta.tool_calls?.forEach { toolCallDelta ->
            val index = toolCallDelta.index
            var toolCall = currentToolCalls[index] ?: ToolCall(
                id = toolCallDelta.id ?: UUID.randomUUID().toString(),
                name = toolCallDelta.function?.name ?: "",
                arguments = emptyMap()
            ).apply {
                status = ToolCallStatus.RUNNING
            }

            toolCallDelta.function?.arguments?.let { args ->
                try {
                    val newArgs = gson.fromJson(toolCall.arguments.toString() + args, Map::class.java)
                    toolCall = toolCall.copy(arguments = newArgs ?: emptyMap())
                } catch (e: Exception) {
                    // Ignore partial JSON parse errors
                }
            }

            toolCallDelta.id?.let { toolCall = toolCall.copy(id = it) }
            toolCallDelta.function?.name?.let { toolCall = toolCall.copy(name = it) }
            
            currentToolCalls[index] = toolCall
        }
    }

    private fun processMessage(message: ChatMessage, sessionId: Long) {
        val chatMessage = ChatMessage.assistant(sessionId, message.content ?: "")
        if (message.tool_calls != null) {
            val toolCalls = message.tool_calls!!.map { tc ->
                com.aicodeassistant.domain.model.ToolCall(
                    id = tc.id,
                    name = tc.function.name,
                    arguments = gson.fromJson(tc.function.arguments, Map::class.java) ?: emptyMap()
                )
            }
            val finalMessage = chatMessage.copy(toolCalls = toolCalls)
            chatRepository?.addMessage(finalMessage)
            
            // Execute tool calls
            toolCalls.forEach { toolCall ->
                scope.launch { executeToolCall(toolCall, sessionId) }
            }
        } else {
            chatRepository?.addMessage(chatMessage)
        }
    }

    private fun finishMessage(message: ChatMessage?, sessionId: Long) {
        message?.let { msg ->
            val finalMessage = msg.copy(isStreaming = false)
            chatRepository?.addMessage(finalMessage)
        }
    }

    private suspend fun executeToolCall(toolCall: com.aicodeassistant.domain.model.ToolCall, sessionId: Long) {
        // TODO: Implement tool execution via ToolExecutionService
        val result = "Tool execution not yet implemented"
        
        // Save tool result as message
        val toolMessage = ChatMessage.tool(sessionId, toolCall.id, result)
        chatRepository?.addMessage(toolMessage)
        
        // Continue conversation with tool result
        sendMessage(sessionId, toolMessage)
    }

    fun updateModel(model: AIModel) {
        currentModel = model
        setupApiService()
    }

    fun stopGeneration() {
        scope.cancel()
        scope.coroutineContext[Job]?.cancel()
        isProcessing = false
    }
}
