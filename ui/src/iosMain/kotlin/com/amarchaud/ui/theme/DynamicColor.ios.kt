package com.amarchaud.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null

actual val isDynamicColorSupported: Boolean = false
