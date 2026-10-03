package com.example.oikos.nlp

import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartAddParserTest {

    @Test
    fun parse_emptyInput_returnsDefaultResult() {
        val result = SmartAddParser.parse("")
        assertEquals("", result.name)
        assertEquals(1.0, result.quantity, 0.001)
        assertEquals("pcs", result.unit)
        assertEquals(GroceryCategory.OTHER, result.category)
        assertNull(result.estimatedPrice)
    }

    @Test
    fun parse_simpleItem_infersCategoryAndUnit() {
        val result = SmartAddParser.parse("apples")
        assertEquals("Apples", result.name)
        assertEquals(1.0, result.quantity, 0.001)
        assertEquals("pcs", result.unit)
        assertEquals(GroceryCategory.PRODUCE, result.category)
    }

    @Test
    fun parse_itemWithQuantityAndUnitAttached() {
        val result = SmartAddParser.parse("2L milk")
        assertEquals("Milk", result.name)
        assertEquals(2.0, result.quantity, 0.001)
        assertEquals("L", result.unit)
        assertEquals(GroceryCategory.DAIRY, result.category)
    }

    @Test
    fun parse_itemWithSeparatedQuantityAndUnit() {
        val result = SmartAddParser.parse("500 g chicken")
        assertEquals("Chicken", result.name)
        assertEquals(500.0, result.quantity, 0.001)
        assertEquals("g", result.unit)
        assertEquals(GroceryCategory.MEAT, result.category)
    }

    @Test
    fun parse_itemWithFractionQuantity() {
        val result = SmartAddParser.parse("1/2 kg sugar")
        assertEquals("Sugar", result.name)
        assertEquals(0.5, result.quantity, 0.001)
        assertEquals("kg", result.unit)
        assertEquals(GroceryCategory.PANTRY, result.category)
    }

    @Test
    fun parse_itemWithPriceTag() {
        val result = SmartAddParser.parse("sourdough @ 85.50")
        assertEquals("Sourdough", result.name)
        assertEquals(85.50, result.estimatedPrice ?: 0.0, 0.001)
        assertEquals(GroceryCategory.BAKERY, result.category)
    }

    @Test
    fun parse_itemWithCurrencyPrefix() {
        val result = SmartAddParser.parse("olive oil ₹350")
        assertEquals("Olive Oil", result.name)
        assertEquals(350.0, result.estimatedPrice ?: 0.0, 0.001)
        assertEquals(GroceryCategory.PANTRY, result.category)
    }

    @Test
    fun parse_complexInput_allTokensParsed() {
        val result = SmartAddParser.parse("3 kg organic tomatoes @ 120")
        assertEquals("Organic Tomatoes", result.name)
        assertEquals(3.0, result.quantity, 0.001)
        assertEquals("kg", result.unit)
        assertEquals(120.0, result.estimatedPrice ?: 0.0, 0.001)
        assertEquals(GroceryCategory.PRODUCE, result.category)
    }

    @Test
    fun parse_categoryInference_allMajorCategories() {
        val produce = SmartAddParser.parse("bananas")
        assertEquals(GroceryCategory.PRODUCE, produce.category)

        val dairy = SmartAddParser.parse("greek yogurt")
        assertEquals(GroceryCategory.DAIRY, dairy.category)

        val bakery = SmartAddParser.parse("croissant")
        assertEquals(GroceryCategory.BAKERY, bakery.category)

        val meat = SmartAddParser.parse("salmon")
        assertEquals(GroceryCategory.MEAT, meat.category)

        val pantry = SmartAddParser.parse("basmati rice")
        assertEquals(GroceryCategory.PANTRY, pantry.category)

        val beverages = SmartAddParser.parse("sparkling water")
        assertEquals(GroceryCategory.BEVERAGES, beverages.category)

        val snacks = SmartAddParser.parse("dark chocolate")
        assertEquals(GroceryCategory.SNACKS, snacks.category)

        val frozen = SmartAddParser.parse("ice cream")
        assertEquals(GroceryCategory.FROZEN, frozen.category)

        val household = SmartAddParser.parse("dish soap")
        assertEquals(GroceryCategory.HOUSEHOLD, household.category)

        val personal = SmartAddParser.parse("toothpaste")
        assertEquals(GroceryCategory.PERSONAL_CARE, personal.category)
    }

    @Test
    fun findDuplicate_detectsExactAndFuzzyMatches() {
        val existing = listOf(
            GroceryItem(listId = "1", name = "Organic Whole Milk", quantity = 1.0, unit = "L", category = GroceryCategory.DAIRY),
            GroceryItem(listId = "1", name = "Avocados", quantity = 3.0, unit = "pcs", category = GroceryCategory.PRODUCE)
        )

        // Exact match
        val dup1 = SmartAddParser.findDuplicate("Avocados", existing)
        assertNotNull(dup1)
        assertEquals("Avocados", dup1?.name)

        // Case insensitive match
        val dup2 = SmartAddParser.findDuplicate("avocados", existing)
        assertNotNull(dup2)

        // Substring / singular match
        val dup3 = SmartAddParser.findDuplicate("avocado", existing)
        assertNotNull(dup3)

        // No match
        val noDup = SmartAddParser.findDuplicate("Butter", existing)
        assertNull(noDup)
    }

    @Test
    fun getSuggestions_returnsMatchingItems() {
        val suggestions = SmartAddParser.getSuggestions("mil", 5)
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.name.contains("Milk", ignoreCase = true) })
    }
}
