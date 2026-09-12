package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: SettingsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(settings: List<SettingsEntity>): List<Long>

    @Update
    suspend fun update(setting: SettingsEntity): Int

    @Delete
    suspend fun delete(setting: SettingsEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteByKey(key: String): Int

    @Query("SELECT * FROM settings WHERE key = :key")
    suspend fun getByKey(key: String): SettingsEntity?

    @Query("SELECT * FROM settings")
    fun getAll(): Flow<List<SettingsEntity>>

    @Query("SELECT value FROM settings WHERE key = :key")
    suspend fun getValue(key: String): String?

    @RawQuery(observedEntities = [SettingsEntity::class])
    fun observeValue(key: String): Flow<String?>
}
