package com.example.oikos.theme

import com.example.oikos.model.GroceryCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeTest {

    @Test
    fun themeMode_entries_containExpectedValues() {
        val modes = ThemeMode.entries.map { it.name }
        assertTrue(modes.contains("SYSTEM"))
        assertTrue(modes.contains("LIGHT"))
        assertTrue(modes.contains("DARK"))
    }

    @Test
    fun colorTokens_areDistinctBetweenLightAndDark() {
        assertFalse(LightOikosColorTokens.isDark)
        assertTrue(DarkOikosColorTokens.isDark)

        // Dark background should be deep AMOLED obsidian
        assertEquals(DarkBackground, DarkOikosColorTokens.background)
        assertEquals(LightBackground, LightOikosColorTokens.background)
    }

    @Test
    fun category_hasAdaptiveDarkBadges() {
        GroceryCategory.entries.forEach { category ->
            // In dark mode, category tagDarkTextColor should be non-null and distinct
            assertTrue(category.tagDarkTextColor != category.tagDarkBgColor)
        }
    }
}
