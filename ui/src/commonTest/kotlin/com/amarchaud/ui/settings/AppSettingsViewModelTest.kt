package com.amarchaud.ui.settings

import com.amarchaud.domain.repository.SettingsRepository
import com.amarchaud.domain.usecase.ObserveMaterialYouUseCase
import com.amarchaud.domain.usecase.SetMaterialYouUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AppSettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    /** Emits only what the test (or a toggle) pushes: lets us observe the "not loaded yet" state. */
    private class ControlledSettingsRepository : SettingsRepository {
        override val materialYouEnabled = MutableSharedFlow<Boolean>(replay = 1)
        var stored: Boolean? = null

        override suspend fun setMaterialYouEnabled(enabled: Boolean) {
            stored = enabled
            materialYouEnabled.emit(enabled)
        }
    }

    private val repository = ControlledSettingsRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = AppSettingsViewModel(
        observeMaterialYouUseCase = ObserveMaterialYouUseCase(repository),
        setMaterialYouUseCase = SetMaterialYouUseCase(repository),
    )

    @Test
    fun stateNotLoadedBeforeFirstValue() = runTest(dispatcher) {
        val viewModel = viewModel()

        advanceUntilIdle()

        assertEquals(AppSettingsState(), viewModel.state.value)
    }

    @Test
    fun stateReflectsStoredValue() = runTest(dispatcher) {
        repository.materialYouEnabled.tryEmit(true)
        val viewModel = viewModel()

        advanceUntilIdle()

        assertEquals(AppSettingsState(materialYou = true, loaded = true), viewModel.state.value)
    }

    @Test
    fun toggleStoresAndUpdatesState() = runTest(dispatcher) {
        repository.materialYouEnabled.tryEmit(false)
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onAction(AppSettingsAction.MaterialYouToggled(enabled = true))
        advanceUntilIdle()

        assertEquals(true, repository.stored)
        assertEquals(AppSettingsState(materialYou = true, loaded = true), viewModel.state.value)
    }
}
