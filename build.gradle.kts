// Top-level build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.secrets.gradle.plugin) apply false
}

val compileSdkVersion by extra(35)
val minSdkVersion by extra(24)
val targetSdkVersion by extra(35)
