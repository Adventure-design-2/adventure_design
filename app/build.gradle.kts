plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "1.9.10"
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.myadventure"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myadventure"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }


        buildFeatures {
            compose = true
            buildConfig = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.3"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        kotlinOptions {
            jvmTarget = "1.8"
        }

        configurations.all {
            resolutionStrategy {
                force("org.jetbrains:annotations:23.0.0")
            }
        }

        configurations.implementation {
            exclude(group = "com.intellij", module = "annotations")
        }
    }
}

dependencies {
    // Core AndroidX dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coil (이미지 로딩 라이브러리)
    implementation(libs.coil.compose)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // AndroidSVG 라이브러리
    implementation(libs.androidsvg)

    // Ktor (Network & Serialization)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)

    // Firebase
    implementation(platform(libs.firebase.bom)) // Firebase BOM
    implementation(libs.com.google.firebase.firebase.auth.ktx)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.google.firebase.analytics.ktx)
    implementation(libs.play.services.auth) // 명시적 선언
    implementation(libs.firebase.auth.ktx.v2211)
    implementation(libs.com.google.firebase.firebase.firestore.ktx)
    implementation(libs.firebase.database.ktx.v2032)
    implementation(libs.google.play.services.auth)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.firebase.database.ktx)
    kapt(libs.androidx.room.compiler)
    // Gson
    implementation(libs.gson.v2110)

    // OkHttp
    implementation(libs.okhttp)

    // Lifecycle 및 ViewModel 관련 Compose 통합
    implementation(libs.androidx.lifecycle.runtime.compose.v261)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Material Icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended.v150)

    // 코루틴 라이브러리 추가
    implementation(libs.kotlinx.coroutines.android)

    // Generative AI
    implementation(libs.generativeai)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)


}


