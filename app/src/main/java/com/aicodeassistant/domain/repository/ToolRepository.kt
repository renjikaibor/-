package com.aicodeassistant.domain.repository

import com.aicodeassistant.domain.model.Tool
import com.aicodeassistant.domain.model.ToolCallLog
import com.aicodeassistant.domain.model.ToolType
import kotlinx.coroutines.flow.Flow

interface ToolRepository {
    suspend fun installTool(tool: Tool): Tool
    suspend fun uninstallTool(id: Long): Boolean
    suspend fun uninstallToolByName(name: String): Boolean
    suspend fun updateTool(tool: Tool): Tool
    suspend fun getTool(id: Long): Tool?
    suspend fun getToolByName(name: String): Tool?
    fun observeToolsByType(type: ToolType): Flow<List<Tool>>
    fun observeAllEnabledTools(): Flow<List<Tool>>
    fun observeAllTools(): Flow<List<Tool>>
    suspend fun incrementCallCount(id: Long): Boolean
    suspend fun setToolEnabled(id: Long, enabled: Boolean): Boolean
    suspend fun getEnabledToolsCount(): Int

    // Call logs
    suspend fun addCallLog(log: ToolCallLog): ToolCallLog
    suspend fun updateCallLog(log: ToolCallLog): ToolCallLog
    fun observeCallLogsByTool(toolId: Long, limit: Int, offset: Int): Flow<List<ToolCallLog>>
    fun observeCallLogsBySession(sessionId: Long): Flow<List<ToolCallLog>>
    suspend fun getRecentCallLogs(limit: Int, offset: Int): List<ToolCallLog>
    suspend fun getCallLogCountByTool(toolId: Long): Int
    suspend fun deleteOldCallLogs(beforeDate: java.util.Date): Int
}
