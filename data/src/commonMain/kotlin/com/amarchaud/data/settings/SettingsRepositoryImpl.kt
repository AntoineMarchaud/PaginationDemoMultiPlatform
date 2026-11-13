package com.amarchaud.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.amarchaud.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import okio.IOException

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val materialYouEnabled: Flow<Boolean> = dataStore.data
        // an unreadable file must not crash the app at launch: the option is just off
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { preferences -> preferences[MATERIAL_YOU] ?: false }
        .distinctUntilChanged()

    override suspend fun setMaterialYouEnabled(enabled: Boolean) {
        dataStore.edit { it[MATERIAL_YOU] = enabled }
    }

    private companion object {
        val MATERIAL_YOU = booleanPreferencesKey("material_you")
    }
}
