package com.aicodeassistant

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.aicodeassistant.data.local.AppDatabase
import com.aicodeassistant.data.local.dao.*
import com.aicodeassistant.data.repository.*
import com.aicodeassistant.di.AppModule
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import javax.inject.Inject

@HiltAndroidApp
class AICodeApplication : Application() {

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getAppComponent(context: Context): AppComponent {
            return EntryPointAccessors.fromApplication(context, AppComponent::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize ProcessLifecycleOwner for lifecycle-aware components
        ProcessLifecycleOwner.get()
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppComponent {
    val chatRepository: ChatRepository
    val modelRepository: ModelRepository
    val toolRepository: ToolRepository
    val fileRepository: FileRepository
    val settingsRepository: SettingsRepository
    val permissionRepository: PermissionRepository
}
