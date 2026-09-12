package com.aicodeassistant.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.aicodeassistant.data.local.AppDatabase
import com.aicodeassistant.data.local.dao.PermissionDao
import com.aicodeassistant.data.local.entity.PermissionEntity
import com.aicodeassistant.data.local.entity.PermissionStatus
import com.aicodeassistant.domain.model.Permission
import com.aicodeassistant.domain.model.PermissionStatus as DomainPermissionStatus
import com.aicodeassistant.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val context: Context
) : PermissionRepository {

    private val dao: PermissionDao = database.permissionDao()

    private fun PermissionEntity.toDomain(): Permission {
        return Permission(
            id = id,
            permissionName = permissionName,
            displayName = displayName,
            rationale = rationale,
            status = when (status) {
                PermissionStatus.GRANTED -> DomainPermissionStatus.GRANTED
                PermissionStatus.DENIED -> DomainPermissionStatus.DENIED
                PermissionStatus.REQUESTED -> DomainPermissionStatus.REQUESTED
                else -> DomainPermissionStatus.PERMANENTLY_DENIED
            },
            isRequired = isRequired,
            grantedAt = grantedAt,
            lastRequestedAt = lastRequestedAt,
            requestCount = requestCount
        )
    }

    private fun Permission.toEntity(): PermissionEntity {
        return PermissionEntity(
            id = id,
            permissionName = permissionName,
            displayName = displayName,
            rationale = rationale,
            status = when (status) {
                DomainPermissionStatus.GRANTED -> PermissionStatus.GRANTED
                DomainPermissionStatus.DENIED -> PermissionStatus.DENIED
                DomainPermissionStatus.REQUESTED -> PermissionStatus.REQUESTED
                else -> PermissionStatus.PERMANENTLY_DENIED
            },
            isRequired = isRequired,
            grantedAt = grantedAt,
            lastRequestedAt = lastRequestedAt,
            requestCount = requestCount
        )
    }

    override suspend fun addPermission(permission: Permission): Permission {
        val entity = permission.toEntity()
        val id = dao.insert(entity)
        return permission.copy(id = id)
    }

    override suspend fun updatePermission(permission: Permission): Permission {
        val entity = permission.toEntity()
        dao.update(entity)
        return permission
    }

    override suspend fun getPermission(id: Long): Permission? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getPermissionByName(name: String): Permission? {
        return dao.getByName(name)?.toDomain()
    }

    override fun observePermissionsByStatus(status: DomainPermissionStatus): Flow<List<Permission>> {
        val entityStatus = when (status) {
            DomainPermissionStatus.GRANTED -> PermissionStatus.GRANTED
            DomainPermissionStatus.DENIED -> PermissionStatus.DENIED
            DomainPermissionStatus.REQUESTED -> PermissionStatus.REQUESTED
            else -> PermissionStatus.PERMANENTLY_DENIED
        }
        return dao.getByStatus(entityStatus).map { it.map { it.toDomain() } }
    }

    override fun observeAllPermissions(): Flow<List<Permission>> {
        return dao.getAll().map { it.map { it.toDomain() } }
    }

    override suspend fun updatePermissionStatus(name: String, status: DomainPermissionStatus, grantedAt: java.util.Date?): Boolean {
        val entityStatus = when (status) {
            DomainPermissionStatus.GRANTED -> PermissionStatus.GRANTED
            DomainPermissionStatus.DENIED -> PermissionStatus.DENIED
            DomainPermissionStatus.REQUESTED -> PermissionStatus.REQUESTED
            else -> PermissionStatus.PERMANENTLY_DENIED
        }
        return dao.updateStatus(name, entityStatus, grantedAt, java.util.Date())
    }

    override suspend fun requestPermission(name: String): Boolean {
        val permission = dao.getByName(name)
        if (permission == null) return false

        // Check current system status
        val currentStatus = if (ContextCompat.checkSelfPermission(context, name) == PackageManager.PERMISSION_GRANTED) {
            DomainPermissionStatus.GRANTED
        } else {
            DomainPermissionStatus.DENIED
        }

        dao.setStatus(name, when (currentStatus) {
            DomainPermissionStatus.GRANTED -> PermissionStatus.GRANTED
            else -> PermissionStatus.REQUESTED
        })

        return currentStatus == DomainPermissionStatus.GRANTED
    }

    override suspend fun initializeDefaultPermissions(): List<Permission> {
        val defaultPermissions = listOf(
            Permission(
                permissionName = "android.permission.CAMERA",
                displayName = "相机",
                rationale = "需要相机权限以拍照或扫描",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.RECORD_AUDIO",
                displayName = "麦克风",
                rationale = "需要麦克风权限以录音",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.ACCESS_FINE_LOCATION",
                displayName = "精确定位",
                rationale = "需要定位权限以获取位置",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.ACCESS_COARSE_LOCATION",
                displayName = "大概定位",
                rationale = "需要定位权限以获取大概位置",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.READ_CONTACTS",
                displayName = "读取通讯录",
                rationale = "需要通讯录权限以搜索联系人",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.WRITE_CONTACTS",
                displayName = "写入通讯录",
                rationale = "需要通讯录权限以添加联系人",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.SEND_SMS",
                displayName = "发送短信",
                rationale = "需要短信权限以发送短信",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.READ_SMS",
                displayName = "读取短信",
                rationale = "需要短信权限以读取短信",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.READ_EXTERNAL_STORAGE",
                displayName = "读取存储",
                rationale = "需要存储权限以读取文件",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.WRITE_EXTERNAL_STORAGE",
                displayName = "写入存储",
                rationale = "需要存储权限以写入文件",
                isRequired = false
            ),
            Permission(
                permissionName = "android.permission.POST_NOTIFICATIONS",
                displayName = "发送通知",
                rationale = "需要通知权限以发送通知",
                isRequired = false
            )
        )

        val results = mutableListOf<Permission>()
        for (perm in defaultPermissions) {
            val existing = dao.getByName(perm.permissionName)
            if (existing == null) {
                val id = dao.insert(perm.toEntity())
                results.add(perm.copy(id = id))
            } else {
                // Update system status
                val currentStatus = if (ContextCompat.checkSelfPermission(context, perm.permissionName) == PackageManager.PERMISSION_GRANTED) {
                    DomainPermissionStatus.GRANTED
                } else {
                    existing.status
                }
                val updated = existing.toDomain().copy(status = currentStatus)
                dao.update(updated.toEntity())
                results.add(updated)
            }
        }
        return results
    }
}
