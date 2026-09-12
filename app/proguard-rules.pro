# Keep Room database
-keep class com.aicodeassistant.data.local.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep Retrofit/Moshi
-keep class com.aicodeassistant.data.remote.** { *; }
-keep class * implements retrofit2.Call { *; }
-keep class * implements retrofit2.http.* { *; }

# Keep Moshi JSON adapters
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class *
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}

# Keep Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep Coil
-keep class coil3.** { *; }

# Keep Markwon
-keep class io.noties.markwon.** { *; }

# Keep WebView
-keep class android.webkit.** { *; }

# Keep Encryption
-keep class androidx.security.crypto.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep WorkManager
-keep class androidx.work.** { *; }

# Keep Gson (if used)
-keep class com.google.gson.** { *; }

# Keep Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }

# Keep our model classes
-keep class com.aicodeassistant.domain.model.** { *; }
-keep class com.aicodeassistant.data.local.entity.** { *; }

# Don't warn about missing classes
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn kotlinx.coroutines.**
-dontwarn androidx.compose.**
-dontwarn com.squareup.moshi.**
-dontwarn io.noties.markwon.**
-dontwarn coil3.**
