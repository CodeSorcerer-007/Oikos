package com.example.oikos.ui.main

import com.example.oikos.model.FrequentItem
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainScreenViewModelLogicTest {

    @Test
    fun smartRestockLogic_excludesItemsAlreadyInCart() {
        val currentItems = listOf(
            GroceryItem(name = "Farm Fresh Whole Milk", quantity = 2.0),
            GroceryItem(name = "Eggs", quantity = 12.0)
        )

        val frequentItems = listOf(
            FrequentItem("Milk", 2.0, "L", GroceryCategory.DAIRY, buyCount = 10, lastBoughtAt = 1000L),
            FrequentItem("Eggs", 12.0, "pcs", GroceryCategory.DAIRY, buyCount = 8, lastBoughtAt = 1000L),
            FrequentItem("Bread", 1.0, "loaf", GroceryCategory.BAKERY, buyCount = 6, lastBoughtAt = 1000L),
            FrequentItem("Tomatoes", 1.0, "kg", GroceryCategory.PRODUCE, buyCount = 5, lastBoughtAt = 1000L),
            FrequentItem("Coffee", 1.0, "pack", GroceryCategory.BEVERAGES, buyCount = 4, lastBoughtAt = 1000L)
        )

        val activeNames = currentItems.map { it.name.trim().lowercase() }.toSet()
        val suggestions = frequentItems.filter { it.name.trim().lowercase() !in activeNames }
            .sortedByDescending { it.buyCount }
            .take(6)

        // Eggs is in active list, so it should be excluded. Bread, Tomatoes, Coffee should be included.
        assertEquals(4, suggestions.size)
        assertFalse(suggestions.any { it.name.equals("Eggs", ignoreCase = true) })
        assertTrue(suggestions.any { it.name == "Bread" })
        assertTrue(suggestions.any { it.name == "Tomatoes" })
    }

    @Test
    fun quantityStepperLogic_fractionalVsIntegerStep() {
        val fractionalItem = GroceryItem(name = "Saffron", quantity = 0.5)
        val stepSmall = if (fractionalItem.quantity < 1.0) 0.5 else 1.0
        val updatedSmall = fractionalItem.copy(quantity = fractionalItem.quantity + stepSmall)
        assertEquals(1.0, updatedSmall.quantity, 0.001)

        val wholeItem = GroceryItem(name = "Milk", quantity = 2.0)
        val stepWhole = if (wholeItem.quantity < 1.0) 0.5 else 1.0
        val updatedWhole = wholeItem.copy(quantity = wholeItem.quantity + stepWhole)
        assertEquals(3.0, updatedWhole.quantity, 0.001)
    }

    @Test
    fun storeWalkingOrderLogic_groupsItemsAccordingToAisleSequence() {
        val customStoreOrder = listOf(
            GroceryCategory.BAKERY,
            GroceryCategory.DAIRY,
            GroceryCategory.PRODUCE
        )

        val activeItems = listOf(
            GroceryItem(name = "Tomatoes", category = GroceryCategory.PRODUCE),
            GroceryItem(name = "Sourdough", category = GroceryCategory.BAKERY),
            GroceryItem(name = "Butter", category = GroceryCategory.DAIRY)
        )

        val grouped = customStoreOrder.mapNotNull { cat ->
            val catItems = activeItems.filter { it.category == cat }
            if (catItems.isNotEmpty()) cat to catItems else null
        }

        assertEquals(3, grouped.size)
        // First aisle must be Bakery
        assertEquals(GroceryCategory.BAKERY, grouped[0].first)
        assertEquals("Sourdough", grouped[0].second[0].name)
        // Second aisle Dairy
        assertEquals(GroceryCategory.DAIRY, grouped[1].first)
        // Third aisle Produce
        assertEquals(GroceryCategory.PRODUCE, grouped[2].first)
    }

    @Test
    fun searchFilteringLogic_matchesNameCategoryAndNotes() {
        val items = listOf(
            GroceryItem(name = "Extra Virgin Olive Oil", category = GroceryCategory.PANTRY, notes = "Cold pressed"),
            GroceryItem(name = "Green Tea", category = GroceryCategory.BEVERAGES, notes = "Jasmine flavor"),
            GroceryItem(name = "Apples", category = GroceryCategory.PRODUCE)
        )

        // Query matching name
        val queryName = "olive"
        val matchesName = items.filter { it.name.lowercase().contains(queryName) }
        assertEquals(1, matchesName.size)
        assertEquals("Extra Virgin Olive Oil", matchesName[0].name)

        // Query matching category
        val queryCat = "beverages"
        val matchesCat = items.filter { it.category.displayName.lowercase().contains(queryCat) }
        assertEquals(1, matchesCat.size)
        assertEquals("Green Tea", matchesCat[0].name)

        // Query matching notes
        val queryNotes = "jasmine"
        val matchesNotes = items.filter { it.notes?.lowercase()?.contains(queryNotes) == true }
        assertEquals(1, matchesNotes.size)
        assertEquals("Green Tea", matchesNotes[0].name)
    }
}
