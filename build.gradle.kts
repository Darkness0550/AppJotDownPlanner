// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false // Plugin para aplicaciones Android
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false // Plugin para Kotlin en Android
}

// Configuraciones comunes para todos los módulos
allprojects {
    repositories {
    }
}

subprojects {
    // Configuraciones específicas de los subproyectos, si las hubiera
}
