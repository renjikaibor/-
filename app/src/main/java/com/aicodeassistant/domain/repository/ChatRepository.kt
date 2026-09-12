package com.aicodeassistant.domain.repository

import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.ChatSession
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    // Sessions
    suspend fun createSession(session: ChatSession): ChatSession
    suspend fun updateSession(session: ChatSession): ChatSession
    suspend fun deleteSession(id: Long): Boolean
    suspend fun getSession(id: Long): ChatSession?
    fun observeSessions(): Flow<List<ChatSession>>
    suspend fun getSessions(limit: Int, offset: Int): List<ChatSession>
    suspend fun getSessionCount(): Int
    suspend fun pinSession(id: Long, pinned: Boolean)
    suspend fun updateSessionTitle(id: Long, title: String)

    // Messages
    suspend fun addMessage(message: ChatMessage): ChatMessage
    suspend fun addMessages(messages: List<ChatMessage>): List<ChatMessage>
    suspend fun updateMessage(message: ChatMessage): ChatMessage
    suspend fun deleteMessage(id: Long): Boolean
    suspend fun deleteMessagesBySession(sessionId: Long): Int
    fun observeMessages(sessionId: Long): Flow<List<ChatMessage>>
    suspend fun getMessages(sessionId: Long, limit: Int, offset: Int): List<ChatMessage>
    suspend fun getMessageCount(sessionId: Long): Int
    suspend fun getLastStreamingMessage(sessionId: Long): ChatMessage?
    suspend fun updateMessageContent(id: Long, content: String, isStreaming: Boolean)
    suspend fun setMessageError(id: Long, isError: Boolean)
}
