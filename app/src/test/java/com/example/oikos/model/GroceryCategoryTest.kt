package com.example.oikos.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GroceryCategoryTest {

    @Test
    fun defaultOrder_isSortedLogically() {
        val categories = GroceryCategory.entries.sortedBy { it.defaultOrder }
        // Produce is first in a standard supermarket layout
        assertEquals(GroceryCategory.PRODUCE, categories.first())
        // Other is last
        assertEquals(GroceryCategory.OTHER, categories.last())
    }

    @Test
    fun allCategories_haveNonEmptyDisplayNames() {
        GroceryCategory.entries.forEach { category ->
            assertTrue(category.displayName.isNotBlank())
        }
    }
}
