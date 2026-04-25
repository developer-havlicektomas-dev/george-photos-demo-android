plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.junit5)
}

android {
    namespace = "dev.havlicektomas.photosapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "dev.havlicektomas.photosapp"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // AndroidX core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose (BOM)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowsizeclass)

    // Navigation (type-safe routes)
    implementation(libs.androidx.navigation.compose)

    // Kotlinx
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // Networking (Ktor + CIO engine)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Image loading (Coil 3 with Ktor fetcher)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    // DI (Koin)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Logging
    implementation(libs.timber)

    // Unit tests (JUnit 5 + Turbine + AssertK + coroutines-test)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.turbine)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests (kept for existing scaffolding; not actively used)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Debug-only
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
