plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.aicodeassistant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aicodeassistant"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = java.util.Properties()
                keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }


    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE,LICENSE.txt,NOTICE,NOTICE.txt}"
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    namespace = "com.aicodeassistant"
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.2")
    implementation("androidx.navigation:navigation-compose:2.8.1")

    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.2")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")

    // Coil Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")

    // Markdown Rendering
    implementation("io.noties.markwon:markwon:4.6.4")
    implementation("io.noties.markwon:markwon-ext-tables:4.6.4")
    implementation("io.noties.markwon:markwon-ext-tasklist:4.6.4")
    implementation("io.noties.markwon:markwon-html:4.6.4")
    implementation("io.noties.markwon:markwon-image-glide:4.6.4")
    implementation("io.noties.markwon:markwon-ext-strikethrough:4.6.4")
    // Code highlighting
    implementation("io.noties.markwon:markwon-ext-highlightjs:4.6.4")

    // WebView for Code Editor
    implementation("androidx.webkit:webkit:1.8.0")

    // Security & Encrypted Storage
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // DataStore for Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Permissions
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.10")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.10")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.6.10")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.10")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.6.10")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
