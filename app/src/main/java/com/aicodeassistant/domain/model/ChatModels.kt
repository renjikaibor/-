package com.aicodeassistant.domain.model

import com.aicodeassistant.data.local.entity.CodeLanguage
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Serializable
data class ChatSession(
    val id: Long = 0,
    val title: String,
    val modelId: String,
    val systemPrompt: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val messageCount: Int = 0,
    val isPinned: Boolean = false,
    val metadata: Map<String, String> = emptyMap()
) {
    companion object {
        fun create(modelId: String, title: String = "新对话"): ChatSession {
            return ChatSession(
                title = title,
                modelId = modelId,
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
}

@Serializable
data class ChatMessage(
    val id: Long = 0,
    val sessionId: Long,
    val role: MessageRole,
    val content: String,
    val toolCalls: List<ToolCall> = emptyList(),
    val toolCallId: String? = null,
    val images: List<String> = emptyList(),
    val metadata: Map<String, Any> = emptyMap(),
    val createdAt: Date = Date(),
    val isStreaming: Boolean = false,
    val isError: Boolean = false
) {
    companion object {
        fun user(sessionId: Long, content: String, images: List<String> = emptyList()): ChatMessage {
            return ChatMessage(
                sessionId = sessionId,
                role = MessageRole.USER,
                content = content,
                images = images,
                createdAt = Date()
            )
        }

        fun assistant(sessionId: Long, content: String = ""): ChatMessage {
            return ChatMessage(
                sessionId = sessionId,
                role = MessageRole.ASSISTANT,
                content = content,
                createdAt = Date()
            )
        }

        fun tool(sessionId: Long, toolCallId: String, content: String): ChatMessage {
            return ChatMessage(
                sessionId = sessionId,
                role = MessageRole.TOOL,
                content = content,
                toolCallId = toolCallId,
                createdAt = Date()
            )
        }

        fun system(sessionId: Long, content: String): ChatMessage {
            return ChatMessage(
                sessionId = sessionId,
                role = MessageRole.SYSTEM,
                content = content,
                createdAt = Date()
            )
        }
    }
}

enum class MessageRole {
    SYSTEM, USER, ASSISTANT, TOOL
}

@Serializable
data class ToolCall(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val arguments: Map<String, Any> = emptyMap(),
    val status: ToolCallStatus = ToolCallStatus.PENDING,
    val result: String? = null,
    val error: String? = null,
    val startedAt: Date = Date(),
    val completedAt: Date? = null
)

enum class ToolCallStatus {
    PENDING, RUNNING, SUCCESS, FAILED, CANCELLED
}

@Serializable
data class AIModel(
    val id: Long = 0,
    val name: String,
    val modelId: String,
    val baseUrl: String,
    val apiKey: String,
    val type: ModelType = ModelType.CUSTOM,
    val maxTokens: Int = 16384,
    val temperature: Float = 1.0f,
    val topP: Float = 0.95f,
    val isStream: Boolean = true,
    val supportsVision: Boolean = false,
    val supportsThinking: Boolean = false,
    val extraParams: Map<String, Any> = emptyMap(),
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    val displayName: String
        get() = if (name.isNotBlank()) name else modelId
}

enum class ModelType {
    BUILTIN, CUSTOM
}

@Serializable
data class Tool(
    val id: Long = 0,
    val name: String,
    val displayName: String,
    val version: String,
    val description: String,
    val type: ToolType = ToolType.INSTALLED,
    val installCommand: String? = null,
    val entryPoint: String? = null,
    val parameters: Map<String, Any> = emptyMap(),
    val permissions: List<String> = emptyList(),
    val isEnabled: Boolean = true,
    val callCount: Long = 0,
    val lastCalledAt: Date? = null,
    val installedAt: Date = Date(),
    val updatedAt: Date = Date(),
    val metadata: Map<String, String> = emptyMap()
)

enum class ToolType {
    BUILTIN, INSTALLED, CUSTOM
}

@Serializable
data class ToolCallLog(
    val id: Long = 0,
    val toolId: Long?,
    val sessionId: Long?,
    val toolName: String,
    val inputArgs: Map<String, Any>,
    val outputResult: Map<String, Any>?,
    val status: ToolCallStatus = ToolCallStatus.PENDING,
    val errorMessage: String? = null,
    val durationMs: Long = 0,
    val createdAt: Date = Date()
)

@Serializable
data class FileItem(
    val id: Long = 0,
    val parentId: Long? = null,
    val name: String,
    val path: String,
    val type: FileType = FileType.FILE,
    val language: CodeLanguage? = null,
    val content: String? = null,
    val size: Long = 0,
    val isOpen: Boolean = false,
    val isDirty: Boolean = false,
    val cursorPosition: Int = 0,
    val scrollPosition: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val metadata: Map<String, String> = emptyMap()
) {
    val isDirectory: Boolean
        get() = type == FileType.DIRECTORY

    val extension: String
        get() = name.substringAfterLast(".", "").lowercase()

    companion object {
        fun createDirectory(parentId: Long?, name: String, path: String): FileItem {
            return FileItem(
                parentId = parentId,
                name = name,
                path = path,
                type = FileType.DIRECTORY,
                createdAt = Date(),
                updatedAt = Date()
            )
        }

        fun createFile(parentId: Long?, name: String, path: String, language: CodeLanguage = CodeLanguage.TEXT): FileItem {
            return FileItem(
                parentId = parentId,
                name = name,
                path = path,
                type = FileType.FILE,
                language = language,
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
}

enum class FileType {
    FILE, DIRECTORY
}

@Serializable
data class Permission(
    val id: Long = 0,
    val permissionName: String,
    val displayName: String,
    val rationale: String,
    val status: PermissionStatus = PermissionStatus.DENIED,
    val isRequired: Boolean = false,
    val grantedAt: Date? = null,
    val lastRequestedAt: Date? = null,
    val requestCount: Int = 0
)

enum class PermissionStatus {
    GRANTED, DENIED, REQUESTED, PERMANENTLY_DENIED
}

@Serializable
data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String,
    val source: String? = null,
    val date: String? = null
)

@Serializable
data class SearchResponse(
    val results: List<SearchResult> = emptyList(),
    val query: String = "",
    val totalResults: Int = 0,
    val searchTimeMs: Long = 0
)

@Serializable
data class CodeExecutionResult(
    val success: Boolean,
    val output: String = "",
    val error: String? = null,
    val exitCode: Int = 0,
    val durationMs: Long = 0
)

@Serializable
data class ModelParameters(
    val maxTokens: Int = 16384,
    val temperature: Float = 1.0f,
    val topP: Float = 0.95f,
    val stream: Boolean = true,
    val reasoningEffort: String? = null,
    val enableThinking: Boolean = false,
    val seed: Int? = null,
    val extraBody: Map<String, Any> = emptyMap()
)
