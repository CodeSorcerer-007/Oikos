package com.example.oikos.data.repository

import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UndoActionTest {

    @Test
    fun undoAction_itemDeleted_storesDeletedItem() {
        val item = GroceryItem(id = "item_1", name = "Artisan Sourdough", quantity = 1.0, category = GroceryCategory.BAKERY)
        val action = UndoAction.ItemDeleted(item)
        assertEquals("item_1", action.item.id)
        assertEquals("Artisan Sourdough", action.item.name)
    }

    @Test
    fun undoAction_itemCompletionToggled_storesPreviousState() {
        val action = UndoAction.ItemCompletionToggled("item_42", previousState = false)
        assertEquals("item_42", action.itemId)
        assertEquals(false, action.previousState)
    }

    @Test
    fun undoAction_itemsCleared_storesListOfItems() {
        val items = listOf(
            GroceryItem(id = "1", name = "Milk", isCompleted = true),
            GroceryItem(id = "2", name = "Eggs", isCompleted = true)
        )
        val action = UndoAction.ItemsCleared(items)
        assertEquals(2, action.items.size)
        assertTrue(action.items.all { it.isCompleted })
    }
}
