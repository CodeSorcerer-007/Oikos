package com.example.oikos.tile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TileServiceTest {

    @Test
    fun extraConstant_isConfigured() {
        assertEquals("open_shopping_mode", OikosShoppingTileService.EXTRA_OPEN_SHOPPING_MODE)
    }

    @Test
    fun tileService_classExists() {
        val clazz = OikosShoppingTileService::class.java
        assertNotNull(clazz)
        assertEquals("com.example.oikos.tile.OikosShoppingTileService", clazz.name)
    }
}
