plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.org.jetbrains.kotlin.serializable)
}


kotlin {
    jvmToolchain(21)
    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "domain"
            isStatic = true
            binaryOption("bundleId", "com.amarchaud.domain")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.paging.common)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.paging.testing)
        }
    }
}
