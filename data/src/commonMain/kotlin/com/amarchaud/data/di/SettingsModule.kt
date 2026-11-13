package com.amarchaud.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath
import org.koin.core.module.Module

/** Provides the settings DataStore (file location is platform specific) and the SettingsRepository. */
expect val settingsModule: Module

internal const val SETTINGS_FILE_NAME = "settings.preferences_pb"

internal fun createSettingsDataStore(filePath: String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        // a corrupted file is replaced by empty settings instead of failing every read
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { filePath.toPath() },
    )
