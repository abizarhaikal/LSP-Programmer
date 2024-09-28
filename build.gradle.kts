// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies { // Sesuaikan versi Gradle sesuai kebutuhan
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.1") // Tambahkan ini
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}