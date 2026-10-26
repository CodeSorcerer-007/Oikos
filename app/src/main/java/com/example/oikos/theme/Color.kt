package com.example.oikos.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================
// 1. Static Light Palette (Organic Kitchen Aesthetic)
// ==========================================
val LightForestGreen = Color(0xFF223E25)
val LightForestGreenLight = Color(0xFF335837)
val LightForestGreenDark = Color(0xFF132515)
val LightForestGreenContainer = Color(0xFFE8EFE8)
val LightOnForestGreen = Color(0xFFFFFFFF)

val LightWarmHoney = Color(0xFFD4822B)
val LightWarmHoneyLight = Color(0xFFFFF7ED)
val LightWarmHoneyContainer = Color(0xFFFDECDA)
val LightOnWarmHoney = Color(0xFFFFFFFF)

val LightBackground = Color(0xFFFAF9F5)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1EFEB)
val LightSurfaceBorder = Color(0xFFE4E2DC)
val LightSurfaceSubtle = Color(0xFFF8F7F3)

val LightTextPrimary = Color(0xFF161C17)
val LightTextSecondary = Color(0xFF535D54)
val LightTextTertiary = Color(0xFF8B948C)
val LightTextInverse = Color(0xFFFFFFFF)

val LightSuccess = Color(0xFF2E7D32)
val LightSuccessContainer = Color(0xFFE8F5E9)
val LightWarning = Color(0xFFE65100)
val LightWarningContainer = Color(0xFFFFF3E0)
val LightError = Color(0xFFC62828)
val LightErrorContainer = Color(0xFFFFEBEE)

// ==========================================
// 2. Static Dark Palette (AMOLED Obsidian Slate)
// ==========================================
val DarkForestGreen = Color(0xFF4EA157)
val DarkForestGreenLight = Color(0xFF68B770)
val DarkForestGreenDark = Color(0xFF81C784)
val DarkForestGreenContainer = Color(0xFF1B3520)
val DarkOnForestGreen = Color(0xFFFFFFFF)

val DarkWarmHoney = Color(0xFFE5953E)
val DarkWarmHoneyLight = Color(0xFF2E1C0A)
val DarkWarmHoneyContainer = Color(0xFF3E2610)
val DarkOnWarmHoney = Color(0xFF1B1105)

val DarkBackground = Color(0xFF0F1411)
val DarkSurface = Color(0xFF181F1A)
val DarkSurfaceVariant = Color(0xFF202A22)
val DarkSurfaceBorder = Color(0xFF2B3A2E)
val DarkSurfaceSubtle = Color(0xFF131914)

val DarkTextPrimary = Color(0xFFF1F5F2)
val DarkTextSecondary = Color(0xFFA5B2A7)
val DarkTextTertiary = Color(0xFF708072)
val DarkTextInverse = Color(0xFF101411)

val DarkSuccess = Color(0xFF4CAF50)
val DarkSuccessContainer = Color(0xFF1B3B1D)
val DarkWarning = Color(0xFFFF9800)
val DarkWarningContainer = Color(0xFF3E280A)
val DarkError = Color(0xFFEF5350)
val DarkErrorContainer = Color(0xFF3E1818)

// ==========================================
// 3. Design System Token Contract
// ==========================================
data class OikosColors(
    val forestGreen: Color,
    val forestGreenLight: Color,
    val forestGreenDark: Color,
    val forestGreenContainer: Color,
    val onForestGreen: Color,
    val warmHoney: Color,
    val warmHoneyLight: Color,
    val warmHoneyContainer: Color,
    val onWarmHoney: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceBorder: Color,
    val surfaceSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textInverse: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val error: Color,
    val errorContainer: Color,
    val isDark: Boolean
)

val LightOikosColorTokens = OikosColors(
    forestGreen = LightForestGreen,
    forestGreenLight = LightForestGreenLight,
    forestGreenDark = LightForestGreenDark,
    forestGreenContainer = LightForestGreenContainer,
    onForestGreen = LightOnForestGreen,
    warmHoney = LightWarmHoney,
    warmHoneyLight = LightWarmHoneyLight,
    warmHoneyContainer = LightWarmHoneyContainer,
    onWarmHoney = LightOnWarmHoney,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    surfaceBorder = LightSurfaceBorder,
    surfaceSubtle = LightSurfaceSubtle,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    textInverse = LightTextInverse,
    success = LightSuccess,
    successContainer = LightSuccessContainer,
    warning = LightWarning,
    warningContainer = LightWarningContainer,
    error = LightError,
    errorContainer = LightErrorContainer,
    isDark = false
)

