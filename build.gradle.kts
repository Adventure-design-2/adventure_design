// Top-level build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.secrets.gradle.plugin) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false

}

val compileSdkVersion by extra(35)
val minSdkVersion by extra(24)
val targetSdkVersion by extra(35)
