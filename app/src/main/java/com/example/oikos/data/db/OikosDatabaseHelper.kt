package com.example.oikos.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.oikos.model.FrequentItem
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import com.example.oikos.model.GroceryList

class OikosDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "oikos_grocery.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_LISTS = "lists"
        const val TABLE_ITEMS = "items"
        const val TABLE_FREQUENT_ITEMS = "frequent_items"
        const val TABLE_STORE_ORDER = "store_order"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Lists table
        db.execSQL(
            """
            CREATE TABLE $TABLE_LISTS (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                emoji TEXT NOT NULL,
                budget REAL,
                is_default INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Items table
        db.execSQL(
            """
            CREATE TABLE $TABLE_ITEMS (
                id TEXT PRIMARY KEY,
                list_id TEXT NOT NULL,
                name TEXT NOT NULL,
                quantity REAL NOT NULL,
                unit TEXT NOT NULL,
                category TEXT NOT NULL,
                is_completed INTEGER NOT NULL DEFAULT 0,
                estimated_price REAL,
                actual_price REAL,
                notes TEXT,
                is_priority INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL,
                completed_at INTEGER,
                sort_order INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (list_id) REFERENCES $TABLE_LISTS (id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_items_list ON $TABLE_ITEMS (list_id, is_completed)")
        db.execSQL("CREATE INDEX idx_items_name ON $TABLE_ITEMS (name)")

        // Frequent Items table (Personal Grocery Memory)
        db.execSQL(
            """
            CREATE TABLE $TABLE_FREQUENT_ITEMS (
                name TEXT PRIMARY KEY,
                default_quantity REAL NOT NULL,
                default_unit TEXT NOT NULL,
                category TEXT NOT NULL,
                buy_count INTEGER NOT NULL DEFAULT 1,
                last_bought_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Store Order table
        db.execSQL(
            """
            CREATE TABLE $TABLE_STORE_ORDER (
                category TEXT PRIMARY KEY,
                sort_order INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Seed initial data
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Future migrations
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()

        // Default List
        val defaultListValues = ContentValues().apply {
            put("id", "default_weekly")
            put("name", "Weekly Groceries")
            put("emoji", "🌿")
            put("budget", 2000.0)
            put("is_default", 1)
            put("created_at", now)
        }
        db.insert(TABLE_LISTS, null, defaultListValues)

        // Second List: Pantry Essentials
        val pantryListValues = ContentValues().apply {
            put("id", "pantry_restock")
            put("name", "Pantry & Spices")
            put("emoji", "🍯")
            put("budget", 1500.0)
            put("is_default", 0)
            put("created_at", now + 10)
        }
        db.insert(TABLE_LISTS, null, pantryListValues)

        // Curated initial groceries for default list
        val sampleItems = listOf(
            GroceryItem(
                name = "Farm Fresh Whole Milk",
                quantity = 2.0,
                unit = "L",
                category = GroceryCategory.DAIRY,
                estimatedPrice = 130.0,
                notes = "Glass bottles if available"
            ),
            GroceryItem(
                name = "Organic Brown Eggs",
                quantity = 12.0,
                unit = "pcs",
                category = GroceryCategory.DAIRY,
                estimatedPrice = 110.0,
                isPriority = true
            ),
            GroceryItem(
                name = "Artisan Sourdough Loaf",
                quantity = 1.0,
                unit = "loaf",
                category = GroceryCategory.BAKERY,
                estimatedPrice = 150.0
            ),
            GroceryItem(
                name = "Roma Vine Tomatoes",
                quantity = 1.0,
                unit = "kg",
                category = GroceryCategory.PRODUCE,
                estimatedPrice = 45.0
            ),
            GroceryItem(
                name = "Baby Spinach",
                quantity = 2.0,
                unit = "bunch",
                category = GroceryCategory.PRODUCE,
                estimatedPrice = 50.0
            ),
            GroceryItem(
                name = "Extra Virgin Olive Oil",
                quantity = 1.0,
                unit = "L",
                category = GroceryCategory.PANTRY,
                estimatedPrice = 750.0
            ),
            GroceryItem(
                name = "Dark Roast Arabica Coffee",
                quantity = 1.0,
                unit = "pack",
                category = GroceryCategory.BEVERAGES,
                estimatedPrice = 380.0
            ),
            GroceryItem(
                name = "Raw Honey",
                quantity = 1.0,
                unit = "jar",
                category = GroceryCategory.PANTRY,
                estimatedPrice = 280.0,
                isCompleted = true,
                completedAt = now - 3600000
            )
        )

        for (item in sampleItems) {
            insertItemDirect(db, item)
        }

        // Prepopulate Store Order
        GroceryCategory.entries.forEachIndexed { index, cat ->
            val cv = ContentValues().apply {
                put("category", cat.id)
                put("sort_order", index)
            }
            db.insert(TABLE_STORE_ORDER, null, cv)
        }

        // Seed Frequent Items for instant suggestions
        val frequentSeed = listOf(
            FrequentItem("Milk", 2.0, "L", GroceryCategory.DAIRY, 8, now),
            FrequentItem("Eggs", 12.0, "pcs", GroceryCategory.DAIRY, 6, now),
            FrequentItem("Bread", 1.0, "loaf", GroceryCategory.BAKERY, 5, now),
            FrequentItem("Bananas", 1.0, "bunch", GroceryCategory.PRODUCE, 4, now),
            FrequentItem("Tomatoes", 1.0, "kg", GroceryCategory.PRODUCE, 5, now),
            FrequentItem("Coffee", 1.0, "pack", GroceryCategory.BEVERAGES, 3, now)
        )
        for (f in frequentSeed) {
            val fcv = ContentValues().apply {
                put("name", f.name)
                put("default_quantity", f.defaultQuantity)
                put("default_unit", f.defaultUnit)
                put("category", f.category.id)
                put("buy_count", f.buyCount)
                put("last_bought_at", f.lastBoughtAt)
            }
            db.insert(TABLE_FREQUENT_ITEMS, null, fcv)
        }
    }

    private fun insertItemDirect(db: SQLiteDatabase, item: GroceryItem) {
        val cv = ContentValues().apply {
            put("id", item.id)
            put("list_id", item.listId)
            put("name", item.name)
            put("quantity", item.quantity)
            put("unit", item.unit)
            put("category", item.category.id)
            put("is_completed", if (item.isCompleted) 1 else 0)
            put("estimated_price", item.estimatedPrice)
            put("actual_price", item.actualPrice)
            put("notes", item.notes)
            put("is_priority", if (item.isPriority) 1 else 0)
            put("created_at", item.createdAt)
            put("completed_at", item.completedAt)
            put("sort_order", item.sortOrder)
        }
        db.insert(TABLE_ITEMS, null, cv)
    }

    // CRUD for Lists
    fun getAllLists(): List<GroceryList> {
        val lists = mutableListOf<GroceryList>()
        val db = readableDatabase
        val cursor = db.query(TABLE_LISTS, null, null, null, null, null, "created_at ASC")
        cursor.use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val nameCol = c.getColumnIndexOrThrow("name")
            val emojiCol = c.getColumnIndexOrThrow("emoji")
            val budgetCol = c.getColumnIndexOrThrow("budget")
            val isDefCol = c.getColumnIndexOrThrow("is_default")
            val createdCol = c.getColumnIndexOrThrow("created_at")

            while (c.moveToNext()) {
                lists.add(
                    GroceryList(
                        id = c.getString(idCol),
                        name = c.getString(nameCol),
                        emoji = c.getString(emojiCol),
                        budget = if (c.isNull(budgetCol)) null else c.getDouble(budgetCol),
                        isDefault = c.getInt(isDefCol) == 1,
                        createdAt = c.getLong(createdCol)
                    )
                )
            }
        }
        return lists
    }

    fun insertList(list: GroceryList) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id", list.id)
            put("name", list.name)
            put("emoji", list.emoji)
            put("budget", list.budget)
            put("is_default", if (list.isDefault) 1 else 0)
            put("created_at", list.createdAt)
        }
        db.insertWithOnConflict(TABLE_LISTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateList(list: GroceryList) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("name", list.name)
            put("emoji", list.emoji)
            put("budget", list.budget)
        }
        db.update(TABLE_LISTS, cv, "id = ?", arrayOf(list.id))
    }

    fun deleteList(listId: String) {
        val db = writableDatabase
        db.delete(TABLE_ITEMS, "list_id = ?", arrayOf(listId))
        db.delete(TABLE_LISTS, "id = ?", arrayOf(listId))
    }

    // CRUD for Items
    fun getItemsForList(listId: String): List<GroceryItem> {
        val items = mutableListOf<GroceryItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ITEMS,
            null,
            "list_id = ?",
            arrayOf(listId),
            null,
            null,
            "is_completed ASC, sort_order ASC, created_at DESC"
        )
        cursor.use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val listIdCol = c.getColumnIndexOrThrow("list_id")
            val nameCol = c.getColumnIndexOrThrow("name")
            val qtyCol = c.getColumnIndexOrThrow("quantity")
            val unitCol = c.getColumnIndexOrThrow("unit")
            val catCol = c.getColumnIndexOrThrow("category")
            val compCol = c.getColumnIndexOrThrow("is_completed")
            val estPriceCol = c.getColumnIndexOrThrow("estimated_price")
            val actPriceCol = c.getColumnIndexOrThrow("actual_price")
            val notesCol = c.getColumnIndexOrThrow("notes")
            val prioCol = c.getColumnIndexOrThrow("is_priority")
            val createdCol = c.getColumnIndexOrThrow("created_at")
            val compAtCol = c.getColumnIndexOrThrow("completed_at")
            val sortCol = c.getColumnIndexOrThrow("sort_order")

            while (c.moveToNext()) {
                items.add(
                    GroceryItem(
                        id = c.getString(idCol),
                        listId = c.getString(listIdCol),
                        name = c.getString(nameCol),
                        quantity = c.getDouble(qtyCol),
                        unit = c.getString(unitCol),
                        category = GroceryCategory.fromId(c.getString(catCol)),
                        isCompleted = c.getInt(compCol) == 1,
                        estimatedPrice = if (c.isNull(estPriceCol)) null else c.getDouble(estPriceCol),
                        actualPrice = if (c.isNull(actPriceCol)) null else c.getDouble(actPriceCol),
                        notes = c.getString(notesCol),
                        isPriority = c.getInt(prioCol) == 1,
                        createdAt = c.getLong(createdCol),
                        completedAt = if (c.isNull(compAtCol)) null else c.getLong(compAtCol),
                        sortOrder = c.getInt(sortCol)
                    )
                )
            }
        }
        return items
    }

    fun insertItem(item: GroceryItem) {
        val db = writableDatabase
        insertItemDirect(db, item)
    }

    fun updateItem(item: GroceryItem) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("name", item.name)
            put("quantity", item.quantity)
            put("unit", item.unit)
            put("category", item.category.id)
            put("is_completed", if (item.isCompleted) 1 else 0)
            put("estimated_price", item.estimatedPrice)
            put("actual_price", item.actualPrice)
            put("notes", item.notes)
            put("is_priority", if (item.isPriority) 1 else 0)
            put("completed_at", item.completedAt)
            put("sort_order", item.sortOrder)
        }
        db.update(TABLE_ITEMS, cv, "id = ?", arrayOf(item.id))
    }

    fun deleteItem(itemId: String) {
        val db = writableDatabase
        db.delete(TABLE_ITEMS, "id = ?", arrayOf(itemId))
    }

    fun clearCompletedItems(listId: String): List<GroceryItem> {
        val cleared = mutableListOf<GroceryItem>()
        val db = writableDatabase
        val items = getItemsForList(listId).filter { it.isCompleted }
        cleared.addAll(items)
        db.delete(TABLE_ITEMS, "list_id = ? AND is_completed = 1", arrayOf(listId))
        return cleared
    }

    // Frequent items memory
    fun getFrequentItems(): List<FrequentItem> {
        val result = mutableListOf<FrequentItem>()
        val db = readableDatabase
        val cursor = db.query(TABLE_FREQUENT_ITEMS, null, null, null, null, null, "buy_count DESC, last_bought_at DESC LIMIT 20")
        cursor.use { c ->
            val nameCol = c.getColumnIndexOrThrow("name")
            val qtyCol = c.getColumnIndexOrThrow("default_quantity")
            val unitCol = c.getColumnIndexOrThrow("default_unit")
            val catCol = c.getColumnIndexOrThrow("category")
            val countCol = c.getColumnIndexOrThrow("buy_count")
            val dateCol = c.getColumnIndexOrThrow("last_bought_at")
            while (c.moveToNext()) {
                result.add(
                    FrequentItem(
                        name = c.getString(nameCol),
                        defaultQuantity = c.getDouble(qtyCol),
                        defaultUnit = c.getString(unitCol),
                        category = GroceryCategory.fromId(c.getString(catCol)),
                        buyCount = c.getInt(countCol),
                        lastBoughtAt = c.getLong(dateCol)
                    )
                )
            }
        }
        return result
    }

    fun recordFrequentPurchase(name: String, quantity: Double, unit: String, category: GroceryCategory) {
        val db = writableDatabase
        val cursor = db.query(TABLE_FREQUENT_ITEMS, arrayOf("buy_count"), "name = ?", arrayOf(name), null, null, null)
        val existingCount = cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }

        val cv = ContentValues().apply {
            put("name", name)
            put("default_quantity", quantity)
            put("default_unit", unit)
            put("category", category.id)
            put("buy_count", existingCount + 1)
            put("last_bought_at", System.currentTimeMillis())
        }
        db.insertWithOnConflict(TABLE_FREQUENT_ITEMS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // Store layout ordering
    fun getStoreCategoryOrder(): List<GroceryCategory> {
        val db = readableDatabase
        val cursor = db.query(TABLE_STORE_ORDER, null, null, null, null, null, "sort_order ASC")
        val order = mutableListOf<GroceryCategory>()
        cursor.use { c ->
            val catCol = c.getColumnIndexOrThrow("category")
            while (c.moveToNext()) {
                order.add(GroceryCategory.fromId(c.getString(catCol)))
            }
        }
        return if (order.isNotEmpty()) order else GroceryCategory.entries
    }

    fun saveStoreCategoryOrder(order: List<GroceryCategory>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_STORE_ORDER, null, null)
            order.forEachIndexed { index, category ->
                val cv = ContentValues().apply {
                    put("category", category.id)
                    put("sort_order", index)
                }
                db.insert(TABLE_STORE_ORDER, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
