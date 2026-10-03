package com.example.oikos.model

data class GroceryList(
    val id: String,
    val name: String,
    val emoji: String = "🛒",
    val budget: Double? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class StoreOrderPreference(
    val categoriesInOrder: List<GroceryCategory> = listOf(
        GroceryCategory.PRODUCE,
        GroceryCategory.BAKERY,
        GroceryCategory.DAIRY,
        GroceryCategory.MEAT,
        GroceryCategory.PANTRY,
        GroceryCategory.BEVERAGES,
        GroceryCategory.SNACKS,
        GroceryCategory.FROZEN,
        GroceryCategory.HOUSEHOLD,
        GroceryCategory.PERSONAL_CARE,
        GroceryCategory.OTHER
    )
)

data class FrequentItem(
    val name: String,
    val defaultQuantity: Double = 1.0,
    val defaultUnit: String = "pcs",
    val category: GroceryCategory = GroceryCategory.OTHER,
    val buyCount: Int = 1,
    val lastBoughtAt: Long = System.currentTimeMillis()
)
