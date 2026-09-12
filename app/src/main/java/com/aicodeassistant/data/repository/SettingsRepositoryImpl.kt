package com.aicodeassistant.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.getString
import androidx.datastore.preferences.core.mutations
import androidx.datastore.preferences.core.preferencesDataStore
import androidx.datastore.preferences.rxjava3.getBoolean
import androidx.datastore.preferences.rxjava3.getInt
import androidx.datastore.preferences.rxjava3.getLong
import androidx.datastore.preferences.rxjava3.getString
import androidx.datastore.preferences.rxjava3.getAll
import androidx.datastore.preferences.rxjava3.toFlow
import androidx.datastore.rxjava3.DataStore
import com.aicodeassistant.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

    private val stringKey = { key: String -> PreferencesKeys.stringKey(key) }
    private val booleanKey = { key: String -> PreferencesKeys.booleanKey(key) }
    private val intKey = { key: String -> PreferencesKeys.intKey(key) }
    private val longKey = { key: String -> PreferencesKeys.longKey(key) }

    override suspend fun putString(key: String, value: String): Boolean {
        return try {
            context.dataStore.edit { it[stringKey(key)] = value }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getString(key: String, defaultValue: String?): String? {
        return try {
            context.dataStore.data
                .map { it[stringKey(key)] ?: defaultValue }
                .firstOrNull()
                .await() ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean): Boolean {
        return try {
            context.dataStore.edit { it[booleanKey(key)] = value }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return try {
            context.dataStore.data
                .map { it[booleanKey(key)] ?: defaultValue }
                .firstOrNull()
                .await() ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun putInt(key: String, value: Int): Boolean {
        return try {
            context.dataStore.edit { it[intKey(key)] = value }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int {
        return try {
            context.dataStore.data
                .map { it[intKey(key)] ?: defaultValue }
                .firstOrNull()
                .await() ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun putLong(key: String, value: Long): Boolean {
        return try {
            context.dataStore.edit { it[longKey(key)] = value }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getLong(key: String, defaultValue: Long): Long {
        return try {
            context.dataStore.data
                .map { it[longKey(key)] ?: defaultValue }
                .firstOrNull()
                .await() ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun remove(key: String): Boolean {
        return try {
            context.dataStore.edit { it.remove(stringKey(key)) }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clear(): Boolean {
        return try {
            context.dataStore.edit { it.clear() }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun observeString(key: String): Flow<String?> {
        return context.dataStore.data
            .map { it[stringKey(key)] }
            .toFlow()
    }

    override fun observeBoolean(key: String): Flow<Boolean> {
        return context.dataStore.data
            .map { it[booleanKey(key)] ?: false }
            .toFlow()
    }

    override fun observeInt(key: String): Flow<Int> {
        return context.dataStore.data
            .map { it[intKey(key)] ?: 0 }
            .toFlow()
    }
}
