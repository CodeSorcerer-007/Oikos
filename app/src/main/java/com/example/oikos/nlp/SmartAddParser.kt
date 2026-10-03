package com.example.oikos.nlp

import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import java.util.Locale

data class ParsedItemResult(
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: GroceryCategory,
    val estimatedPrice: Double?,
    val confidenceScore: Float = 1.0f
) {
    fun toGroceryItem(listId: String): GroceryItem {
        return GroceryItem(
            listId = listId,
            name = name,
            quantity = quantity,
            unit = unit,
            category = category,
            estimatedPrice = estimatedPrice
        )
    }
}

object SmartAddParser {

    // Common grocery knowledge dictionary for auto-categorization and standard units
    private val groceryDictionary = mapOf(
        // Produce - Fruits & Vegetables
        "apple" to (GroceryCategory.PRODUCE to "pcs"),
        "apples" to (GroceryCategory.PRODUCE to "pcs"),
        "banana" to (GroceryCategory.PRODUCE to "bunch"),
        "bananas" to (GroceryCategory.PRODUCE to "bunch"),
        "orange" to (GroceryCategory.PRODUCE to "pcs"),
        "oranges" to (GroceryCategory.PRODUCE to "pcs"),
        "lemon" to (GroceryCategory.PRODUCE to "pcs"),
        "lemons" to (GroceryCategory.PRODUCE to "pcs"),
        "lime" to (GroceryCategory.PRODUCE to "pcs"),
        "tomato" to (GroceryCategory.PRODUCE to "kg"),
        "tomatoes" to (GroceryCategory.PRODUCE to "kg"),
        "onion" to (GroceryCategory.PRODUCE to "kg"),
        "onions" to (GroceryCategory.PRODUCE to "kg"),
        "potato" to (GroceryCategory.PRODUCE to "kg"),
        "potatoes" to (GroceryCategory.PRODUCE to "kg"),
        "spinach" to (GroceryCategory.PRODUCE to "bunch"),
        "palak" to (GroceryCategory.PRODUCE to "bunch"),
        "carrot" to (GroceryCategory.PRODUCE to "kg"),
        "carrots" to (GroceryCategory.PRODUCE to "kg"),
        "cucumber" to (GroceryCategory.PRODUCE to "pcs"),
        "cucumbers" to (GroceryCategory.PRODUCE to "pcs"),
        "garlic" to (GroceryCategory.PRODUCE to "pcs"),
        "ginger" to (GroceryCategory.PRODUCE to "g"),
        "avocado" to (GroceryCategory.PRODUCE to "pcs"),
        "avocados" to (GroceryCategory.PRODUCE to "pcs"),
        "coriander" to (GroceryCategory.PRODUCE to "bunch"),
        "cilantro" to (GroceryCategory.PRODUCE to "bunch"),
        "mint" to (GroceryCategory.PRODUCE to "bunch"),
        "lettuce" to (GroceryCategory.PRODUCE to "head"),
        "broccoli" to (GroceryCategory.PRODUCE to "head"),
        "capsicum" to (GroceryCategory.PRODUCE to "pcs"),
        "bell pepper" to (GroceryCategory.PRODUCE to "pcs"),
        "mango" to (GroceryCategory.PRODUCE to "kg"),
        "mangoes" to (GroceryCategory.PRODUCE to "kg"),
        "strawberries" to (GroceryCategory.PRODUCE to "box"),
        "grapes" to (GroceryCategory.PRODUCE to "kg"),
        "watermelon" to (GroceryCategory.PRODUCE to "pcs"),

        // Dairy & Eggs
        "milk" to (GroceryCategory.DAIRY to "L"),
        "whole milk" to (GroceryCategory.DAIRY to "L"),
        "toned milk" to (GroceryCategory.DAIRY to "L"),
        "curd" to (GroceryCategory.DAIRY to "pack"),
        "dahi" to (GroceryCategory.DAIRY to "pack"),
        "yogurt" to (GroceryCategory.DAIRY to "pack"),
        "greek yogurt" to (GroceryCategory.DAIRY to "pack"),
        "butter" to (GroceryCategory.DAIRY to "g"),
        "cheese" to (GroceryCategory.DAIRY to "pack"),
        "paneer" to (GroceryCategory.DAIRY to "g"),
        "cottage cheese" to (GroceryCategory.DAIRY to "g"),
        "egg" to (GroceryCategory.DAIRY to "pcs"),
        "eggs" to (GroceryCategory.DAIRY to "pcs"),
        "cream" to (GroceryCategory.DAIRY to "ml"),
        "sour cream" to (GroceryCategory.DAIRY to "pack"),
        "ghee" to (GroceryCategory.DAIRY to "kg"),

        // Bakery
        "bread" to (GroceryCategory.BAKERY to "pack"),
        "brown bread" to (GroceryCategory.BAKERY to "pack"),
        "sourdough" to (GroceryCategory.BAKERY to "loaf"),
        "white bread" to (GroceryCategory.BAKERY to "pack"),
        "bagel" to (GroceryCategory.BAKERY to "pack"),
        "bagels" to (GroceryCategory.BAKERY to "pack"),
        "buns" to (GroceryCategory.BAKERY to "pack"),
        "croissant" to (GroceryCategory.BAKERY to "pcs"),
        "croissants" to (GroceryCategory.BAKERY to "pcs"),
        "pita" to (GroceryCategory.BAKERY to "pack"),
        "muffins" to (GroceryCategory.BAKERY to "box"),

        // Meat & Seafood
        "chicken" to (GroceryCategory.MEAT to "kg"),
        "chicken breast" to (GroceryCategory.MEAT to "kg"),
        "chicken wings" to (GroceryCategory.MEAT to "kg"),
        "mutton" to (GroceryCategory.MEAT to "kg"),
        "fish" to (GroceryCategory.MEAT to "kg"),
        "salmon" to (GroceryCategory.MEAT to "g"),
        "prawns" to (GroceryCategory.MEAT to "g"),
        "shrimp" to (GroceryCategory.MEAT to "g"),
        "bacon" to (GroceryCategory.MEAT to "pack"),
        "sausage" to (GroceryCategory.MEAT to "pack"),
        "sausages" to (GroceryCategory.MEAT to "pack"),
        "turkey" to (GroceryCategory.MEAT to "kg"),

        // Pantry & Staples
        "rice" to (GroceryCategory.PANTRY to "kg"),
        "basmati rice" to (GroceryCategory.PANTRY to "kg"),
        "atta" to (GroceryCategory.PANTRY to "kg"),
        "flour" to (GroceryCategory.PANTRY to "kg"),
        "wheat flour" to (GroceryCategory.PANTRY to "kg"),
        "dal" to (GroceryCategory.PANTRY to "kg"),
        "toor dal" to (GroceryCategory.PANTRY to "kg"),
        "moong dal" to (GroceryCategory.PANTRY to "kg"),
        "lentils" to (GroceryCategory.PANTRY to "kg"),
        "pasta" to (GroceryCategory.PANTRY to "pack"),
        "spaghetti" to (GroceryCategory.PANTRY to "pack"),
        "noodles" to (GroceryCategory.PANTRY to "pack"),
        "olive oil" to (GroceryCategory.PANTRY to "L"),
        "oil" to (GroceryCategory.PANTRY to "L"),
        "sunflower oil" to (GroceryCategory.PANTRY to "L"),
        "mustard oil" to (GroceryCategory.PANTRY to "L"),
        "salt" to (GroceryCategory.PANTRY to "kg"),
        "sugar" to (GroceryCategory.PANTRY to "kg"),
        "honey" to (GroceryCategory.PANTRY to "bottle"),
        "black pepper" to (GroceryCategory.PANTRY to "g"),
        "turmeric" to (GroceryCategory.PANTRY to "g"),
        "cumin" to (GroceryCategory.PANTRY to "g"),
        "oats" to (GroceryCategory.PANTRY to "kg"),
        "cereal" to (GroceryCategory.PANTRY to "box"),
        "peanut butter" to (GroceryCategory.PANTRY to "jar"),
        "jam" to (GroceryCategory.PANTRY to "jar"),
        "ketchup" to (GroceryCategory.PANTRY to "bottle"),
        "soy sauce" to (GroceryCategory.PANTRY to "bottle"),

        // Beverages
        "coffee" to (GroceryCategory.BEVERAGES to "pack"),
        "coffee powder" to (GroceryCategory.BEVERAGES to "pack"),
        "tea" to (GroceryCategory.BEVERAGES to "box"),
        "green tea" to (GroceryCategory.BEVERAGES to "box"),
        "chai" to (GroceryCategory.BEVERAGES to "pack"),
        "orange juice" to (GroceryCategory.BEVERAGES to "L"),
        "apple juice" to (GroceryCategory.BEVERAGES to "L"),
        "water" to (GroceryCategory.BEVERAGES to "bottle"),
        "sparkling water" to (GroceryCategory.BEVERAGES to "can"),
        "coke" to (GroceryCategory.BEVERAGES to "can"),
        "soda" to (GroceryCategory.BEVERAGES to "bottle"),
        "oat milk" to (GroceryCategory.BEVERAGES to "L"),
        "almond milk" to (GroceryCategory.BEVERAGES to "L"),

        // Snacks
        "chips" to (GroceryCategory.SNACKS to "pack"),
        "potato chips" to (GroceryCategory.SNACKS to "pack"),
        "biscuits" to (GroceryCategory.SNACKS to "pack"),
        "cookies" to (GroceryCategory.SNACKS to "pack"),
        "chocolate" to (GroceryCategory.SNACKS to "bar"),
        "almonds" to (GroceryCategory.SNACKS to "g"),
        "cashews" to (GroceryCategory.SNACKS to "g"),
        "walnuts" to (GroceryCategory.SNACKS to "g"),
        "popcorn" to (GroceryCategory.SNACKS to "pack"),
        "crackers" to (GroceryCategory.SNACKS to "box"),

        // Frozen
        "ice cream" to (GroceryCategory.FROZEN to "tub"),
        "frozen peas" to (GroceryCategory.FROZEN to "pack"),
        "frozen fries" to (GroceryCategory.FROZEN to "pack"),
        "nuggets" to (GroceryCategory.FROZEN to "pack"),
        "frozen berries" to (GroceryCategory.FROZEN to "pack"),

        // Household
        "dish soap" to (GroceryCategory.HOUSEHOLD to "bottle"),
        "detergent" to (GroceryCategory.HOUSEHOLD to "kg"),
        "paper towels" to (GroceryCategory.HOUSEHOLD to "roll"),
        "toilet paper" to (GroceryCategory.HOUSEHOLD to "pack"),
        "trash bags" to (GroceryCategory.HOUSEHOLD to "box"),
        "garbage bags" to (GroceryCategory.HOUSEHOLD to "box"),
        "sponges" to (GroceryCategory.HOUSEHOLD to "pack"),

        // Personal Care
        "shampoo" to (GroceryCategory.PERSONAL_CARE to "bottle"),
        "soap" to (GroceryCategory.PERSONAL_CARE to "bar"),
        "body wash" to (GroceryCategory.PERSONAL_CARE to "bottle"),
        "toothpaste" to (GroceryCategory.PERSONAL_CARE to "tube"),
        "toothbrush" to (GroceryCategory.PERSONAL_CARE to "pcs"),
        "hand wash" to (GroceryCategory.PERSONAL_CARE to "bottle"),
        "conditioner" to (GroceryCategory.PERSONAL_CARE to "bottle"),
        "deodorant" to (GroceryCategory.PERSONAL_CARE to "pcs")
    )

