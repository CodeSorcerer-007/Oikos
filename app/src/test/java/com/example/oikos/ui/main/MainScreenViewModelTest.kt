package com.example.oikos.ui.main

import com.example.oikos.model.GroceryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActiveModalTest {

    @Test
    fun activeModal_editItem_holdsTargetItem() {
        val item = GroceryItem(name = "Avocado", quantity = 2.0)
        val modal = ActiveModal.EditItem(item)
        assertEquals("Avocado", modal.item.name)
        assertEquals(2.0, modal.item.quantity, 0.001)
    }

    @Test
    fun activeModal_singletons_areDistinct() {
        assertTrue(ActiveModal.None is ActiveModal)
        assertTrue(ActiveModal.ListManager is ActiveModal)
        assertTrue(ActiveModal.StoreOrder is ActiveModal)
        assertTrue(ActiveModal.Settings is ActiveModal)
        assertTrue(ActiveModal.Search is ActiveModal)
        assertTrue(ActiveModal.ShoppingMode is ActiveModal)
    }
}
