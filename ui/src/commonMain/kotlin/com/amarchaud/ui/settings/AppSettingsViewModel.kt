package com.amarchaud.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.domain.usecase.ObserveMaterialYouUseCase
import com.amarchaud.domain.usecase.SetMaterialYouUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** App-wide settings: currently the "Material You" theme option (Android 12+). */
class AppSettingsViewModel(
    observeMaterialYouUseCase: ObserveMaterialYouUseCase,
    private val setMaterialYouUseCase: SetMaterialYouUseCase,
) : ViewModel() {

    val state: StateFlow<AppSettingsState> = observeMaterialYouUseCase.run()
        .map { AppSettingsState(materialYou = it, loaded = true) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettingsState())

    fun onAction(action: AppSettingsAction) {
        when (action) {
            is AppSettingsAction.MaterialYouToggled -> viewModelScope.launch {
                setMaterialYouUseCase.run(action.enabled)
            }
        }
    }
}
