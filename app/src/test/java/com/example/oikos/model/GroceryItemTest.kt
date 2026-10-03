package com.example.oikos.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GroceryItemTest {

    @Test
    fun formattedQuantity_singlePiece_returnsJustNumber() {
        val item = GroceryItem(name = "Apple", quantity = 1.0, unit = "pcs")
        assertEquals("1", item.formattedQuantity)
    }

    @Test
    fun formattedQuantity_multiplePieces_includesPcs() {
        val item = GroceryItem(name = "Apples", quantity = 6.0, unit = "pcs")
        assertEquals("6 pcs", item.formattedQuantity)
    }

    @Test
    fun formattedQuantity_withCustomUnit_includesUnit() {
        val item = GroceryItem(name = "Milk", quantity = 2.0, unit = "L")
        assertEquals("2 L", item.formattedQuantity)
    }

    @Test
    fun formattedQuantity_decimalValue_formatsOneDecimal() {
        val item = GroceryItem(name = "Sugar", quantity = 0.5, unit = "kg")
        assertEquals("0.5 kg", item.formattedQuantity)
    }

    @Test
    fun displayPrice_prioritizesActualPriceOverEstimated() {
        val itemWithBoth = GroceryItem(name = "Bread", estimatedPrice = 50.0, actualPrice = 45.0)
        assertEquals(45.0, itemWithBoth.displayPrice ?: 0.0, 0.001)

        val itemWithEstimatedOnly = GroceryItem(name = "Bread", estimatedPrice = 50.0, actualPrice = null)
        assertEquals(50.0, itemWithEstimatedOnly.displayPrice ?: 0.0, 0.001)

        val itemWithNone = GroceryItem(name = "Bread", estimatedPrice = null, actualPrice = null)
        assertNull(itemWithNone.displayPrice)
    }

    @Test
    fun defaultValues_areInitializedProperly() {
        val item = GroceryItem(name = "Eggs")
        assertNotNull(item.id)
        assertEquals("default_weekly", item.listId)
        assertFalse(item.isCompleted)
        assertFalse(item.isPriority)
        assertTrue(item.createdAt > 0)
        assertNull(item.completedAt)
    }
}
