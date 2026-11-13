package com.amarchaud.domain.usecase

import com.amarchaud.domain.repository.SettingsRepository

class SetMaterialYouUseCase(
    private val repository: SettingsRepository
) {
    suspend fun run(enabled: Boolean) = repository.setMaterialYouEnabled(enabled)
}
