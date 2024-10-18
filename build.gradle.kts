// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.org.jetbrains.kotlin.serializable) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.room.plugin) apply false
    id("version-catalog")
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
