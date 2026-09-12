package com.aicodeassistant.data.remote

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val max_tokens: Int? = null,
    val temperature: Float? = null,
    val top_p: Float? = null,
    val stream: Boolean = true,
    val tools: List<Tool>? = null,
    val tool_choice: Any? = null, // "auto" | "none" | ToolChoice
    val seed: Int? = null,
    val reasoning_effort: String? = null,
    @SerializedName("extra_body")
    val extraBody: Map<String, Any>? = null
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: Any?, // String or List<ContentPart>
    val name: String? = null,
    val tool_call_id: String? = null,
    val tool_calls: List<ToolCall>? = null
) {
    companion object {
        fun system(content: String): ChatMessage = ChatMessage("system", content)
        fun user(content: String): ChatMessage = ChatMessage("user", content)
        fun user(content: List<ContentPart>): ChatMessage = ChatMessage("user", content)
        fun assistant(content: String? = null, toolCalls: List<ToolCall>? = null): ChatMessage =
            ChatMessage("assistant", content, tool_calls = toolCalls)
        fun tool(content: String, toolCallId: String): ChatMessage =
            ChatMessage("tool", content, tool_call_id = toolCallId)
    }
}

@Serializable
sealed class ContentPart {
    @Serializable
    data class Text(val text: String, val type: String = "text") : ContentPart()
    @Serializable
    data class ImageUrl(
        val image_url: ImageUrlData,
        val type: String = "image_url"
    ) : ContentPart()
}

@Serializable
data class ImageUrlData(
    val url: String,
    val detail: String = "auto"
)

@Serializable
data class Tool(
    val type: String = "function",
    val function: FunctionDefinition
)

@Serializable
data class FunctionDefinition(
    val name: String,
    val description: String,
    val parameters: Map<String, Any> // JSON Schema
)

@Serializable
data class ToolChoice(
    val type: String = "function",
    val function: FunctionName
)

@Serializable
data class FunctionName(
    val name: String
)

@Serializable
data class ToolCall(
    val id: String,
    val type: String = "function",
    val function: FunctionCall
)

@Serializable
data class FunctionCall(
    val name: String,
    val arguments: String // JSON string
)

@Serializable
data class ChatCompletionResponse(
    val id: String,
    val object: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage? = null
)

@Serializable
data class Choice(
    val index: Int,
    val message: ChatMessage,
    val finish_reason: String?,
    val delta: Delta? = null
)

@Serializable
data class Delta(
    val role: String? = null,
    val content: String? = null,
    val tool_calls: List<ToolCallDelta>? = null
)

@Serializable
data class ToolCallDelta(
    val index: Int,
    val id: String? = null,
    val type: String? = null,
    val function: FunctionCallDelta? = null
)

@Serializable
data class FunctionCallDelta(
    val name: String? = null,
    val arguments: String? = null
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class ModelsResponse(
    val object: String,
    val data: List<ModelInfo>
)

@Serializable
data class ModelInfo(
    val id: String,
    val object: String,
    val created: Long,
    val owned_by: String
)

@Serializable
data class ErrorResponse(
    val error: ApiError
)

@Serializable
data class ApiError(
    val message: String,
    val type: String,
    val param: String? = null,
    val code: String? = null
)

@Serializable
data class SearchRequest(
    val q: String,
    val num: Int = 10,
    val gl: String = "us",
    val hl: String = "en"
)

@Serializable
data class SearchResponse(
    val organic: List<SearchResult> = emptyList(),
    val searchInformation: SearchInformation? = null
)

@Serializable
data class SearchResult(
    val title: String,
    val link: String,
    val snippet: String,
    val source: String? = null,
    val date: String? = null,
    val position: Int = 0
)

@Serializable
data class SearchInformation(
    val totalResults: String,
    val searchTime: Double
)
