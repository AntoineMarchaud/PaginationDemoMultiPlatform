package com.amarchaud.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** System "Material You" palette (Android 12+), null where it does not exist. */
@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?

/** Dynamic colors only exist on Android 12+. */
expect val isDynamicColorSupported: Boolean
