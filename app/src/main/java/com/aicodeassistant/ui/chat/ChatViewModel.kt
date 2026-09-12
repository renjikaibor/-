package com.aicodeassistant.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.MessageRole
import com.aicodeassistant.domain.repository.ChatRepository
import com.aicodeassistant.domain.repository.ModelRepository
import com.aicodeassistant.domain.repository.ToolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val toolRepository: ToolRepository
) : ViewModel() {

    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId = _currentSessionId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentModel = MutableStateFlow<AIModel?>(null)
    val currentModel = _currentModel.asStateFlow()

    val availableModels = modelRepository.observeAllEnabledModels()

    private val _showModelSelector = MutableStateFlow(false)
    val showModelSelector = _showModelSelector.asStateFlow()

    init {
        loadDefaultModel()
        observeCurrentSession()
    }

    private fun loadDefaultModel() {
        viewModelScope.launch {
            val model = modelRepository.getDefaultModel()
            _currentModel.value = model
        }
    }

    private fun observeCurrentSession() {
        // TODO: Get current session from navigation or saved state
        val sessionId = 1L // Default session
        _currentSessionId.value = sessionId
        
        viewModelScope.launch {
            chatRepository.observeMessages(sessionId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun sendMessage(content: String, images: List<String>) {
        val sessionId = _currentSessionId.value ?: return
        val model = _currentModel.value ?: return
        
        _isLoading.value = true

        // Add user message
        val userMessage = ChatMessage.user(sessionId, content, images)
        viewModelScope.launch {
            chatRepository.addMessage(userMessage)
            
            // Call AI service to get response
            // TODO: Integrate with AIChatService
            simulateAIResponse(sessionId, content, model)
        }
    }

    private fun simulateAIResponse(sessionId: Long, userContent: String, model: AIModel) {
        viewModelScope.launch {
            // This would be replaced with actual AIChatService call
            val aiMessage = ChatMessage.assistant(sessionId, "这是模拟的 AI 回复。实际实现中会调用 AIChatService 进行流式生成。")
            chatRepository.addMessage(aiMessage)
            _isLoading.value = false
        }
    }

    fun stopGeneration() {
        _isLoading.value = false
        // TODO: Cancel ongoing AI generation
    }

    fun selectModel(model: AIModel) {
        _currentModel.value = model
        viewModelScope.launch {
            modelRepository.setDefaultModel(model.id)
        }
    }

    fun newChat() {
        val model = _currentModel.value ?: return
        viewModelScope.launch {
            val session = chatRepository.createSession(
                com.aicodeassistant.domain.model.ChatSession.create(model.modelId, "新对话")
            )
            _currentSessionId.value = session.id
            _messages.value = emptyList()
        }
    }

    fun pickImage() {
        // TODO: Launch image picker
    }
}
