package com.example.oikos.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.oikos.data.db.OikosDatabaseHelper
import com.example.oikos.model.FrequentItem
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import com.example.oikos.model.GroceryList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Stack
import java.util.UUID

sealed class UndoAction {
    data class ItemDeleted(val item: GroceryItem) : UndoAction()
    data class ItemCompletionToggled(val itemId: String, val previousState: Boolean) : UndoAction()
    data class ItemsCleared(val items: List<GroceryItem>) : UndoAction()
}

class OikosRepository(context: Context) {

    private val dbHelper = OikosDatabaseHelper(context.applicationContext)
    private val prefs: SharedPreferences = context.getSharedPreferences("oikos_preferences", Context.MODE_PRIVATE)
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _lists = MutableStateFlow<List<GroceryList>>(emptyList())
    val lists: StateFlow<List<GroceryList>> = _lists.asStateFlow()

    private val _activeListId = MutableStateFlow(prefs.getString("active_list_id", "default_weekly") ?: "default_weekly")
    val activeListId: StateFlow<String> = _activeListId.asStateFlow()

    private val _items = MutableStateFlow<List<GroceryItem>>(emptyList())
    val items: StateFlow<List<GroceryItem>> = _items.asStateFlow()

    private val _frequentItems = MutableStateFlow<List<FrequentItem>>(emptyList())
    val frequentItems: StateFlow<List<FrequentItem>> = _frequentItems.asStateFlow()

    private val _storeOrder = MutableStateFlow<List<GroceryCategory>>(emptyList())
    val storeOrder: StateFlow<List<GroceryCategory>> = _storeOrder.asStateFlow()

    private val _currencySymbol = MutableStateFlow(prefs.getString("currency_symbol", "₹") ?: "₹")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean("haptics_enabled", true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _keepScreenAwake = MutableStateFlow(prefs.getBoolean("keep_screen_awake", true))
    val keepScreenAwake: StateFlow<Boolean> = _keepScreenAwake.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val undoStack = Stack<UndoAction>()
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        repositoryScope.launch {
            refreshLists()
            refreshItemsForActiveList()
            refreshFrequentItems()
            refreshStoreOrder()
        }
    }

    private suspend fun refreshLists() = withContext(Dispatchers.IO) {
        val loadedLists = dbHelper.getAllLists()
        _lists.value = loadedLists

        // If active list not in loaded lists, pick first
        if (loadedLists.isNotEmpty() && loadedLists.none { it.id == _activeListId.value }) {
            val fallback = loadedLists.first().id
            _activeListId.value = fallback
            prefs.edit().putString("active_list_id", fallback).apply()
        }
    }

    private val appContext = context.applicationContext

    private suspend fun refreshItemsForActiveList() = withContext(Dispatchers.IO) {
        val loadedItems = dbHelper.getItemsForList(_activeListId.value)
        _items.value = loadedItems
        try {
            com.example.oikos.widget.OikosAppWidgetProvider.updateAll(appContext)
        } catch (_: Exception) {}
    }

    private suspend fun refreshFrequentItems() = withContext(Dispatchers.IO) {
        _frequentItems.value = dbHelper.getFrequentItems()
    }

    private suspend fun refreshStoreOrder() = withContext(Dispatchers.IO) {
        _storeOrder.value = dbHelper.getStoreCategoryOrder()
    }

    suspend fun setActiveList(listId: String) = withContext(Dispatchers.IO) {
        _activeListId.value = listId
        prefs.edit().putString("active_list_id", listId).apply()
        refreshItemsForActiveList()
    }

    suspend fun addItem(item: GroceryItem): GroceryItem = withContext(Dispatchers.IO) {
        val targetListId = item.listId.ifBlank { _activeListId.value }
        val itemToInsert = item.copy(listId = targetListId)
        dbHelper.insertItem(itemToInsert)
        refreshItemsForActiveList()
        itemToInsert
    }

