package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<ChatMessageEntity>): List<Long>

    @Update
    suspend fun update(message: ChatMessageEntity): Int

    @Delete
    suspend fun delete(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long): Int

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getById(id: Long): ChatMessageEntity?

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY createdAt ASC")
    fun getBySessionId(sessionId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY createdAt ASC LIMIT :limit OFFSET :offset")
    suspend fun getBySessionIdPaged(sessionId: Long, limit: Int, offset: Int): List<ChatMessageEntity>

    @Query("SELECT COUNT(*) FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun getCountBySessionId(sessionId: Long): Int

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId AND isStreaming = 1 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastStreamingMessage(sessionId: Long): ChatMessageEntity?

    @Query("UPDATE chat_messages SET content = :content, isStreaming = :isStreaming WHERE id = :id")
    suspend fun updateContent(id: Long, content: String, isStreaming: Boolean)

    @Query("UPDATE chat_messages SET isError = :isError WHERE id = :id")
    suspend fun setError(id: Long, isError: Boolean)
}
