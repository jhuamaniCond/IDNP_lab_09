// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // KSP: procesador de símbolos Kotlin necesario para que Room genere
    // el código de implementación (DAOs, Database) en tiempo de compilación.
    alias(libs.plugins.ksp) apply false
}