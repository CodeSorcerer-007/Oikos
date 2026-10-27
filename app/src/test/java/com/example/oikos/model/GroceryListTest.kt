package com.example.oikos.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GroceryListTest {

    @Test
    fun groceryList_defaultValues() {
        val list = GroceryList(name = "Weekend Market")
        assertNotNull(list.id)
        assertEquals("Weekend Market", list.name)
        assertEquals("🛒", list.emoji)
        assertNull(list.budget)
        assertFalse(list.isDefault)
        assertTrue(list.createdAt > 0)
    }

    @Test
    fun groceryList_customValuesAndCopy() {
        val list = GroceryList(
            id = "custom_id",
            name = "Organic Store",
            emoji = "🌿",
            budget = 3500.0,
            isDefault = true,
            createdAt = 123456789L
        )

        assertEquals("custom_id", list.id)
        assertEquals("Organic Store", list.name)
        assertEquals("🌿", list.emoji)
        assertEquals(3500.0, list.budget ?: 0.0, 0.001)
        assertTrue(list.isDefault)

        val modified = list.copy(budget = 4000.0, name = "Organic Mart")
        assertEquals(4000.0, modified.budget ?: 0.0, 0.001)
        assertEquals("Organic Mart", modified.name)
        assertEquals("custom_id", modified.id)
    }
}
