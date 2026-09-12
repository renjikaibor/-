package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.ToolCallLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolCallLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ToolCallLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<ToolCallLogEntity>): List<Long>

    @Update
    suspend fun update(log: ToolCallLogEntity): Int

    @Delete
    suspend fun delete(log: ToolCallLogEntity)

    @Query("DELETE FROM tool_call_logs WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM tool_call_logs WHERE toolId = :toolId")
    suspend fun deleteByToolId(toolId: Long): Int

    @Query("DELETE FROM tool_call_logs WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long): Int

    @Query("SELECT * FROM tool_call_logs WHERE id = :id")
    suspend fun getById(id: Long): ToolCallLogEntity?

    @Query("SELECT * FROM tool_call_logs WHERE toolId = :toolId ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getByToolId(toolId: Long, limit: Int, offset: Int): Flow<List<ToolCallLogEntity>>

    @Query("SELECT * FROM tool_call_logs WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun getBySessionId(sessionId: Long): Flow<List<ToolCallLogEntity>>

    @Query("SELECT * FROM tool_call_logs ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getRecent(limit: Int, offset: Int): List<ToolCallLogEntity>

    @Query("SELECT COUNT(*) FROM tool_call_logs WHERE toolId = :toolId")
    suspend fun getCountByToolId(toolId: Long): Int

    @Query("DELETE FROM tool_call_logs WHERE createdAt < :beforeDate")
    suspend fun deleteOldLogs(beforeDate: java.util.Date): Int
}
