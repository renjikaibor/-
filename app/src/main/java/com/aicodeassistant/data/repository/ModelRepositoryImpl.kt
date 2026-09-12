package com.aicodeassistant.data.repository

import com.aicodeassistant.data.local.AppDatabase
import com.aicodeassistant.data.local.dao.AIModelDao
import com.aicodeassistant.data.local.entity.AIModelEntity
import com.aicodeassistant.data.local.entity.ModelType
import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ModelType as DomainModelType
import com.aicodeassistant.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : ModelRepository {

    private val dao: AIModelDao = database.aiModelDao()

    private fun AIModelEntity.toDomain(): AIModel {
        return AIModel(
            id = id,
            name = name,
            modelId = modelId,
            baseUrl = baseUrl,
            apiKey = apiKey,
            type = when (type) {
                ModelType.BUILTIN -> DomainModelType.BUILTIN
                else -> DomainModelType.CUSTOM
            },
            maxTokens = maxTokens,
            temperature = temperature,
            topP = topP,
            isStream = isStream,
            supportsVision = supportsVision,
            supportsThinking = supportsThinking,
            extraParams = com.google.gson.Gson().fromJson(extraParams ?: "{}", Map::class.java) ?: emptyMap(),
            isDefault = isDefault,
            isEnabled = isEnabled,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun AIModel.toEntity(): AIModelEntity {
        return AIModelEntity(
            id = id,
            name = name,
            modelId = modelId,
            baseUrl = baseUrl,
            apiKey = apiKey,
            type = when (type) {
                DomainModelType.BUILTIN -> ModelType.BUILTIN
                else -> ModelType.CUSTOM
            },
            maxTokens = maxTokens,
            temperature = temperature,
            topP = topP,
            isStream = isStream,
            supportsVision = supportsVision,
            supportsThinking = supportsThinking,
            extraParams = com.google.gson.Gson().toJson(extraParams),
            isDefault = isDefault,
            isEnabled = isEnabled,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    override suspend fun addModel(model: AIModel): AIModel {
        val entity = model.toEntity()
        val id = dao.insert(entity)
        return model.copy(id = id)
    }

    override suspend fun updateModel(model: AIModel): AIModel {
        val entity = model.toEntity()
        dao.update(entity)
        return model
    }

    override suspend fun deleteModel(id: Long): Boolean {
        return dao.deleteById(id) > 0
    }

    override suspend fun getModel(id: Long): AIModel? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getModelByModelId(modelId: String): AIModel? {
        return dao.getByModelId(modelId)?.toDomain()
    }

    override suspend fun getDefaultModel(): AIModel? {
        return dao.getDefaultModel()?.toDomain()
    }

    override fun observeModelsByType(type: DomainModelType): Flow<List<AIModel>> {
        val entityType = when (type) {
            DomainModelType.BUILTIN -> ModelType.BUILTIN
            else -> ModelType.CUSTOM
        }
        return dao.getByType(entityType).map { it.map { it.toDomain() } }
    }

    override fun observeAllEnabledModels(): Flow<List<AIModel>> {
        return dao.getAllEnabled().map { it.map { it.toDomain() } }
    }

    override fun observeAllModels(): Flow<List<AIModel>> {
        return dao.getAll().map { it.map { it.toDomain() } }
    }

    override suspend fun setDefaultModel(id: Long): Boolean {
        dao.clearDefault()
        dao.setDefault(id, java.util.Date())
        return true
    }

    override suspend fun setModelEnabled(id: Long, enabled: Boolean): Boolean {
        dao.setEnabled(id, enabled, java.util.Date())
        return true
    }

    override suspend fun initializeBuiltinModels(): List<AIModel> {
        val builtinModels = listOf(
            AIModel(
                name = "Kimi K3 (视觉)",
                modelId = "moonshotai/kimi-k3",
                baseUrl = "https://integrate.api.nvidia.com/v1",
                apiKey = "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",
                type = DomainModelType.BUILTIN,
                maxTokens = 16384,
                temperature = 1.0f,
                topP = 0.95f,
                isStream = true,
                supportsVision = true,
                supportsThinking = false,
                extraParams = mapOf("reasoning_effort" to "max", "seed" to 0),
                isDefault = true,
                isEnabled = true
            ),
            AIModel(
                name = "DeepSeek V4 Pro",
                modelId = "deepseek-ai/deepseek-v4-pro-0813",
                baseUrl = "https://integrate.api.nvidia.com/v1",
                apiKey = "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",
                type = DomainModelType.BUILTIN,
                maxTokens = 16384,
                temperature = 1.0f,
                topP = 0.95f,
                isStream = true,
                supportsVision = false,
                supportsThinking = false,
                extraParams = mapOf("seed" to 42),
                isDefault = false,
                isEnabled = true
            ),
            AIModel(
                name = "DeepSeek V4 Flash",
                modelId = "deepseek-ai/deepseek-v4-flash-0731",
                baseUrl = "https://integrate.api.nvidia.com/v1",
                apiKey = "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",
                type = DomainModelType.BUILTIN,
                maxTokens = 16384,
                temperature = 1.0f,
                topP = 0.95f,
                isStream = true,
                supportsVision = false,
                supportsThinking = true,
                extraParams = emptyMap(),
                isDefault = false,
                isEnabled = true
            ),
            AIModel(
                name = "Nemotron 3 Super 120B",
                modelId = "nvidia/nemotron-3-super-120b-a12b",
                baseUrl = "https://integrate.api.nvidia.com/v1",
                apiKey = "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",
                type = DomainModelType.BUILTIN,
                maxTokens = 16384,
                temperature = 1.0f,
                topP = 0.95f,
                isStream = true,
                supportsVision = false,
                supportsThinking = true,
                extraParams = mapOf("chat_template_kwargs" to mapOf("enable_thinking" to true)),
                isDefault = false,
                isEnabled = true
            ),
            AIModel(
                name = "Nemotron 3 Ultra 550B",
                modelId = "nvidia/nemotron-3-ultra-550b-a55b",
                baseUrl = "https://integrate.api.nvidia.com/v1",
                apiKey = "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",
                type = DomainModelType.BUILTIN,
                maxTokens = 16384,
                temperature = 1.0f,
                topP = 0.95f,
                isStream = true,
                supportsVision = false,
                supportsThinking = false,
                extraParams = emptyMap(),
                isDefault = false,
                isEnabled = true
            )
        )

        val results = mutableListOf<AIModel>()
        for (model in builtinModels) {
            val existing = dao.getByModelId(model.modelId)
            if (existing == null) {
                val id = dao.insert(model.toEntity())
                results.add(model.copy(id = id))
            } else {
                // Update existing builtin model with latest config
                val updated = model.copy(id = existing.id, isDefault = existing.isDefault)
                dao.update(updated.toEntity())
                results.add(updated)
            }
        }
        return results
    }
}
