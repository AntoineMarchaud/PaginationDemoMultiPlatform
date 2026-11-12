plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.org.jetbrains.kotlin.serializable)
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "com.amarchaud.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        kotlin.sourceSets.configureEach {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        androidResources {
            enable = true
        }

        withHostTest {}
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ui"
            isStatic = true
            binaryOption("bundleId", "com.amarchaud.ui")
        }
    }


    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.coil)
            implementation(libs.coil.network.ktor)

            // common google
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.lifecycle.runtime.compose)

            implementation(libs.paging.compose)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.paging.testing)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.ui.tooling)
}
