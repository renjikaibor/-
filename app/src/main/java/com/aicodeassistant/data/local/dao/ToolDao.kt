package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.ToolEntity
import com.aicodeassistant.data.local.entity.ToolType
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tool: ToolEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tools: List<ToolEntity>): List<Long>

    @Update
    suspend fun update(tool: ToolEntity): Int

    @Delete
    suspend fun delete(tool: ToolEntity)

    @Query("DELETE FROM tools WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM tools WHERE name = :name")
    suspend fun deleteByName(name: String): Int

    @Query("SELECT * FROM tools WHERE id = :id")
    suspend fun getById(id: Long): ToolEntity?

    @Query("SELECT * FROM tools WHERE name = :name")
    suspend fun getByName(name: String): ToolEntity?

    @Query("SELECT * FROM tools WHERE type = :type AND isEnabled = 1 ORDER BY callCount DESC, installedAt DESC")
    fun getByType(type: ToolType): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE isEnabled = 1 ORDER BY callCount DESC, installedAt DESC")
    fun getAllEnabled(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools ORDER BY callCount DESC, installedAt DESC")
    fun getAll(): Flow<List<ToolEntity>>

    @Query("UPDATE tools SET callCount = callCount + 1, lastCalledAt = :lastCalledAt WHERE id = :id")
    suspend fun incrementCallCount(id: Long, lastCalledAt: java.util.Date)

    @Query("UPDATE tools SET isEnabled = :enabled, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean, updatedAt: java.util.Date)

    @Query("SELECT COUNT(*) FROM tools WHERE isEnabled = 1")
    suspend fun getEnabledCount(): Int
}
