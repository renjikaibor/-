package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "tools",
    indices = [
        Index(value = ["name"], name = "idx_tools_name", unique = true),
        Index(value = ["type"], name = "idx_tools_type")
    ]
)
data class ToolEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val displayName: String,
    val version: String,
    val description: String,
    val type: ToolType = ToolType.INSTALLED,
    val installCommand: String? = null,
    val entryPoint: String? = null, // Main class or script path
    val parameters: String? = null, // JSON schema for parameters
    val permissions: String? = null, // JSON list of required permissions
    val isEnabled: Boolean = true,
    val callCount: Long = 0,
    val lastCalledAt: Date? = null,
    val installedAt: Date = Date(),
    val updatedAt: Date = Date(),
    val metadata: String? = null // JSON for extra data
) {
    constructor() : this(0, "", "", "1.0.0", "", ToolType.INSTALLED, null, null, null, null, true, 0, null, Date(), Date(), null)
}
