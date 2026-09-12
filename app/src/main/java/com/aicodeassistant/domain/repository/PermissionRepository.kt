package com.aicodeassistant.domain.repository

import com.aicodeassistant.domain.model.Permission
import com.aicodeassistant.domain.model.PermissionStatus
import kotlinx.coroutines.flow.Flow

interface PermissionRepository {
    suspend fun addPermission(permission: Permission): Permission
    suspend fun updatePermission(permission: Permission): Permission
    suspend fun getPermission(id: Long): Permission?
    suspend fun getPermissionByName(name: String): Permission?
    fun observePermissionsByStatus(status: PermissionStatus): Flow<List<Permission>>
    fun observeAllPermissions(): Flow<List<Permission>>
    suspend fun updatePermissionStatus(name: String, status: PermissionStatus, grantedAt: java.util.Date?): Boolean
    suspend fun requestPermission(name: String): Boolean
    suspend fun initializeDefaultPermissions(): List<Permission>
}
