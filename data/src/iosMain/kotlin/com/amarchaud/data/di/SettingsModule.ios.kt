package com.amarchaud.data.di

import com.amarchaud.data.settings.SettingsRepositoryImpl
import com.amarchaud.domain.repository.SettingsRepository
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val settingsModule = module {
    single<SettingsRepository> {
        val documents = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )
        SettingsRepositoryImpl(createSettingsDataStore(requireNotNull(documents?.path) + "/" + SETTINGS_FILE_NAME))
    }
}
