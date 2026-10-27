package com.example.oikos.nlp

import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartAddParserAdvancedTest {

    @Test
    fun parse_collisionResistance_redPeppersIsNotBread() {
        val result = SmartAddParser.parse("red peppers")
        // "peppers" is produce, and "red" should NOT substring match "bread" (BAKERY)
        assertEquals(GroceryCategory.PRODUCE, result.category)
    }

    @Test
    fun parse_collisionResistance_almondsIsNotPalak() {
        val result = SmartAddParser.parse("almonds")
        assertEquals(GroceryCategory.SNACKS, result.category)
        assertEquals("Almonds", result.name)
    }

    @Test
    fun parse_collisionResistance_salmonSteak() {
        val result = SmartAddParser.parse("salmon steak")
        assertEquals(GroceryCategory.MEAT, result.category)
    }

    @Test
    fun parse_collisionResistance_oliveOilIsNotLive() {
        val result = SmartAddParser.parse("1L extra virgin olive oil @ 650")
        assertEquals(GroceryCategory.PANTRY, result.category)
        assertEquals(1.0, result.quantity, 0.001)
        assertEquals("L", result.unit)
        assertEquals(650.0, result.estimatedPrice ?: 0.0, 0.001)
    }

    @Test
    fun parse_fractions_quarterAndThreeQuarters() {
        val quarter = SmartAddParser.parse("1/4 kg butter")
        assertEquals(0.25, quarter.quantity, 0.001)
        assertEquals("kg", quarter.unit)
        assertEquals(GroceryCategory.DAIRY, quarter.category)

        val threeQuarter = SmartAddParser.parse("3/4 L milk")
        assertEquals(0.75, threeQuarter.quantity, 0.001)
        assertEquals("L", threeQuarter.unit)
        assertEquals(GroceryCategory.DAIRY, threeQuarter.category)
    }

    @Test
    fun parse_batchDelimiters_semicolonsAndNewlines() {
        val raw = "2L milk; 6 eggs\n1 loaf sourdough, 1kg bananas"
        assertTrue(SmartAddParser.isBatchInput(raw))
        val batch = SmartAddParser.parseBatch(raw)
        assertEquals(4, batch.size)
        assertEquals("Milk", batch[0].name)
        assertEquals("Eggs", batch[1].name)
        assertEquals("Sourdough", batch[2].name)
        assertEquals("Bananas", batch[3].name)
    }

    @Test
    fun parse_pricingTokens_variousCurrencies() {
        val p1 = SmartAddParser.parse("coffee inr 250")
        assertEquals(250.0, p1.estimatedPrice ?: 0.0, 0.001)

        val p2 = SmartAddParser.parse("cheese rs. 180")
        assertEquals(180.0, p2.estimatedPrice ?: 0.0, 0.001)

        val p3 = SmartAddParser.parse("apples for 120")
        assertEquals(120.0, p3.estimatedPrice ?: 0.0, 0.001)
    }

    @Test
    fun findDuplicate_matchesSingularAndPlural() {
        val items = listOf(
            GroceryItem(id = "1", name = "Apple", quantity = 1.0, isCompleted = false),
            GroceryItem(id = "2", name = "Eggs", quantity = 12.0, isCompleted = false),
            GroceryItem(id = "3", name = "Milk", quantity = 1.0, isCompleted = true) // completed!
        )

        // Exact match
        val matchApple = SmartAddParser.findDuplicate("Apple", items)
        assertNotNull(matchApple)
        assertEquals("1", matchApple?.id)

        // Plural matches singular
        val matchApples = SmartAddParser.findDuplicate("Apples", items)
        assertNotNull(matchApples)
        assertEquals("1", matchApples?.id)

        // Singular matches plural
        val matchEgg = SmartAddParser.findDuplicate("Egg", items)
        assertNotNull(matchEgg)
        assertEquals("2", matchEgg?.id)

        // Completed item is ignored (should not block adding fresh)
        val matchMilk = SmartAddParser.findDuplicate("Milk", items)
        assertNull(matchMilk)

        // Non-existent item
        val matchBread = SmartAddParser.findDuplicate("Bread", items)
        assertNull(matchBread)
    }

    @Test
    fun getSuggestions_prefixMatching() {
        val suggestions = SmartAddParser.getSuggestions("mil", limit = 3)
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.name.contains("Milk", ignoreCase = true) })
        assertTrue(suggestions.size <= 3)

        val empty = SmartAddParser.getSuggestions("", limit = 5)
        assertTrue(empty.isEmpty())
    }
}
