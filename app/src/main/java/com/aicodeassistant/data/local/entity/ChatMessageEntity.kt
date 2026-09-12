package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sessionId"], name = "idx_chat_messages_session_id"),
        Index(value = ["createdAt"], name = "idx_chat_messages_created_at")
    ]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val role: MessageRole,
    val content: String,
    val toolCalls: String? = null, // JSON serialized ToolCall list
    val toolCallId: String? = null,
    val images: String? = null, // JSON serialized list of image URIs
    val metadata: String? = null, // JSON for extra data (model params, etc.)
    val createdAt: Date = Date(),
    val isStreaming: Boolean = false,
    val isError: Boolean = false
) {
    constructor() : this(0, 0, MessageRole.USER, "", null, null, null, null, Date(), false, false)
}
