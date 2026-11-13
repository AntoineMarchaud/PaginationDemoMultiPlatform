package com.amarchaud.ui.settings

/**
 * @param loaded false until the stored value has been read:
 * the UI waits for it, so the palette never flashes at startup.
 */
data class AppSettingsState(
    val materialYou: Boolean = false,
    val loaded: Boolean = false,
)

sealed interface AppSettingsAction {
    data class MaterialYouToggled(val enabled: Boolean) : AppSettingsAction
}
