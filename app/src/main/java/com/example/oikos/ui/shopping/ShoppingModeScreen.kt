package com.example.oikos.ui.shopping

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import com.example.oikos.model.GroceryList
import com.example.oikos.theme.OikosBackground
import com.example.oikos.theme.OikosError
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosForestGreenDark
import com.example.oikos.theme.OikosOnForestGreen
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary
import com.example.oikos.theme.OikosWarmHoney
import com.example.oikos.theme.OikosWarmHoneyContainer
import com.example.oikos.util.CurrencyFormatter

@Composable
fun ShoppingModeScreen(
    currentList: GroceryList,
    items: List<GroceryItem>,
    storeOrder: List<GroceryCategory>,
    currencySymbol: String,
    keepScreenAwake: Boolean,
    onToggleCompletion: (String) -> Unit,
    onFinishShopping: () -> Unit,
    onExitShoppingMode: () -> Unit,
    onHapticConfirm: () -> Unit,
    onHapticMilestone: () -> Unit
) {
    val context = LocalContext.current

    // Keep screen awake while shopping
    DisposableEffect(keepScreenAwake) {
        val window = (context as? Activity)?.window
        if (keepScreenAwake) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    var showFinishModal by remember { mutableStateOf(false) }
    var lastToggledItemId by remember { mutableStateOf<String?>(null) }
    var lastToggledItemName by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val totalCount = items.size
    val completedCount = items.count { it.isCompleted }
    val remainingCount = totalCount - completedCount
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "shop_prog")

    val checkedTotal = items.filter { it.isCompleted }.sumOf { (it.displayPrice ?: 0.0) * it.quantity }
    val estimatedTotal = items.sumOf { (it.displayPrice ?: 0.0) * it.quantity }

    // Group items according to Store Walking Order!
    val groupedItemsByStoreOrder by remember(items, storeOrder) {
        derivedStateOf {
            val orderedCats = if (storeOrder.isNotEmpty()) storeOrder else GroceryCategory.entries
            orderedCats.mapNotNull { category ->
                val categoryItems = items.filter { it.category == category }
                if (categoryItems.isNotEmpty()) category to categoryItems else null
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OikosBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = OikosSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onExitShoppingMode) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Shopping Mode",
                            tint = OikosTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(OikosForestGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SHOPPING MODE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = OikosForestGreen
                                )
                            )
                        }
                        Text(
                            text = currentList.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OikosTextPrimary
                            )
                        )
                    }

                    // Live Cart Cost Pill
                    if (estimatedTotal > 0.0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = OikosForestGreenContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, OikosForestGreen.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = CurrencyFormatter.format(checkedTotal, currencySymbol),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OikosForestGreen
                                )
                            )
                        }
                    }
                }
            }

            // Progress & Budget Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = OikosSurfaceBorder.copy(alpha = 0.25f)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (remainingCount == 0 && totalCount > 0) "All items checked!" else "$remainingCount items remaining",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (remainingCount == 0 && totalCount > 0) OikosForestGreen else OikosTextPrimary
                            )
                        )
                        Text(
                            text = "$completedCount of $totalCount",
                            style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = OikosForestGreen,
                        trackColor = OikosSurfaceBorder
                    )

                    // Budget Gauge if list has a defined budget
                    if (currentList.budget != null) {
                        val budget = currentList.budget
                        val remainingBudget = budget - checkedTotal
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Budget: ${CurrencyFormatter.format(budget, currencySymbol)}",
                                style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary)
                            )
                            Text(
                                text = if (remainingBudget >= 0) "${CurrencyFormatter.format(remainingBudget, currencySymbol)} remaining" else "Over budget by ${CurrencyFormatter.format(-remainingBudget, currencySymbol)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (remainingBudget >= 0) OikosForestGreen else OikosError
                                )
                            )
                        }
                    }
                }
            }

            // Aisle Quick-Jump Bar
            if (groupedItemsByStoreOrder.size > 1) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = OikosSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        groupedItemsByStoreOrder.forEachIndexed { index, (category, catItems) ->
                            val pendingInCat = catItems.count { !it.isCompleted }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (pendingInCat == 0) OikosSurfaceBorder.copy(alpha = 0.5f) else category.getAdaptiveTagBgColor(),
                                modifier = Modifier.clickable {
                                    val targetIndex = groupedItemsByStoreOrder.take(index).sumOf { 1 + it.second.size }
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(targetIndex)
                                    }
                                }
                            ) {
                                Text(
                                    text = "${category.displayName} ${if (pendingInCat == 0) "✓" else "($pendingInCat)"}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (pendingInCat == 0) OikosTextTertiary else category.getAdaptiveTagTextColor()
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Main List Ordered by Store Layout
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                groupedItemsByStoreOrder.forEach { (category, catItems) ->
                    // Category Walking Header
                    item(key = "header_${category.id}") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(category.getAdaptiveTagBgColor())
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = category.displayName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = category.getAdaptiveTagTextColor(),
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            val pendingInCat = catItems.count { !it.isCompleted }
                            Text(
                                text = if (pendingInCat == 0) "Done ✓" else "$pendingInCat remaining",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (pendingInCat == 0) OikosForestGreen else OikosTextTertiary,
                                    fontWeight = if (pendingInCat == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }

                    // Large Accessible One-Handed Shopping Items
                    items(catItems, key = { it.id }) { item ->
                        val itemScale by animateFloatAsState(
                            targetValue = if (item.isCompleted) 1.04f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                            label = "shop_box_scale"
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .scale(itemScale)
                                .clip(RoundedCornerShape(16.dp))
                                .semantics {
                                    contentDescription = "${item.name}, ${item.formattedQuantity}, aisle ${item.category.displayName}, ${if (item.isCompleted) "Checked" else "Not checked"}"
                                }
                                .clickable {
                                    onHapticConfirm()
                                    val wasCompleted = item.isCompleted
                                    onToggleCompletion(item.id)
                                    if (!wasCompleted) {
                                        lastToggledItemId = item.id
                                        lastToggledItemName = item.name
                                        if (remainingCount == 1) {
                                            onHapticMilestone()
                                        }
                                    } else if (lastToggledItemId == item.id) {
                                        lastToggledItemId = null
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (item.isCompleted) OikosSurface.copy(alpha = 0.6f) else OikosSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (item.isCompleted) 1.dp else 1.5.dp,
                                color = if (item.isCompleted) OikosSurfaceBorder.copy(alpha = 0.5f) else OikosForestGreen.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Large 36dp Checkbox Target (Super comfortable with thumb in store)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (item.isCompleted) OikosForestGreen else Color.Transparent)
                                        .border(
                                            width = 2.dp,
                                            color = if (item.isCompleted) OikosForestGreen else OikosTextTertiary,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (item.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Checked",
                                            tint = OikosOnForestGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Item Details
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (item.isCompleted) FontWeight.Normal else FontWeight.Bold,
                                            color = if (item.isCompleted) OikosTextTertiary else OikosTextPrimary,
                                            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (!item.notes.isNullOrBlank()) {
                                        Text(
                                            text = item.notes,
                                            style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Large Quantity Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (item.isCompleted) OikosSurfaceBorder.copy(alpha = 0.5f) else OikosForestGreenContainer)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.formattedQuantity,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isCompleted) OikosTextTertiary else OikosForestGreen
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Inline Undo Floating Banner
            AnimatedVisibility(
                visible = lastToggledItemId != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = OikosForestGreen,
                    contentColor = OikosOnForestGreen,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Checked $lastToggledItemName",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    lastToggledItemId?.let { id ->
                                        onToggleCompletion(id)
                                        onHapticConfirm()
                                    }
                                    lastToggledItemId = null
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo toggle",
                                tint = OikosWarmHoney,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Undo",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = OikosWarmHoney,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Floating Finish Shopping Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = OikosSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onExitShoppingMode,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Exit Mode")
                    }

                    Button(
                        onClick = {
                            onHapticMilestone()
                            showFinishModal = true
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OikosForestGreen,
                            contentColor = OikosOnForestGreen
                        )
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (remainingCount == 0 && totalCount > 0) "Finish Trip 🎉" else "Finish Shopping",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Finish Shopping Celebration Modal
    if (showFinishModal) {
        AlertDialog(
            onDismissRequest = { showFinishModal = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(OikosForestGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Celebration,
                        contentDescription = null,
                        tint = OikosForestGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Shopping Trip Complete!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You've checked off $completedCount of $totalCount items.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = OikosTextSecondary),
                        textAlign = TextAlign.Center
                    )
                    if (checkedTotal > 0.0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Estimated Spend: ${CurrencyFormatter.format(checkedTotal, currencySymbol)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OikosForestGreen
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Would you like to clear completed items from this list and save them to your grocery memory?",
                        style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishModal = false
                        onFinishShopping()
                        onExitShoppingMode()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OikosForestGreen,
                        contentColor = OikosOnForestGreen
                    )
                ) {
                    Text("Clear Checked & Finish")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showFinishModal = false
                        onExitShoppingMode()
                    }
                ) {
                    Text("Keep Items on List")
                }
            }
        )
    }
}
