package com.example.oikos.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oikos.model.GroceryCategory
import com.example.oikos.theme.OikosBackground
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosOnForestGreen
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary
import com.example.oikos.theme.OikosWarmHoney
import com.example.oikos.ui.components.CategorySectionHeader
import com.example.oikos.ui.components.EmptyListState
import com.example.oikos.ui.components.GroceryItemRow
import com.example.oikos.ui.components.ItemDetailSheet
import com.example.oikos.ui.components.ListManagerSheet
import com.example.oikos.ui.components.OikosProgressCard
import com.example.oikos.ui.components.OikosTopBar
import com.example.oikos.ui.components.SearchDialog
import com.example.oikos.ui.components.SettingsSheet
import com.example.oikos.ui.components.SmartAddBar
import com.example.oikos.ui.components.StoreOrderSheet
import com.example.oikos.ui.shopping.ShoppingModeScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel()
) {
    val lists by viewModel.lists.collectAsStateWithLifecycle()
    val currentList by viewModel.currentList.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()
    val frequentItems by viewModel.frequentItems.collectAsStateWithLifecycle()
    val storeOrder by viewModel.storeOrder.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val keepScreenAwake by viewModel.keepScreenAwake.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val smartRestockSuggestions by viewModel.smartRestockSuggestions.collectAsStateWithLifecycle()
    val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()
    val activeModal by viewModel.activeModal.collectAsStateWithLifecycle()
    val collapsedCategories by viewModel.collapsedCategories.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = if (canUndo) "Undo" else null,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoLastAction()
            }
            viewModel.clearSnackbarMessage()
        }
    }

    // Separate active vs completed items
    val activeItems by remember(items) {
        derivedStateOf { items.filter { !it.isCompleted } }
    }
    val completedItems by remember(items) {
        derivedStateOf { items.filter { it.isCompleted } }
    }

    // Group active items by Category according to Store Walking Order
    val groupedActiveItems by remember(activeItems, storeOrder) {
        derivedStateOf {
            val orderedCats = if (storeOrder.isNotEmpty()) storeOrder else GroceryCategory.entries
            orderedCats.mapNotNull { cat ->
                val catItems = activeItems.filter { it.category == cat }
                if (catItems.isNotEmpty()) cat to catItems else null
            }
        }
    }

    // Full Screen Shopping Mode Overlay
    if (activeModal is ActiveModal.ShoppingMode && currentList != null) {
        ShoppingModeScreen(
            currentList = currentList!!,
            items = items,
            storeOrder = storeOrder,
            currencySymbol = currencySymbol,
            keepScreenAwake = keepScreenAwake,
            onToggleCompletion = { viewModel.toggleItemCompletion(it) },
            onFinishShopping = { viewModel.finishShopping() },
            onExitShoppingMode = { viewModel.dismissModal() },
            onHapticConfirm = { viewModel.hapticHelper.confirm() },
            onHapticMilestone = { viewModel.hapticHelper.milestone() }
        )
        return
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            OikosTopBar(
                currentList = currentList,
                lists = lists,
                onSelectList = { viewModel.selectList(it) },
                onOpenSearch = { viewModel.showModal(ActiveModal.Search) },
                onOpenShoppingMode = { viewModel.showModal(ActiveModal.ShoppingMode) },
                onOpenListManager = { viewModel.showModal(ActiveModal.ListManager) },
                onOpenStoreOrder = { viewModel.showModal(ActiveModal.StoreOrder) },
                onOpenSettings = { viewModel.showModal(ActiveModal.Settings) }
            )
        },
        floatingActionButton = {
            if (items.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.showModal(ActiveModal.ShoppingMode) },
                    containerColor = OikosForestGreen,
                    contentColor = OikosOnForestGreen,
                    shape = RoundedCornerShape(20.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Start Shopping"
                        )
                    },
                    text = {
                        Text(
                            text = "Shopping Mode",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = OikosForestGreen,
                    contentColor = OikosOnForestGreen,
                    actionColor = OikosWarmHoney,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = OikosBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Progress & Stats Card
            if (items.isNotEmpty()) {
                item(key = "progress_card") {
                    OikosProgressCard(
                        items = items,
                        budget = currentList?.budget,
                        currencySymbol = currencySymbol
                    )
                }
            }

            // Smart NLP Item Input
            item(key = "smart_add_bar") {
                SmartAddBar(
                    existingItems = items,
                    frequentItems = frequentItems,
                    currencySymbol = currencySymbol,
                    onAddItem = { viewModel.addItem(it) },
                    onAddItems = { viewModel.addItems(it) },
                    onIncrementExistingItem = { viewModel.incrementItemQuantity(it) },
                    onHapticTick = { viewModel.hapticHelper.tick() }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Personal Grocery Intelligence: Smart Restock Suggestions
            if (smartRestockSuggestions.isNotEmpty()) {
                item(key = "smart_restock_section") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = OikosWarmHoney,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Smart Restock Suggestions",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OikosTextSecondary,
                                    letterSpacing = 0.4.sp
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            smartRestockSuggestions.forEach { frequent ->
                                Surface(
                                    onClick = { viewModel.restockItem(frequent) },
                                    shape = RoundedCornerShape(20.dp),
                                    color = OikosSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, OikosForestGreen.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = OikosForestGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${frequent.name} (${if (frequent.defaultQuantity % 1.0 == 0.0) frequent.defaultQuantity.toInt().toString() else frequent.defaultQuantity.toString()} ${frequent.defaultUnit})",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = OikosTextPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Empty State
            if (items.isEmpty()) {
                item(key = "empty_state") {
                    EmptyListState(
                        onAddSuggestedItem = { viewModel.addItem(it) }
                    )
                }
            } else {
                // Active Categories and Items
                groupedActiveItems.forEach { (category, catItems) ->
                    val isCollapsed = collapsedCategories.contains(category)

                    item(key = "cat_header_${category.id}") {
                        CategorySectionHeader(
                            category = category,
                            itemCount = catItems.size,
                            isExpanded = !isCollapsed,
                            onToggleExpand = { viewModel.toggleCategoryCollapsed(category) }
                        )
                    }

                    if (!isCollapsed) {
                        items(catItems, key = { it.id }) { item ->
                            GroceryItemRow(
                                item = item,
                                currencySymbol = currencySymbol,
                                onToggleCompletion = { viewModel.toggleItemCompletion(it) },
                                onEditItem = { viewModel.showModal(ActiveModal.EditItem(it)) },
                                onDeleteItem = { viewModel.deleteItem(it) },
                                onHapticConfirm = { viewModel.hapticHelper.confirm() }
                            )
                        }
                    }
                }

                // Completed Items Section
                if (completedItems.isNotEmpty()) {
                    item(key = "completed_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMPLETED (${completedItems.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OikosTextTertiary,
                                    letterSpacing = 1.sp
                                )
                            )

                            TextButton(
                                onClick = { viewModel.clearCompletedItems() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteSweep,
                                    contentDescription = null,
                                    tint = OikosTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Clear",
                                    style = MaterialTheme.typography.labelMedium.copy(color = OikosTextSecondary)
                                )
                            }
                        }
                    }

                    items(completedItems, key = { it.id }) { item ->
                        GroceryItemRow(
                            item = item,
                            currencySymbol = currencySymbol,
                            onToggleCompletion = { viewModel.toggleItemCompletion(it) },
                            onEditItem = { viewModel.showModal(ActiveModal.EditItem(it)) },
                            onDeleteItem = { viewModel.deleteItem(it) },
                            onHapticConfirm = { viewModel.hapticHelper.confirm() }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets
    when (val modal = activeModal) {
        is ActiveModal.EditItem -> {
            ItemDetailSheet(
                item = modal.item,
                currencySymbol = currencySymbol,
                onSaveItem = { viewModel.updateItem(it) },
                onDeleteItem = { viewModel.deleteItem(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }
        is ActiveModal.ListManager -> {
            ListManagerSheet(
                lists = lists,
                activeListId = viewModel.activeListId.collectAsStateWithLifecycle().value,
                currencySymbol = currencySymbol,
                onSelectList = { viewModel.selectList(it) },
                onCreateList = { name, emoji, budget -> viewModel.createList(name, emoji, budget) },
                onDeleteList = { viewModel.deleteList(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }
        is ActiveModal.StoreOrder -> {
            StoreOrderSheet(
                currentOrder = storeOrder,
                onSaveOrder = { viewModel.saveStoreOrder(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }
        is ActiveModal.Settings -> {
            SettingsSheet(
                currencySymbol = currencySymbol,
                themeMode = themeMode,
                hapticsEnabled = hapticsEnabled,
                keepScreenAwake = keepScreenAwake,
                onSetCurrency = { viewModel.setCurrency(it) },
                onSetThemeMode = { viewModel.setThemeMode(it) },
                onSetHaptics = { viewModel.setHaptics(it) },
                onSetKeepScreenAwake = { viewModel.setKeepScreenAwake(it) },
                onExportBackup = { viewModel.exportBackup() },
                onImportBackup = { viewModel.importBackup(it) },
                onDismiss = { viewModel.dismissModal() }
            )
        }
        is ActiveModal.Search -> {
            SearchDialog(
                items = items,
                currencySymbol = currencySymbol,
                onToggleCompletion = { viewModel.toggleItemCompletion(it) },
                onEditItem = { viewModel.showModal(ActiveModal.EditItem(it)) },
                onDeleteItem = { viewModel.deleteItem(it) },
                onHapticConfirm = { viewModel.hapticHelper.confirm() },
                onDismiss = { viewModel.dismissModal() }
            )
        }
        else -> Unit
    }
}
