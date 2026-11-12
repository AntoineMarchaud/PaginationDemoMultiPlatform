package com.amarchaud.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import paginationdemomultiplatform.ui.generated.resources.Res
import paginationdemomultiplatform.ui.generated.resources.poppins_bold
import paginationdemomultiplatform.ui.generated.resources.poppins_medium
import paginationdemomultiplatform.ui.generated.resources.poppins_regular
import paginationdemomultiplatform.ui.generated.resources.poppins_semibold

@Composable
private fun poppins() = FontFamily(
    Font(Res.font.poppins_regular, FontWeight.Normal),
    Font(Res.font.poppins_medium, FontWeight.Medium),
    Font(Res.font.poppins_semibold, FontWeight.SemiBold),
    Font(Res.font.poppins_bold, FontWeight.Bold),
)

@Composable
internal fun appTypography(): Typography {
    val poppins = poppins()
    fun style(size: Int, line: Int, weight: FontWeight, letterSpacing: Double = 0.0) = TextStyle(
        fontFamily = poppins,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = line.sp,
        letterSpacing = letterSpacing.sp,
    )
    return Typography(
        displaySmall = style(36, 44, FontWeight.SemiBold, -0.5),
        headlineLarge = style(32, 40, FontWeight.SemiBold, -0.5),
        headlineMedium = style(28, 36, FontWeight.SemiBold, -0.25),
        headlineSmall = style(24, 32, FontWeight.SemiBold),
        titleLarge = style(20, 28, FontWeight.SemiBold),
        titleMedium = style(16, 24, FontWeight.SemiBold),
        titleSmall = style(14, 20, FontWeight.Medium),
        bodyLarge = style(16, 24, FontWeight.Normal),
        bodyMedium = style(14, 20, FontWeight.Normal),
        bodySmall = style(12, 16, FontWeight.Normal),
        labelLarge = style(14, 20, FontWeight.Medium),
        labelMedium = style(12, 16, FontWeight.Medium),
        labelSmall = style(11, 16, FontWeight.Medium, 0.2),
    )
}
