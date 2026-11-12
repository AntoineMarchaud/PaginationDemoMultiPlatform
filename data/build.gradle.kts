plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.multiplatform.library)
    alias(libs.plugins.org.jetbrains.kotlin.serializable)
    alias(libs.plugins.room.plugin)
    alias(libs.plugins.com.google.devtools.ksp)
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        // Room KMP generates an `expect object` constructor for the database
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    android {
        namespace = "com.amarchaud.data"
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
            baseName = "data"
            isStatic = true
            binaryOption("bundleId", "com.amarchaud.data")
            // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }


    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)

            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.koin.core)

            implementation(libs.ktor.core)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.content)

            implementation(libs.paging.common)

            implementation(libs.kotlinx.datetime)

            implementation(libs.sqliteBundled)
            implementation(libs.roomRuntime)
            implementation(libs.roomPaging)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.paging.testing)
        }
    }
}

dependencies {
    add("kspAndroid", libs.roomCompiler)
    add("kspIosSimulatorArm64", libs.roomCompiler)
    add("kspIosArm64", libs.roomCompiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