    suspend fun addItems(items: List<GroceryItem>): List<GroceryItem> = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext emptyList()
        val targetListId = _activeListId.value
        val itemsToInsert = items.map { it.copy(listId = if (it.listId.isBlank()) targetListId else it.listId) }
        dbHelper.insertItems(itemsToInsert)
        refreshItemsForActiveList()
        itemsToInsert
    }

    suspend fun updateItem(item: GroceryItem) = withContext(Dispatchers.IO) {
        dbHelper.updateItem(item)
        refreshItemsForActiveList()
    }

    suspend fun toggleItemCompletion(itemId: String) = withContext(Dispatchers.IO) {
        val currentItem = _items.value.firstOrNull { it.id == itemId } ?: return@withContext
        val newCompleted = !currentItem.isCompleted
        val updated = currentItem.copy(
            isCompleted = newCompleted,
            completedAt = if (newCompleted) System.currentTimeMillis() else null
        )
        dbHelper.updateItem(updated)

        // Track frequent purchase if completed
        if (newCompleted) {
            dbHelper.recordFrequentPurchase(
                name = updated.name,
                quantity = updated.quantity,
                unit = updated.unit,
                category = updated.category
            )
            refreshFrequentItems()
        }

        undoStack.push(UndoAction.ItemCompletionToggled(itemId, currentItem.isCompleted))
        _canUndo.value = true
        refreshItemsForActiveList()
    }

    suspend fun deleteItem(itemId: String) = withContext(Dispatchers.IO) {
        val currentItem = _items.value.firstOrNull { it.id == itemId } ?: return@withContext
        dbHelper.deleteItem(itemId)
        undoStack.push(UndoAction.ItemDeleted(currentItem))
        _canUndo.value = true
        refreshItemsForActiveList()
    }

    suspend fun clearCompletedItems(): List<GroceryItem> = withContext(Dispatchers.IO) {
        val cleared = dbHelper.clearCompletedItems(_activeListId.value)
        if (cleared.isNotEmpty()) {
            undoStack.push(UndoAction.ItemsCleared(cleared))
            _canUndo.value = true
        }
        refreshItemsForActiveList()
        cleared
    }

    suspend fun undoLastAction(): String? = withContext(Dispatchers.IO) {
        if (undoStack.isEmpty()) return@withContext null
        val action = undoStack.pop()
        _canUndo.value = undoStack.isNotEmpty()

        when (action) {
            is UndoAction.ItemDeleted -> {
                dbHelper.insertItem(action.item)
                refreshItemsForActiveList()
                "Restored ${action.item.name}"
            }
            is UndoAction.ItemCompletionToggled -> {
                val item = _items.value.firstOrNull { it.id == action.itemId }
                if (item != null) {
                    val reverted = item.copy(
                        isCompleted = action.previousState,
                        completedAt = if (action.previousState) item.completedAt ?: System.currentTimeMillis() else null
                    )
                    dbHelper.updateItem(reverted)
                    refreshItemsForActiveList()
                    "Reverted ${item.name}"
                } else null
            }
            is UndoAction.ItemsCleared -> {
                for (item in action.items) {
                    dbHelper.insertItem(item)
                }
                refreshItemsForActiveList()
                "Restored ${action.items.size} completed items"
            }
        }
    }

    suspend fun createList(name: String, emoji: String = "🛒", budget: Double? = null): GroceryList = withContext(Dispatchers.IO) {
        val newList = GroceryList(
            id = UUID.randomUUID().toString(),
            name = name,
            emoji = emoji,
            budget = budget,
            isDefault = false
        )
        dbHelper.insertList(newList)
        refreshLists()
        setActiveList(newList.id)
        newList
    }

    suspend fun updateList(list: GroceryList) = withContext(Dispatchers.IO) {
        dbHelper.updateList(list)
        refreshLists()
    }

    suspend fun deleteList(listId: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteList(listId)
        refreshLists()
        val remaining = dbHelper.getAllLists()
        if (remaining.isNotEmpty()) {
            setActiveList(remaining.first().id)
        }
    }

    suspend fun updateStoreOrder(newOrder: List<GroceryCategory>) = withContext(Dispatchers.IO) {
        dbHelper.saveStoreCategoryOrder(newOrder)
        _storeOrder.value = newOrder
    }

    fun setCurrencySymbol(symbol: String) {
        _currencySymbol.value = symbol
        prefs.edit().putString("currency_symbol", symbol).apply()
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
    }

    fun setKeepScreenAwake(awake: Boolean) {
        _keepScreenAwake.value = awake
        prefs.edit().putBoolean("keep_screen_awake", awake).apply()
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
    }

    // Export JSON Backup
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", 1)
        root.put("app", "Oikos")
        root.put("exportedAt", System.currentTimeMillis())

        val listsArray = JSONArray()
        val allLists = dbHelper.getAllLists()
        for (l in allLists) {
            val listObj = JSONObject().apply {
                put("id", l.id)
                put("name", l.name)
                put("emoji", l.emoji)
                put("budget", l.budget ?: JSONObject.NULL)
                put("isDefault", l.isDefault)
                put("createdAt", l.createdAt)

                val itemsArray = JSONArray()
                val items = dbHelper.getItemsForList(l.id)
                for (it in items) {
                    val itemObj = JSONObject().apply {
                        put("id", it.id)
                        put("name", it.name)
                        put("quantity", it.quantity)
                        put("unit", it.unit)
                        put("category", it.category.id)
                        put("isCompleted", it.isCompleted)
                        put("estimatedPrice", it.estimatedPrice ?: JSONObject.NULL)
                        put("actualPrice", it.actualPrice ?: JSONObject.NULL)
                        put("notes", it.notes ?: JSONObject.NULL)
                        put("isPriority", it.isPriority)
                        put("createdAt", it.createdAt)
                        put("completedAt", it.completedAt ?: JSONObject.NULL)
                    }
                    itemsArray.put(itemObj)
                }
                put("items", itemsArray)
            }
            listsArray.put(listObj)
        }
        root.put("lists", listsArray)
        root.toString(2)
    }

    // Import JSON Backup
    suspend fun importBackupJson(jsonStr: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonStr)
            val listsArray = root.getJSONArray("lists")
            for (i in 0 until listsArray.length()) {
                val listObj = listsArray.getJSONObject(i)
                val listId = listObj.getString("id")
                val list = GroceryList(
                    id = listId,
                    name = listObj.getString("name"),
                    emoji = listObj.optString("emoji", "🛒"),
                    budget = if (listObj.isNull("budget")) null else listObj.getDouble("budget"),
                    isDefault = listObj.optBoolean("isDefault", false),
                    createdAt = listObj.optLong("createdAt", System.currentTimeMillis())
                )
                dbHelper.insertList(list)

                val itemsArray = listObj.optJSONArray("items")
                if (itemsArray != null) {
                    for (j in 0 until itemsArray.length()) {
                        val itemObj = itemsArray.getJSONObject(j)
                        val item = GroceryItem(
                            id = itemObj.getString("id"),
                            listId = listId,
                            name = itemObj.getString("name"),
                            quantity = itemObj.optDouble("quantity", 1.0),
                            unit = itemObj.optString("unit", "pcs"),
                            category = GroceryCategory.fromId(itemObj.optString("category", "other")),
                            isCompleted = itemObj.optBoolean("isCompleted", false),
                            estimatedPrice = if (itemObj.isNull("estimatedPrice")) null else itemObj.getDouble("estimatedPrice"),
                            actualPrice = if (itemObj.isNull("actualPrice")) null else itemObj.getDouble("actualPrice"),
                            notes = if (itemObj.isNull("notes")) null else itemObj.getString("notes"),
                            isPriority = itemObj.optBoolean("isPriority", false),
                            createdAt = itemObj.optLong("createdAt", System.currentTimeMillis()),
                            completedAt = if (itemObj.isNull("completedAt")) null else itemObj.getLong("completedAt")
                        )
                        dbHelper.insertItem(item)
                    }
                }
            }
            refreshLists()
            refreshItemsForActiveList()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
