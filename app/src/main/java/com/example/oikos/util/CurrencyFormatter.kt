package com.example.oikos.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    fun format(amount: Double?, symbol: String = "₹"): String {
        if (amount == null) return ""
        val isWhole = amount % 1.0 == 0.0
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        numberFormat.maximumFractionDigits = if (isWhole) 0 else 2
        numberFormat.minimumFractionDigits = if (isWhole) 0 else 2
        return "$symbol${numberFormat.format(amount)}"
    }
}
