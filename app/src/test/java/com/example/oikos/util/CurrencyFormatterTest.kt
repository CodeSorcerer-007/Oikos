package com.example.oikos.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun format_nullAmount_returnsEmptyString() {
        val result = CurrencyFormatter.format(null, "₹")
        assertEquals("", result)
    }

    @Test
    fun format_roundInteger_omitsDecimals() {
        val result = CurrencyFormatter.format(150.0, "₹")
        assertEquals("₹150", result)
    }

    @Test
    fun format_fractionalAmount_displaysTwoDecimals() {
        val result = CurrencyFormatter.format(99.50, "$")
        assertEquals("$99.50", result)

        val result2 = CurrencyFormatter.format(45.25, "€")
        assertEquals("€45.25", result2)
    }

    @Test
    fun format_zeroAmount_formatsCorrectly() {
        val result = CurrencyFormatter.format(0.0, "₹")
        assertEquals("₹0", result)
    }
}
