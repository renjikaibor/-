package com.aicodeassistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aicodeassistant.data.local.converters.Converters
import com.aicodeassistant.data.local.dao.*
import com.aicodeassistant.data.local.entity.*

@Database(
    entities = [
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        AIModelEntity::class,
        ToolEntity::class,
        ToolCallLogEntity::class,
        FileEntity::class,
        PermissionEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun aiModelDao(): AIModelDao
    abstract fun toolDao(): ToolDao
    abstract fun toolCallLogDao(): ToolCallLogDao
    abstract fun fileDao(): FileDao
    abstract fun permissionDao(): PermissionDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aicodeassistant.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
