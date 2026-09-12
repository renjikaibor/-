package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "files",
    indices = [
        Index(value = ["parentId"], name = "idx_files_parent_id"),
        Index(value = ["path"], name = "idx_files_path", unique = true),
        Index(value = ["type"], name = "idx_files_type")
    ]
)
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentId: Long? = null,
    val name: String,
    val path: String,
    val type: FileType = FileType.FILE,
    val language: String? = null, // CodeLanguage id
    val content: String? = null, // For small files, otherwise null
    val size: Long = 0,
    val isOpen: Boolean = false,
    val isDirty: Boolean = false,
    val cursorPosition: Int = 0,
    val scrollPosition: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val metadata: String? = null // JSON for extra data
) {
    constructor() : this(0, null, "", "", FileType.FILE, null, null, 0, false, false, 0, 0, Date(), Date(), null)
}
