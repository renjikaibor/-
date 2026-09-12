package com.aicodeassistant.data.repository

import com.aicodeassistant.data.local.AppDatabase
import com.aicodeassistant.data.local.dao.ChatMessageDao
import com.aicodeassistant.data.local.dao.ChatSessionDao
import com.aicodeassistant.data.local.entity.ChatMessageEntity
import com.aicodeassistant.data.local.entity.ChatSessionEntity
import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.ChatSession
import com.aicodeassistant.domain.model.MessageRole
import com.aicodeassistant.domain.repository.ChatRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : ChatRepository {

    private val sessionDao: ChatSessionDao = database.chatSessionDao()
    private val messageDao: ChatMessageDao = database.chatMessageDao()
    private val gson = Gson()

    private fun ChatSessionEntity.toDomain(): ChatSession {
        return ChatSession(
            id = id,
            title = title,
            modelId = modelId,
            systemPrompt = systemPrompt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            messageCount = messageCount,
            isPinned = isPinned,
            metadata = gson.fromJson(metadata ?: "{}", Map::class.java) ?: emptyMap()
        )
    }

    private fun ChatSession.toEntity(): ChatSessionEntity {
        return ChatSessionEntity(
            id = id,
            title = title,
            modelId = modelId,
            systemPrompt = systemPrompt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            messageCount = messageCount,
            isPinned = isPinned,
            metadata = gson.toJson(metadata)
        )
    }

    private fun ChatMessageEntity.toDomain(): ChatMessage {
        val toolCalls = gson.fromJson(toolCalls ?: "[]", ToolCallsType::class.java) ?: emptyList()
        val images = gson.fromJson(images ?: "[]", ImagesType::class.java) ?: emptyList()
        val metadata = gson.fromJson(metadata ?: "{}", MetadataType::class.java) ?: emptyMap()
        return ChatMessage(
            id = id,
            sessionId = sessionId,
            role = role,
            content = content,
            toolCalls = toolCalls,
            toolCallId = toolCallId,
            images = images,
            metadata = metadata,
            createdAt = createdAt,
            isStreaming = isStreaming,
            isError = isError
        )
    }

    private fun ChatMessage.toEntity(): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            sessionId = sessionId,
            role = role,
            content = content,
            toolCalls = gson.toJson(toolCalls),
            toolCallId = toolCallId,
            images = gson.toJson(images),
            metadata = gson.toJson(metadata),
            createdAt = createdAt,
            isStreaming = isStreaming,
            isError = isError
        )
    }

    // Type tokens for Gson
    private class ToolCallsType : com.google.gson.reflect.TypeToken<List<com.aicodeassistant.domain.model.ToolCall>>() {}
    private class ImagesType : com.google.gson.reflect.TypeToken<List<String>>() {}
    private class MetadataType : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}

    override suspend fun createSession(session: ChatSession): ChatSession {
        val entity = session.toEntity()
        val id = sessionDao.insert(entity)
        return session.copy(id = id)
    }

    override suspend fun updateSession(session: ChatSession): ChatSession {
        val entity = session.toEntity()
        sessionDao.update(entity)
        return session
    }

    override suspend fun deleteSession(id: Long): Boolean {
        return sessionDao.deleteById(id) > 0
    }

    override suspend fun getSession(id: Long): ChatSession? {
        return sessionDao.getById(id)?.toDomain()
    }

    override fun observeSessions(): Flow<List<ChatSession>> {
        return sessionDao.getAll().map { it.map { it.toDomain() } }
    }

    override suspend fun getSessions(limit: Int, offset: Int): List<ChatSession> {
        return sessionDao.getPaged(limit, offset).map { it.toDomain() }
    }

    override suspend fun getSessionCount(): Int {
        return sessionDao.getCount()
    }

    override suspend fun pinSession(id: Long, pinned: Boolean) {
        sessionDao.setPinned(id, pinned)
    }

    override suspend fun updateSessionTitle(id: Long, title: String) {
        sessionDao.updateTitle(id, title)
    }

    override suspend fun addMessage(message: ChatMessage): ChatMessage {
        val entity = message.toEntity()
        val id = messageDao.insert(entity)
        return message.copy(id = id)
    }

    override suspend fun addMessages(messages: List<ChatMessage>): List<ChatMessage> {
        val entities = messages.map { it.toEntity() }
        val ids = messageDao.insertAll(entities)
        return messages.mapIndexed { index, msg -> msg.copy(id = ids[index]) }
    }

    override suspend fun updateMessage(message: ChatMessage): ChatMessage {
        val entity = message.toEntity()
        messageDao.update(entity)
        return message
    }

    override suspend fun deleteMessage(id: Long): Boolean {
        return messageDao.deleteById(id) > 0
    }

    override suspend fun deleteMessagesBySession(sessionId: Long): Int {
        return messageDao.deleteBySessionId(sessionId)
    }

    override fun observeMessages(sessionId: Long): Flow<List<ChatMessage>> {
        return messageDao.getBySessionId(sessionId).map { it.map { it.toDomain() } }
    }

    override suspend fun getMessages(sessionId: Long, limit: Int, offset: Int): List<ChatMessage> {
        return messageDao.getBySessionIdPaged(sessionId, limit, offset).map { it.toDomain() }
    }

    override suspend fun getMessageCount(sessionId: Long): Int {
        return messageDao.getCountBySessionId(sessionId)
    }

    override suspend fun getLastStreamingMessage(sessionId: Long): ChatMessage? {
        return messageDao.getLastStreamingMessage(sessionId)?.toDomain()
    }

    override suspend fun updateMessageContent(id: Long, content: String, isStreaming: Boolean) {
        messageDao.updateContent(id, content, isStreaming)
    }

    override suspend fun setMessageError(id: Long, isError: Boolean) {
        messageDao.setError(id, isError)
    }
}
