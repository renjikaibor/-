package com.aicodeassistant.domain.repository

import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ModelType
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    suspend fun addModel(model: AIModel): AIModel
    suspend fun updateModel(model: AIModel): AIModel
    suspend fun deleteModel(id: Long): Boolean
    suspend fun getModel(id: Long): AIModel?
    suspend fun getModelByModelId(modelId: String): AIModel?
    suspend fun getDefaultModel(): AIModel?
    fun observeModelsByType(type: ModelType): Flow<List<AIModel>>
    fun observeAllEnabledModels(): Flow<List<AIModel>>
    fun observeAllModels(): Flow<List<AIModel>>
    suspend fun setDefaultModel(id: Long): Boolean
    suspend fun setModelEnabled(id: Long, enabled: Boolean): Boolean
    suspend fun initializeBuiltinModels(): List<AIModel>
}
