package com.amarchaud.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import okio.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryImplTest {

    private val dataStore = FakePreferencesDataStore()

    @Test
    fun materialYouDisabledByDefault() = runTest {
        assertFalse(SettingsRepositoryImpl(dataStore).materialYouEnabled.first())
    }

    @Test
    fun unreadableSettingsFallBackToDisabled() = runTest {
        // a disk error must not crash the app at launch: the option is just off
        val unreadable = object : DataStore<Preferences> {
            override val data: Flow<Preferences> = flow { throw IOException("disk error") }
            override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
                throw IOException("disk error")
        }

        assertFalse(SettingsRepositoryImpl(unreadable).materialYouEnabled.first())
    }

    @Test
    fun materialYouIsPersisted() = runTest {
        SettingsRepositoryImpl(dataStore).setMaterialYouEnabled(true)

        // a new instance (next app launch) reads the stored value
        assertTrue(SettingsRepositoryImpl(dataStore).materialYouEnabled.first())
    }

    @Test
    fun changesAreEmitted() = runTest {
        val repository = SettingsRepositoryImpl(dataStore)
        val emitted = mutableListOf<Boolean>()
        backgroundScope.launch { repository.materialYouEnabled.collect { emitted += it } }
        runCurrent()

        repository.setMaterialYouEnabled(true)
        runCurrent()
        repository.setMaterialYouEnabled(false)
        runCurrent()

        assertEquals(listOf(false, true, false), emitted)
    }
}
