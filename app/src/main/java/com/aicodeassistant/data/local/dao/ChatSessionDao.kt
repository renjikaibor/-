package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ChatSessionEntity): Long

    @Update
    suspend fun update(session: ChatSessionEntity): Int

    @Delete
    suspend fun delete(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM chat_sessions WHERE id = :id")
    suspend fun getById(id: Long): ChatSessionEntity?

    @Query("SELECT * FROM chat_sessions ORDER BY isPinned DESC, updatedAt DESC")
    fun getAll(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions ORDER BY isPinned DESC, updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<ChatSessionEntity>

    @Query("SELECT COUNT(*) FROM chat_sessions")
    suspend fun getCount(): Int

    @Query("UPDATE chat_sessions SET updatedAt = :updatedAt, messageCount = :messageCount WHERE id = :id")
    suspend fun updateMetadata(id: Long, updatedAt: java.util.Date, messageCount: Int)

    @Query("UPDATE chat_sessions SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("UPDATE chat_sessions SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: Long, isPinned: Boolean)

    @Query("DELETE FROM chat_sessions WHERE id NOT IN (SELECT id FROM chat_sessions ORDER BY updatedAt DESC LIMIT :keepCount)")
    suspend fun deleteOldSessions(keepCount: Int)
}