    private val recognizedUnits = setOf(
        "kg", "g", "gm", "gram", "grams", "kilo", "kilos", "kilogram", "kilograms",
        "l", "ltr", "litre", "litres", "liter", "liters", "ml", "milliliters",
        "pack", "packs", "packet", "packets", "pkt", "pkts",
        "pcs", "pc", "piece", "pieces",
        "bottle", "bottles", "btl",
        "can", "cans", "box", "boxes", "jar", "jars", "tub", "tubs",
        "loaf", "loaves", "head", "heads", "bunch", "bunches", "roll", "rolls",
        "bar", "bars", "tube", "tubes", "dozen", "dozens"
    )

    fun parse(rawInput: String): ParsedItemResult {
        var input = rawInput.trim()
        if (input.isBlank()) {
            return ParsedItemResult(
                name = "",
                quantity = 1.0,
                unit = "pcs",
                category = GroceryCategory.OTHER,
                estimatedPrice = null
            )
        }

        // 1. Extract optional price (e.g. "@ 80" or "rs 80" or "₹80" or "for 80")
        var estimatedPrice: Double? = null
        val priceRegex = Regex("""(?:@|rs\.?|₹|for|\binr)\s*(\d+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
        val priceMatch = priceRegex.find(input)
        if (priceMatch != null) {
            estimatedPrice = priceMatch.groupValues[1].toDoubleOrNull()
            input = input.removeRange(priceMatch.range).trim()
        }

        // 2. Extract leading quantity and unit (e.g. "2L", "2 litres", "1.5 kg", "6", "1/2 kg")
        var quantity = 1.0
        var unit = "pcs"
        var itemName = input

        // Fraction pattern e.g. "1/2 kg sugar" or "1/4 kg"
        val fractionRegex = Regex("""^(\d+)/(\d+)\s*([a-zA-Z]+)?\s+(.+)$""")
        val fractionMatch = fractionRegex.find(input)
        if (fractionMatch != null) {
            val num = fractionMatch.groupValues[1].toDoubleOrNull() ?: 1.0
            val denom = fractionMatch.groupValues[2].toDoubleOrNull() ?: 2.0
            quantity = if (denom != 0.0) num / denom else 1.0
            val potentialUnit = fractionMatch.groupValues[3].lowercase(Locale.ROOT)
            if (isRecognizedUnit(potentialUnit)) {
                unit = normalizeUnit(potentialUnit)
            }
            itemName = fractionMatch.groupValues[4].trim()
        } else {
            // Pattern: number followed optionally by attached unit then space, or number space unit space item
            // Examples: "2L milk", "2 L milk", "2 litres milk", "500g chicken", "6 eggs"
            val qtyUnitRegex = Regex("""^(\d+(?:\.\d+)?)\s*([a-zA-Z]+)?(?:\s+(.+))?$""")
            val match = qtyUnitRegex.find(input)

            if (match != null) {
                val qtyVal = match.groupValues[1].toDoubleOrNull()
                val potentialUnit = match.groupValues[2].lowercase(Locale.ROOT)
                val rest = match.groupValues[3].trim()

                if (qtyVal != null) {
                    if (potentialUnit.isNotBlank() && isRecognizedUnit(potentialUnit)) {
                        quantity = qtyVal
                        unit = normalizeUnit(potentialUnit)
                        itemName = rest.ifBlank { input }
                    } else if (rest.isNotBlank()) {
                        // Check if the first word of 'rest' is a unit, e.g. "2 litres milk"
                        val words = rest.split(Regex("""\s+"""), limit = 2)
                        val firstWord = words[0].lowercase(Locale.ROOT)
                        if (isRecognizedUnit(firstWord)) {
                            quantity = qtyVal
                            unit = normalizeUnit(firstWord)
                            itemName = if (words.size > 1) words[1] else rest
                        } else {
                            // "6 eggs" -> quantity 6, unit pcs, itemName "eggs"
                            quantity = qtyVal
                            unit = "pcs"
                            itemName = rest
                        }
                    } else if (potentialUnit.isNotBlank()) {
                        // "2 apples" where "apples" is not a unit
                        quantity = qtyVal
                        unit = "pcs"
                        itemName = potentialUnit
                    }
                }
            }
        }

        // Clean up item name: capitalize words nicely
        val cleanName = cleanAndCapitalize(itemName)

        // 3. Infer Category and fallback Unit from dictionary
        val (inferredCategory, defaultUnit) = inferCategoryAndUnit(cleanName)

        // If unit wasn't explicitly specified or was default "pcs", adopt defaultUnit if meaningful
        val finalUnit = if (unit == "pcs" && defaultUnit != "pcs" && quantity == 1.0) {
            defaultUnit
        } else {
            unit
        }

        return ParsedItemResult(
            name = cleanName,
            quantity = quantity,
            unit = finalUnit,
            category = inferredCategory,
            estimatedPrice = estimatedPrice
        )
    }

    private fun isRecognizedUnit(unitCandidate: String): Boolean {
        return recognizedUnits.contains(unitCandidate.lowercase(Locale.ROOT))
    }

    private fun normalizeUnit(unit: String): String {
        return when (unit.lowercase(Locale.ROOT)) {
            "l", "ltr", "litre", "litres", "liter", "liters" -> "L"
            "ml", "milliliters" -> "ml"
            "kg", "kilo", "kilos", "kilogram", "kilograms" -> "kg"
            "g", "gm", "gram", "grams" -> "g"
            "pack", "packs", "packet", "packets", "pkt", "pkts" -> "pack"
            "pcs", "pc", "piece", "pieces" -> "pcs"
            "bottle", "bottles", "btl" -> "bottle"
            "can", "cans" -> "can"
            "box", "boxes" -> "box"
            "jar", "jars" -> "jar"
            "tub", "tubs" -> "tub"
            "loaf", "loaves" -> "loaf"
            "head", "heads" -> "head"
            "bunch", "bunches" -> "bunch"
            "roll", "rolls" -> "roll"
            "bar", "bars" -> "bar"
            "tube", "tubes" -> "tube"
            "dozen", "dozens" -> "dozen"
            else -> unit
        }
    }

    private fun inferCategoryAndUnit(name: String): Pair<GroceryCategory, String> {
        val lower = name.lowercase(Locale.ROOT).trim()

        // Direct exact match
        groceryDictionary[lower]?.let { return it }

        // Substring / token matching
        for ((key, pair) in groceryDictionary) {
            if (lower.contains(key) || key.contains(lower)) {
                return pair
            }
        }

        // Heuristics based on keyword indicators
        return when {
            lower.contains("juice") || lower.contains("tea") || lower.contains("coffee") || lower.contains("water") || lower.contains("drink") -> 
                GroceryCategory.BEVERAGES to "bottle"
            lower.contains("milk") || lower.contains("cheese") || lower.contains("paneer") || lower.contains("yogurt") || lower.contains("butter") -> 
                GroceryCategory.DAIRY to "pack"
            lower.contains("bread") || lower.contains("cake") || lower.contains("pastry") || lower.contains("bun") || lower.contains("cookie") -> 
                GroceryCategory.BAKERY to "pack"
            lower.contains("meat") || lower.contains("chicken") || lower.contains("fish") || lower.contains("pork") || lower.contains("beef") -> 
                GroceryCategory.MEAT to "kg"
            lower.contains("fruit") || lower.contains("apple") || lower.contains("berry") || lower.contains("vegetable") || lower.contains("salad") -> 
                GroceryCategory.PRODUCE to "kg"
            lower.contains("rice") || lower.contains("flour") || lower.contains("oil") || lower.contains("dal") || lower.contains("spice") || lower.contains("sauce") -> 
                GroceryCategory.PANTRY to "kg"
            lower.contains("snack") || lower.contains("chip") || lower.contains("nut") || lower.contains("candy") -> 
                GroceryCategory.SNACKS to "pack"
            lower.contains("soap") || lower.contains("cleaner") || lower.contains("towel") || lower.contains("wipe") -> 
                GroceryCategory.HOUSEHOLD to "pack"
            lower.contains("shampoo") || lower.contains("cream") || lower.contains("wash") || lower.contains("paste") -> 
                GroceryCategory.PERSONAL_CARE to "pcs"
            else -> GroceryCategory.OTHER to "pcs"
        }
    }

    private fun cleanAndCapitalize(text: String): String {
        return text.split(Regex("""\s+"""))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase(Locale.ROOT).replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() 
                }
            }
    }

    data class SuggestionItem(
        val name: String,
        val category: GroceryCategory,
        val defaultUnit: String
    )

    fun findDuplicate(name: String, existingItems: List<GroceryItem>): GroceryItem? {
        val trimmed = name.trim().lowercase(Locale.ROOT)
        if (trimmed.isBlank()) return null
        return existingItems.firstOrNull { item ->
            val itemName = item.name.trim().lowercase(Locale.ROOT)
            itemName == trimmed || 
            itemName == trimmed + "s" || 
            itemName + "s" == trimmed ||
            (itemName.contains(trimmed) && trimmed.length >= 3)
        }
    }

    fun getSuggestions(prefix: String, limit: Int = 5): List<SuggestionItem> {
        val lower = prefix.trim().lowercase(Locale.ROOT)
        if (lower.isBlank()) return emptyList()
        return groceryDictionary.entries
            .filter { it.key.startsWith(lower) || it.key.contains(lower) }
            .take(limit)
            .map { (key, pair) ->
                SuggestionItem(
                    name = cleanAndCapitalize(key),
                    category = pair.first,
                    defaultUnit = pair.second
                )
            }
    }
}

