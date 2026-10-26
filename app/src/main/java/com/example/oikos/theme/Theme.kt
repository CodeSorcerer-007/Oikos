package com.example.oikos.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

private val LightColorScheme = lightColorScheme(
    primary = LightForestGreen,
    onPrimary = LightOnForestGreen,
    primaryContainer = LightForestGreenContainer,
    onPrimaryContainer = LightForestGreenDark,
    secondary = LightWarmHoney,
    onSecondary = LightOnWarmHoney,
    secondaryContainer = LightWarmHoneyContainer,
    onSecondaryContainer = LightWarmHoney,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder,
    outlineVariant = LightSurfaceBorder,
    error = LightError,
    onError = LightOnForestGreen,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightError
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkForestGreen,
    onPrimary = DarkOnForestGreen,
    primaryContainer = DarkForestGreenContainer,
    onPrimaryContainer = DarkForestGreenDark,
    secondary = DarkWarmHoney,
    onSecondary = DarkOnWarmHoney,
    secondaryContainer = DarkWarmHoneyContainer,
    onSecondaryContainer = DarkWarmHoney,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceBorder,
    outlineVariant = DarkSurfaceBorder,
    error = DarkError,
    onError = DarkOnForestGreen,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkError
)

@Composable
fun OikosTheme(
    themeMode: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode.uppercase()) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemDark
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val oikosColors = if (isDark) DarkOikosColorTokens else LightOikosColorTokens

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val bgColor = (if (isDark) DarkBackground else LightBackground).toArgb()
            window.statusBarColor = bgColor
            window.navigationBarColor = bgColor
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalOikosColors provides oikosColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
