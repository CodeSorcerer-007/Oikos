package com.example.oikos.model

import androidx.compose.ui.graphics.Color

enum class GroceryCategory(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val defaultOrder: Int,
    val tagBgColorHex: Long,
    val tagTextColorHex: Long
) {
    PRODUCE(
        id = "produce",
        displayName = "Produce",
        subtitle = "Fresh fruits, vegetables & herbs",
        defaultOrder = 1,
        tagBgColorHex = 0xFFE8F5E9,
        tagTextColorHex = 0xFF1B5E20
    ),
    BAKERY(
        id = "bakery",
        displayName = "Bakery",
        subtitle = "Breads, buns, bagels & pastries",
        defaultOrder = 2,
        tagBgColorHex = 0xFFFFF3E0,
        tagTextColorHex = 0xFFE65100
    ),
    DAIRY(
        id = "dairy",
        displayName = "Dairy & Eggs",
        subtitle = "Milk, yogurt, cheeses & eggs",
        defaultOrder = 3,
        tagBgColorHex = 0xFFE3F2FD,
        tagTextColorHex = 0xFF0D47A1
    ),
    MEAT(
        id = "meat",
        displayName = "Meat & Seafood",
        subtitle = "Poultry, cuts, fish & deli",
        defaultOrder = 4,
        tagBgColorHex = 0xFFFFEBEE,
        tagTextColorHex = 0xFFB71C1C
    ),
    PANTRY(
        id = "pantry",
        displayName = "Pantry & Staples",
        subtitle = "Grains, pasta, spices, oils & sauces",
        defaultOrder = 5,
        tagBgColorHex = 0xFFEFEBE9,
        tagTextColorHex = 0xFF4E342E
    ),
    BEVERAGES(
        id = "beverages",
        displayName = "Beverages",
        subtitle = "Coffee, tea, juice, water & soda",
        defaultOrder = 6,
        tagBgColorHex = 0xFFE0F7FA,
        tagTextColorHex = 0xFF006064
    ),
    SNACKS(
        id = "snacks",
        displayName = "Snacks & Sweets",
        subtitle = "Nuts, chips, biscuits & chocolates",
        defaultOrder = 7,
        tagBgColorHex = 0xFFFFF8E1,
        tagTextColorHex = 0xFFF57F17
    ),
    FROZEN(
        id = "frozen",
        displayName = "Frozen Foods",
        subtitle = "Ice cream, frozen veg & ready meals",
        defaultOrder = 8,
        tagBgColorHex = 0xFFEDE7F6,
        tagTextColorHex = 0xFF4A148C
    ),
    HOUSEHOLD(
        id = "household",
        displayName = "Household",
        subtitle = "Cleaners, paper towels & detergents",
        defaultOrder = 9,
        tagBgColorHex = 0xFFECEFF1,
        tagTextColorHex = 0xFF37474F
    ),
    PERSONAL_CARE(
        id = "personal_care",
        displayName = "Personal Care",
        subtitle = "Soaps, shampoo, dental & hygiene",
        defaultOrder = 10,
        tagBgColorHex = 0xFFFCE4EC,
        tagTextColorHex = 0xFF880E4F
    ),
    OTHER(
        id = "other",
        displayName = "Other",
        subtitle = "Miscellaneous items",
        defaultOrder = 11,
        tagBgColorHex = 0xFFF5F5F5,
        tagTextColorHex = 0xFF616161
    );

    val tagBgColor: Color get() = Color(tagBgColorHex)
    val tagTextColor: Color get() = Color(tagTextColorHex)

    companion object {
        fun fromId(id: String?): GroceryCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: OTHER
        }

        fun fromName(name: String?): GroceryCategory {
            return entries.firstOrNull { 
                it.displayName.equals(name, ignoreCase = true) || it.name.equals(name, ignoreCase = true)
            } ?: OTHER
        }
    }
}
