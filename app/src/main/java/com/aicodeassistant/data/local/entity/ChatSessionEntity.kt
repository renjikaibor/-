package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "chat_sessions",
    indices = [Index(value = ["updatedAt"], name = "idx_chat_sessions_updated_at")]
)
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val modelId: String,
    val systemPrompt: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val messageCount: Int = 0,
    val isPinned: Boolean = false,
    val metadata: String? = null // JSON for extra data
) {
    constructor() : this(0, "", "", null, Date(), Date(), 0, false, null)
}
