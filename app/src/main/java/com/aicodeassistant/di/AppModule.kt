package com.aicodeassistant.di

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.aicodeassistant.data.local.AppDatabase
import com.aicodeassistant.data.local.dao.*
import com.aicodeassistant.data.repository.*
import com.aicodeassistant.domain.repository.*
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "aicodeassistant.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideChatSessionDao(database: AppDatabase): ChatSessionDao {
        return database.chatSessionDao()
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(database: AppDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideAIModelDao(database: AppDatabase): AIModelDao {
        return database.aiModelDao()
    }

    @Provides
    @Singleton
    fun provideToolDao(database: AppDatabase): ToolDao {
        return database.toolDao()
    }

    @Provides
    @Singleton
    fun provideToolCallLogDao(database: AppDatabase): ToolCallLogDao {
        return database.toolCallLogDao()
    }

    @Provides
    @Singleton
    fun provideFileDao(database: AppDatabase): FileDao {
        return database.fileDao()
    }

    @Provides
    @Singleton
    fun providePermissionDao(database: AppDatabase): PermissionDao {
        return database.permissionDao()
    }

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        database: AppDatabase
    ): ChatRepository {
        return ChatRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideModelRepository(
        database: AppDatabase
    ): ModelRepository {
        return ModelRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideToolRepository(
        database: AppDatabase
    ): ToolRepository {
        return ToolRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideFileRepository(
        database: AppDatabase
    ): FileRepository {
        return FileRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun providePermissionRepository(
        database: AppDatabase,
        @ApplicationContext context: Context
    ): PermissionRepository {
        return PermissionRepositoryImpl(database, context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}
