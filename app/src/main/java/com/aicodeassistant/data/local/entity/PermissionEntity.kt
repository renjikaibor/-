package com.aicodeassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "permissions",
    indices = [
        Index(value = ["permissionName"], name = "idx_permissions_name", unique = true)
    ]
)
data class PermissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val permissionName: String, // e.g., "android.permission.CAMERA"
    val displayName: String,
    val rationale: String,
    val status: PermissionStatus = PermissionStatus.DENIED,
    val isRequired: Boolean = false,
    val grantedAt: Date? = null,
    val lastRequestedAt: Date? = null,
    val requestCount: Int = 0,
    val metadata: String? = null
) {
    constructor() : this(0, "", "", "", PermissionStatus.DENIED, false, null, null, 0, null)
}
