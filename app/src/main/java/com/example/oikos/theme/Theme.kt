package com.example.oikos.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = OikosForestGreen,
    onPrimary = OikosOnForestGreen,
    primaryContainer = OikosForestGreenContainer,
    onPrimaryContainer = OikosForestGreenDark,
    secondary = OikosWarmHoney,
    onSecondary = OikosOnWarmHoney,
    secondaryContainer = OikosWarmHoneyContainer,
    onSecondaryContainer = OikosWarmHoney,
    background = OikosBackground,
    onBackground = OikosTextPrimary,
    surface = OikosSurface,
    onSurface = OikosTextPrimary,
    surfaceVariant = OikosSurfaceVariant,
    onSurfaceVariant = OikosTextSecondary,
    outline = OikosSurfaceBorder,
    outlineVariant = OikosSurfaceBorder,
    error = OikosError,
    onError = OikosOnForestGreen,
    errorContainer = OikosErrorContainer,
    onErrorContainer = OikosError
)

@Composable
fun OikosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Primary light aesthetic is the signature identity of Oikos
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = OikosBackground.toArgb()
            window.navigationBarColor = OikosBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
