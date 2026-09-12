package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.PermissionEntity
import com.aicodeassistant.data.local.entity.PermissionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PermissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(permission: PermissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(permissions: List<PermissionEntity>): List<Long>

    @Update
    suspend fun update(permission: PermissionEntity): Int

    @Delete
    suspend fun delete(permission: PermissionEntity)

    @Query("DELETE FROM permissions WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM permissions WHERE id = :id")
    suspend fun getById(id: Long): PermissionEntity?

    @Query("SELECT * FROM permissions WHERE permissionName = :name")
    suspend fun getByName(name: String): PermissionEntity?

    @Query("SELECT * FROM permissions WHERE status = :status")
    fun getByStatus(status: PermissionStatus): Flow<List<PermissionEntity>>

    @Query("SELECT * FROM permissions ORDER BY permissionName ASC")
    fun getAll(): Flow<List<PermissionEntity>>

    @Query("UPDATE permissions SET status = :status, grantedAt = :grantedAt, lastRequestedAt = :lastRequestedAt, requestCount = requestCount + 1 WHERE permissionName = :name")
    suspend fun updateStatus(name: String, status: PermissionStatus, grantedAt: java.util.Date?, lastRequestedAt: java.util.Date)

    @Query("UPDATE permissions SET status = :status WHERE permissionName = :name")
    suspend fun setStatus(name: String, status: PermissionStatus)
}
