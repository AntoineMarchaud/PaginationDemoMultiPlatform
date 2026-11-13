package com.amarchaud.data.di

import com.amarchaud.data.settings.SettingsRepositoryImpl
import com.amarchaud.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val settingsModule = module {
    single<SettingsRepository> {
        val file = androidContext().filesDir.resolve(SETTINGS_FILE_NAME)
        SettingsRepositoryImpl(createSettingsDataStore(file.absolutePath))
    }
}
