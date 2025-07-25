@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

true // Needed to make the Suppress annotation work