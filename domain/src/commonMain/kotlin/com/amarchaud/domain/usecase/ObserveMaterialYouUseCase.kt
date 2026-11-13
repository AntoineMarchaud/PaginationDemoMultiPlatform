package com.amarchaud.domain.usecase

import com.amarchaud.domain.repository.SettingsRepository

class ObserveMaterialYouUseCase(
    private val repository: SettingsRepository
) {
    fun run() = repository.materialYouEnabled
}