val DarkOikosColorTokens = OikosColors(
    forestGreen = DarkForestGreen,
    forestGreenLight = DarkForestGreenLight,
    forestGreenDark = DarkForestGreenDark,
    forestGreenContainer = DarkForestGreenContainer,
    onForestGreen = DarkOnForestGreen,
    warmHoney = DarkWarmHoney,
    warmHoneyLight = DarkWarmHoneyLight,
    warmHoneyContainer = DarkWarmHoneyContainer,
    onWarmHoney = DarkOnWarmHoney,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    surfaceBorder = DarkSurfaceBorder,
    surfaceSubtle = DarkSurfaceSubtle,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    textInverse = DarkTextInverse,
    success = DarkSuccess,
    successContainer = DarkSuccessContainer,
    warning = DarkWarning,
    warningContainer = DarkWarningContainer,
    error = DarkError,
    errorContainer = DarkErrorContainer,
    isDark = true
)

val LocalOikosColors = staticCompositionLocalOf { LightOikosColorTokens }

// ==========================================
// 4. Dynamic Composable Color Getters (Seamless dark/light switching)
// ==========================================
val OikosForestGreen: Color @Composable get() = LocalOikosColors.current.forestGreen
val OikosForestGreenLight: Color @Composable get() = LocalOikosColors.current.forestGreenLight
val OikosForestGreenDark: Color @Composable get() = LocalOikosColors.current.forestGreenDark
val OikosForestGreenContainer: Color @Composable get() = LocalOikosColors.current.forestGreenContainer
val OikosOnForestGreen: Color @Composable get() = LocalOikosColors.current.onForestGreen

val OikosWarmHoney: Color @Composable get() = LocalOikosColors.current.warmHoney
val OikosWarmHoneyLight: Color @Composable get() = LocalOikosColors.current.warmHoneyLight
val OikosWarmHoneyContainer: Color @Composable get() = LocalOikosColors.current.warmHoneyContainer
val OikosOnWarmHoney: Color @Composable get() = LocalOikosColors.current.onWarmHoney

val OikosBackground: Color @Composable get() = LocalOikosColors.current.background
val OikosSurface: Color @Composable get() = LocalOikosColors.current.surface
val OikosSurfaceVariant: Color @Composable get() = LocalOikosColors.current.surfaceVariant
val OikosSurfaceBorder: Color @Composable get() = LocalOikosColors.current.surfaceBorder
val OikosSurfaceSubtle: Color @Composable get() = LocalOikosColors.current.surfaceSubtle

val OikosTextPrimary: Color @Composable get() = LocalOikosColors.current.textPrimary
val OikosTextSecondary: Color @Composable get() = LocalOikosColors.current.textSecondary
val OikosTextTertiary: Color @Composable get() = LocalOikosColors.current.textTertiary
val OikosTextInverse: Color @Composable get() = LocalOikosColors.current.textInverse

val OikosSuccess: Color @Composable get() = LocalOikosColors.current.success
val OikosSuccessContainer: Color @Composable get() = LocalOikosColors.current.successContainer
val OikosWarning: Color @Composable get() = LocalOikosColors.current.warning
val OikosWarningContainer: Color @Composable get() = LocalOikosColors.current.warningContainer
val OikosError: Color @Composable get() = LocalOikosColors.current.error
val OikosErrorContainer: Color @Composable get() = LocalOikosColors.current.errorContainer

// Category Accent Badges
val CatProduceBg = Color(0xFFE8F5E9)
val CatProduceText = Color(0xFF1B5E20)
val CatBakeryBg = Color(0xFFFFF3E0)
val CatBakeryText = Color(0xFFE65100)
val CatDairyBg = Color(0xFFE3F2FD)
val CatDairyText = Color(0xFF0D47A1)
val CatMeatBg = Color(0xFFFFEBEE)
val CatMeatText = Color(0xFFB71C1C)
val CatPantryBg = Color(0xFFEFEBE9)
val CatPantryText = Color(0xFF4E342E)
val CatBeveragesBg = Color(0xFFE0F7FA)
val CatBeveragesText = Color(0xFF006064)
val CatSnacksBg = Color(0xFFFFF8E1)
val CatSnacksText = Color(0xFFF57F17)
val CatFrozenBg = Color(0xFFEDE7F6)
val CatFrozenText = Color(0xFF4A148C)
val CatHouseholdBg = Color(0xFFECEFF1)
val CatHouseholdText = Color(0xFF37474F)
val CatPersonalBg = Color(0xFFFCE4EC)
val CatPersonalText = Color(0xFF880E4F)
val CatOtherBg = Color(0xFFF5F5F5)
val CatOtherText = Color(0xFF616161)
