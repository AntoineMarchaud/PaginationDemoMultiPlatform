plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.multiplatform.library)
    alias(libs.plugins.org.jetbrains.kotlin.serializable)
    alias(libs.plugins.room.plugin)
    alias(libs.plugins.com.google.devtools.ksp)
}

kotlin {
    jvmToolchain(21)

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
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "data"
            isStatic = true
            // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }


    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)

            implementation(libs.koin.android)
            implementation(libs.koin.compose)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.stately.common)
            implementation(libs.stately.isolate)
            implementation(libs.stately.iso.collection)
        }

        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.koin.core)

            implementation(libs.ktor.core)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.content)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)

            implementation(libs.paging.common)

            implementation(libs.kotlinx.datetime)

            implementation(libs.sqliteBundled)
            implementation(libs.roomRuntime)
            implementation(libs.roomPaging)
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

