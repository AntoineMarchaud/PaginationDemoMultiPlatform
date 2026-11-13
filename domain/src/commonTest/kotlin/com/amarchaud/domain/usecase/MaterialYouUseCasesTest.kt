package com.amarchaud.domain.usecase

import com.amarchaud.domain.FakeSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MaterialYouUseCasesTest {

    @Test
    fun observeReturnsStoredValue() = runTest {
        val repository = FakeSettingsRepository(materialYou = true)

        assertTrue(ObserveMaterialYouUseCase(repository).run().first())
    }

    @Test
    fun setStoresValue() = runTest {
        val repository = FakeSettingsRepository(materialYou = false)

        SetMaterialYouUseCase(repository).run(enabled = true)

        assertTrue(repository.materialYouEnabled.value)
    }

    @Test
    fun disabledByDefault() = runTest {
        assertFalse(ObserveMaterialYouUseCase(FakeSettingsRepository()).run().first())
    }
}
