package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.AIModelEntity
import com.aicodeassistant.data.local.entity.ModelType
import kotlinx.coroutines.flow.Flow

@Dao
interface AIModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: AIModelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(models: List<AIModelEntity>): List<Long>

    @Update
    suspend fun update(model: AIModelEntity): Int

    @Delete
    suspend fun delete(model: AIModelEntity)

    @Query("DELETE FROM ai_models WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM ai_models WHERE id = :id")
    suspend fun getById(id: Long): AIModelEntity?

    @Query("SELECT * FROM ai_models WHERE modelId = :modelId")
    suspend fun getByModelId(modelId: String): AIModelEntity?

    @Query("SELECT * FROM ai_models WHERE isDefault = 1 AND isEnabled = 1 LIMIT 1")
    suspend fun getDefaultModel(): AIModelEntity?

    @Query("SELECT * FROM ai_models WHERE type = :type AND isEnabled = 1 ORDER BY createdAt ASC")
    fun getByType(type: ModelType): Flow<List<AIModelEntity>>

    @Query("SELECT * FROM ai_models WHERE isEnabled = 1 ORDER BY type DESC, createdAt ASC")
    fun getAllEnabled(): Flow<List<AIModelEntity>>

    @Query("SELECT * FROM ai_models ORDER BY type DESC, createdAt ASC")
    fun getAll(): Flow<List<AIModelEntity>>

    @Query("UPDATE ai_models SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearDefault()

    @Query("UPDATE ai_models SET isDefault = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setDefault(id: Long, updatedAt: java.util.Date)

    @Query("UPDATE ai_models SET isEnabled = :enabled, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean, updatedAt: java.util.Date)

    @Query("DELETE FROM ai_models WHERE type = :type")
    suspend fun deleteByType(type: ModelType)
}
