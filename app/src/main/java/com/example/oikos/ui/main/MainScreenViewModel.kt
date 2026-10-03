package com.example.oikos.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oikos.data.repository.OikosRepository
import com.example.oikos.model.FrequentItem
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import com.example.oikos.model.GroceryList
import com.example.oikos.util.HapticFeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ActiveModal {
    data object None : ActiveModal()
    data class EditItem(val item: GroceryItem) : ActiveModal()
    data object ListManager : ActiveModal()
    data object StoreOrder : ActiveModal()
    data object Settings : ActiveModal()
    data object Search : ActiveModal()
    data object ShoppingMode : ActiveModal()
}

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OikosRepository(application)
    val hapticHelper = HapticFeedbackHelper(application) { repository.hapticsEnabled.value }

    val lists: StateFlow<List<GroceryList>> = repository.lists
    val activeListId: StateFlow<String> = repository.activeListId
    val items: StateFlow<List<GroceryItem>> = repository.items
    val frequentItems: StateFlow<List<FrequentItem>> = repository.frequentItems
    val storeOrder: StateFlow<List<GroceryCategory>> = repository.storeOrder
    val currencySymbol: StateFlow<String> = repository.currencySymbol
    val hapticsEnabled: StateFlow<Boolean> = repository.hapticsEnabled
    val keepScreenAwake: StateFlow<Boolean> = repository.keepScreenAwake
    val canUndo: StateFlow<Boolean> = repository.canUndo

    val currentList: StateFlow<GroceryList?> = combine(lists, activeListId) { allLists, id ->
        allLists.firstOrNull { it.id == id } ?: allLists.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _activeModal = MutableStateFlow<ActiveModal>(ActiveModal.None)
    val activeModal: StateFlow<ActiveModal> = _activeModal.asStateFlow()

    private val _collapsedCategories = MutableStateFlow<Set<GroceryCategory>>(emptySet())
    val collapsedCategories: StateFlow<Set<GroceryCategory>> = _collapsedCategories.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun showModal(modal: ActiveModal) {
        _activeModal.value = modal
    }

    fun dismissModal() {
        _activeModal.value = ActiveModal.None
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun addItem(item: GroceryItem) {
        viewModelScope.launch {
            val listId = activeListId.value
            repository.addItem(item.copy(listId = listId))
        }
    }

    fun incrementItemQuantity(existingItem: GroceryItem) {
        viewModelScope.launch {
            val step = if (existingItem.quantity < 1.0) 0.5 else 1.0
            val updated = existingItem.copy(quantity = existingItem.quantity + step)
            repository.updateItem(updated)
            _snackbarMessage.value = "Updated ${existingItem.name} to ${updated.formattedQuantity}"
        }
    }

    fun updateItem(item: GroceryItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun toggleItemCompletion(itemId: String) {
        viewModelScope.launch {
            repository.toggleItemCompletion(itemId)
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            hapticHelper.warning()
            repository.deleteItem(itemId)
            _snackbarMessage.value = "Item removed"
        }
    }

    fun clearCompletedItems() {
        viewModelScope.launch {
            val cleared = repository.clearCompletedItems()
            if (cleared.isNotEmpty()) {
                _snackbarMessage.value = "Cleared ${cleared.size} completed items"
            }
        }
    }

    fun undoLastAction() {
        viewModelScope.launch {
            val message = repository.undoLastAction()
            if (message != null) {
                _snackbarMessage.value = message
            }
        }
    }

    fun selectList(listId: String) {
        viewModelScope.launch {
            repository.setActiveList(listId)
        }
    }

    fun createList(name: String, emoji: String, budget: Double?) {
        viewModelScope.launch {
            repository.createList(name, emoji, budget)
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            repository.deleteList(listId)
        }
    }

    fun saveStoreOrder(order: List<GroceryCategory>) {
        viewModelScope.launch {
            repository.updateStoreOrder(order)
        }
    }

    fun finishShopping() {
        viewModelScope.launch {
            val cleared = repository.clearCompletedItems()
            _snackbarMessage.value = "Shopping complete! Saved ${cleared.size} items to history."
        }
    }

    fun toggleCategoryCollapsed(category: GroceryCategory) {
        val current = _collapsedCategories.value
        _collapsedCategories.value = if (current.contains(category)) {
            current - category
        } else {
            current + category
        }
    }

    fun setCurrency(symbol: String) {
        repository.setCurrencySymbol(symbol)
    }

    fun setHaptics(enabled: Boolean) {
        repository.setHapticsEnabled(enabled)
    }

    fun setKeepScreenAwake(awake: Boolean) {
        repository.setKeepScreenAwake(awake)
    }

    suspend fun exportBackup(): String {
        return repository.exportBackupJson()
    }

    suspend fun importBackup(jsonStr: String): Boolean {
        return repository.importBackupJson(jsonStr)
    }
}
