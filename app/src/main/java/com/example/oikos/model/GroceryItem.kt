package com.example.oikos.model

import java.util.UUID

data class GroceryItem(
    val id: String = UUID.randomUUID().toString(),
    val listId: String = "default_weekly",
    val name: String,
    val quantity: Double = 1.0,
    val unit: String = "pcs",
    val category: GroceryCategory = GroceryCategory.OTHER,
    val isCompleted: Boolean = false,
    val estimatedPrice: Double? = null,
    val actualPrice: Double? = null,
    val notes: String? = null,
    val isPriority: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val sortOrder: Int = 0
) {
    val formattedQuantity: String
        get() {
            val qStr = if (quantity % 1.0 == 0.0) {
                quantity.toInt().toString()
            } else {
                String.format("%.1f", quantity)
            }
            return if (unit.isNotBlank() && !unit.equals("pcs", ignoreCase = true)) {
                "$qStr $unit"
            } else if (quantity > 1.0) {
                "$qStr pcs"
            } else {
                qStr
            }
        }

    val displayPrice: Double?
        get() = actualPrice ?: estimatedPrice
}
