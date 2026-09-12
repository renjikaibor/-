package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "ai_models",
    indices = [Index(value = ["isDefault"], name = "idx_ai_models_default")]
)
data class AIModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val modelId: String, // e.g., "moonshotai/kimi-k3"
    val baseUrl: String,
    val apiKey: String, // Encrypted
    val type: ModelType = ModelType.CUSTOM,
    val maxTokens: Int = 16384,
    val temperature: Float = 1.0f,
    val topP: Float = 0.95f,
    val isStream: Boolean = true,
    val supportsVision: Boolean = false,
    val supportsThinking: Boolean = false,
    val extraParams: String? = null, // JSON for extra parameters
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    constructor() : this(0, "", "", "", "", ModelType.CUSTOM, 16384, 1.0f, 0.95f, true, false, false, null, false, true, Date(), Date())
}
