package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "tool_call_logs",
    foreignKeys = [
        ForeignKey(
            entity = ToolEntity::class,
            parentColumns = ["id"],
            childColumns = ["toolId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["toolId"], name = "idx_tool_call_logs_tool_id"),
        Index(value = ["sessionId"], name = "idx_tool_call_logs_session_id"),
        Index(value = ["createdAt"], name = "idx_tool_call_logs_created_at")
    ]
)
data class ToolCallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolId: Long?,
    val sessionId: Long?,
    val toolName: String,
    val inputArgs: String, // JSON
    val outputResult: String?, // JSON
    val status: ToolCallStatus = ToolCallStatus.PENDING,
    val errorMessage: String? = null,
    val durationMs: Long = 0,
    val createdAt: Date = Date()
) {
    constructor() : this(0, null, null, "", "", null, ToolCallStatus.PENDING, null, 0, Date())
}
