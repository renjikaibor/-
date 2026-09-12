package com.aicodeassistant.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun putString(key: String, value: String): Boolean
    suspend fun getString(key: String, defaultValue: String?): String?
    suspend fun putBoolean(key: String, value: Boolean): Boolean
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean
    suspend fun putInt(key: String, value: Int): Boolean
    suspend fun getInt(key: String, defaultValue: Int): Int
    suspend fun putLong(key: String, value: Long): Boolean
    suspend fun getLong(key: String, defaultValue: Long): Long
    suspend fun remove(key: String): Boolean
    suspend fun clear(): Boolean
    fun observeString(key: String): Flow<String?>
    fun observeBoolean(key: String): Flow<Boolean>
    fun observeInt(key: String): Flow<Int>
}
