plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

// Framework "Shared" for the native SwiftUI app (iosAppNative):
// domain exported to Swift, data embedded: Swift writes its own ViewModels. No presentation, no Compose.
// Flows are bridged by hand (Watcher): SKIE 0.10.16 triggers a Gradle deprecation warning.
kotlin {
    jvmToolchain(21)

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "Shared"
            isStatic = true
            binaryOption("bundleId", "com.amarchaud.iosshared")
            export(dependencies.project(":domain"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":domain"))
            implementation(project(":data"))
            implementation(libs.koin.core)
            implementation(libs.paging.common)
        }
        iosTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.paging.testing)
        }
    }
}
