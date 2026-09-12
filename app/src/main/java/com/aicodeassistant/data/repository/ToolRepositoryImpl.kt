package com.aicodeassistant.data.repository

import com.aicodeassistant.data.local.AppDatabase
import com.aicodeassistant.data.local.dao.ToolCallLogDao
import com.aicodeassistant.data.local.dao.ToolDao
import com.aicodeassistant.data.local.entity.ToolCallLogEntity
import com.aicodeassistant.data.local.entity.ToolEntity
import com.aicodeassistant.data.local.entity.ToolType
import com.aicodeassistant.domain.model.Tool
import com.aicodeassistant.domain.model.ToolCallLog
import com.aicodeassistant.domain.model.ToolCallStatus
import com.aicodeassistant.domain.model.ToolType as DomainToolType
import com.aicodeassistant.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : ToolRepository {

    private val toolDao: ToolDao = database.toolDao()
    private val logDao: ToolCallLogDao = database.toolCallLogDao()
    private val gson = com.google.gson.Gson()

    private fun ToolEntity.toDomain(): Tool {
        return Tool(
            id = id,
            name = name,
            displayName = displayName,
            version = version,
            description = description,
            type = when (type) {
                ToolType.BUILTIN -> DomainToolType.BUILTIN
                ToolType.INSTALLED -> DomainToolType.INSTALLED
                else -> DomainToolType.CUSTOM
            },
            installCommand = installCommand,
            entryPoint = entryPoint,
            parameters = gson.fromJson(parameters ?: "{}", Map::class.java) ?: emptyMap(),
            permissions = gson.fromJson(permissions ?: "[]", List::class.java) ?: emptyList(),
            isEnabled = isEnabled,
            callCount = callCount,
            lastCalledAt = lastCalledAt,
            installedAt = installedAt,
            updatedAt = updatedAt,
            metadata = gson.fromJson(metadata ?: "{}", Map::class.java) ?: emptyMap()
        )
    }

    private fun Tool.toEntity(): ToolEntity {
        return ToolEntity(
            id = id,
            name = name,
            displayName = displayName,
            version = version,
            description = description,
            type = when (type) {
                DomainToolType.BUILTIN -> ToolType.BUILTIN
                DomainToolType.INSTALLED -> ToolType.INSTALLED
                else -> ToolType.CUSTOM
            },
            installCommand = installCommand,
            entryPoint = entryPoint,
            parameters = gson.toJson(parameters),
            permissions = gson.toJson(permissions),
            isEnabled = isEnabled,
            callCount = callCount,
            lastCalledAt = lastCalledAt,
            installedAt = installedAt,
            updatedAt = updatedAt,
            metadata = gson.toJson(metadata)
        )
    }

    private fun ToolCallLogEntity.toDomain(): ToolCallLog {
        return ToolCallLog(
            id = id,
            toolId = toolId,
            sessionId = sessionId,
            toolName = toolName,
            inputArgs = gson.fromJson(inputArgs, Map::class.java) ?: emptyMap(),
            outputResult = outputResult?.let { gson.fromJson(it, Map::class.java) },
            status = when (status) {
                ToolCallStatus.PENDING -> com.aicodeassistant.domain.model.ToolCallStatus.PENDING
                ToolCallStatus.RUNNING -> com.aicodeassistant.domain.model.ToolCallStatus.RUNNING
                ToolCallStatus.SUCCESS -> com.aicodeassistant.domain.model.ToolCallStatus.SUCCESS
                ToolCallStatus.FAILED -> com.aicodeassistant.domain.model.ToolCallStatus.FAILED
                else -> com.aicodeassistant.domain.model.ToolCallStatus.CANCELLED
            },
            errorMessage = errorMessage,
            durationMs = durationMs,
            createdAt = createdAt
        )
    }

    private fun ToolCallLog.toEntity(): ToolCallLogEntity {
        return ToolCallLogEntity(
            id = id,
            toolId = toolId,
            sessionId = sessionId,
            toolName = toolName,
            inputArgs = gson.toJson(inputArgs),
            outputResult = outputResult?.let { gson.toJson(it) },
            status = when (status) {
                com.aicodeassistant.domain.model.ToolCallStatus.PENDING -> ToolCallStatus.PENDING
                com.aicodeassistant.domain.model.ToolCallStatus.RUNNING -> ToolCallStatus.RUNNING
                com.aicodeassistant.domain.model.ToolCallStatus.SUCCESS -> ToolCallStatus.SUCCESS
                com.aicodeassistant.domain.model.ToolCallStatus.FAILED -> ToolCallStatus.FAILED
                else -> ToolCallStatus.CANCELLED
            },
            errorMessage = errorMessage,
            durationMs = durationMs,
            createdAt = createdAt
        )
    }

    override suspend fun installTool(tool: Tool): Tool {
        val entity = tool.toEntity()
        val id = toolDao.insert(entity)
        return tool.copy(id = id)
    }

    override suspend fun uninstallTool(id: Long): Boolean {
        logDao.deleteByToolId(id)
        return toolDao.deleteById(id) > 0
    }

    override suspend fun uninstallToolByName(name: String): Boolean {
        val tool = toolDao.getByName(name)
        tool?.let { logDao.deleteByToolId(it.id) }
        return toolDao.deleteByName(name) > 0
    }

    override suspend fun updateTool(tool: Tool): Tool {
        val entity = tool.toEntity()
        toolDao.update(entity)
        return tool
    }

    override suspend fun getTool(id: Long): Tool? {
        return toolDao.getById(id)?.toDomain()
    }

    override suspend fun getToolByName(name: String): Tool? {
        return toolDao.getByName(name)?.toDomain()
    }

    override fun observeToolsByType(type: DomainToolType): Flow<List<Tool>> {
        val entityType = when (type) {
            DomainToolType.BUILTIN -> ToolType.BUILTIN
            DomainToolType.INSTALLED -> ToolType.INSTALLED
            else -> ToolType.CUSTOM
        }
        return toolDao.getByType(entityType).map { it.map { it.toDomain() } }
    }

    override fun observeAllEnabledTools(): Flow<List<Tool>> {
        return toolDao.getAllEnabled().map { it.map { it.toDomain() } }
    }

    override fun observeAllTools(): Flow<List<Tool>> {
        return toolDao.getAll().map { it.map { it.toDomain() } }
    }

    override suspend fun incrementCallCount(id: Long): Boolean {
        toolDao.incrementCallCount(id, java.util.Date())
        return true
    }

    override suspend fun setToolEnabled(id: Long, enabled: Boolean): Boolean {
        toolDao.setEnabled(id, enabled, java.util.Date())
        return true
    }

    override suspend fun getEnabledToolsCount(): Int {
        return toolDao.getEnabledCount()
    }

    // Call logs
    override suspend fun addCallLog(log: ToolCallLog): ToolCallLog {
        val entity = log.toEntity()
        val id = logDao.insert(entity)
        return log.copy(id = id)
    }

    override suspend fun updateCallLog(log: ToolCallLog): ToolCallLog {
        val entity = log.toEntity()
        logDao.update(entity)
        return log
    }

    override fun observeCallLogsByTool(toolId: Long, limit: Int, offset: Int): Flow<List<ToolCallLog>> {
        return logDao.getByToolId(toolId, limit, offset).map { it.map { it.toDomain() } }
    }

    override fun observeCallLogsBySession(sessionId: Long): Flow<List<ToolCallLog>> {
        return logDao.getBySessionId(sessionId).map { it.map { it.toDomain() } }
    }

    override suspend fun getRecentCallLogs(limit: Int, offset: Int): List<ToolCallLog> {
        return logDao.getRecent(limit, offset).map { it.toDomain() }
    }

    override suspend fun getCallLogCountByTool(toolId: Long): Int {
        return logDao.getCountByToolId(toolId)
    }

    override suspend fun deleteOldCallLogs(beforeDate: java.util.Date): Int {
        return logDao.deleteOldLogs(beforeDate)
    }
}
