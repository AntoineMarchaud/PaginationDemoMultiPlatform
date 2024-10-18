plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "com.amarchaud.paginationdemokmm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.amarchaud.paginationdemokmm"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(project(":data"))
    implementation(project(":ui"))
    implementation(libs.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
}
