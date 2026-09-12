package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val key: String,
    val value: String, // JSON serialized
    val updatedAt: Date = Date()
) {
    constructor() : this("", "", Date())
}
