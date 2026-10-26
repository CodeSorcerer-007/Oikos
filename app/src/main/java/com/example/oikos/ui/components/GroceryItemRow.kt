package com.example.oikos.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryItem
import com.example.oikos.theme.OikosError
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosOnForestGreen
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary
import com.example.oikos.theme.OikosWarmHoney
import com.example.oikos.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryItemRow(
    item: GroceryItem,
    currencySymbol: String,
    onToggleCompletion: (String) -> Unit,
    onEditItem: (GroceryItem) -> Unit,
    onDeleteItem: (String) -> Unit,
    onHapticConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val checkboxScale by animateFloatAsState(
        targetValue = if (item.isCompleted) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f),
        label = "checkbox_scale"
    )

    val itemTextColor by animateColorAsState(
        targetValue = if (item.isCompleted) OikosTextTertiary else OikosTextPrimary,
        animationSpec = tween(200),
        label = "item_text_color"
    )

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onHapticConfirm()
                    onToggleCompletion(item.id)
                    false // Return false so the item bounces back gracefully and re-renders
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onHapticConfirm()
                    onDeleteItem(item.id)
                    true // Dismisses the item card
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val backgroundColor by animateColorAsState(
                targetValue = when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> OikosForestGreen
                    SwipeToDismissBoxValue.EndToStart -> OikosError
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "swipe_bg_color"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Complete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (item.isCompleted) "Uncheck" else "Complete",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                } else if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Delete",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        content = {
            Surface(
                onClick = { onEditItem(item) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (item.isCompleted) OikosSurface.copy(alpha = 0.65f) else OikosSurface,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (item.isCompleted) OikosSurfaceBorder.copy(alpha = 0.4f) else OikosSurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tactile Checkbox Button
                    Box(
                        modifier = Modifier
                            .scale(checkboxScale)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (item.isCompleted) OikosForestGreen else Color.Transparent)
                            .border(
                                width = 1.8.dp,
                                color = if (item.isCompleted) OikosForestGreen else OikosTextTertiary.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                            .clickable {
                                onHapticConfirm()
                                onToggleCompletion(item.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = OikosOnForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Main Details Column
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Item Name Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Priority dot if marked
                            if (item.isPriority && !item.isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(OikosWarmHoney)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (item.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                                    color = itemTextColor,
                                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Subtitle Pills Row: Quantity, Category, Price
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Quantity Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (item.isCompleted) OikosSurfaceBorder.copy(alpha = 0.5f) else OikosForestGreenContainer.copy(alpha = 0.7f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.formattedQuantity,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isCompleted) OikosTextTertiary else OikosForestGreen
                                    )
                                )
                            }

                            // Category Tag (Adaptive)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (item.isCompleted) OikosSurfaceBorder.copy(alpha = 0.3f) else item.category.getAdaptiveTagBgColor())
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.category.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (item.isCompleted) OikosTextTertiary else item.category.getAdaptiveTagTextColor()
                                    )
                                )
                            }

                            // Price Tag if available
                            if (item.displayPrice != null) {
                                Text(
                                    text = CurrencyFormatter.format(item.displayPrice!! * item.quantity, currencySymbol),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (item.isCompleted) OikosTextTertiary else OikosTextSecondary
                                    )
                                )
                            }

                            // Note Indicator
                            if (!item.notes.isNullOrBlank()) {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = "Has notes",
                                    tint = OikosTextTertiary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // Quick More Options Menu
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Item Actions",
                                tint = OikosTextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(OikosSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Details", color = OikosTextPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = OikosTextSecondary)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEditItem(item)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (item.isPriority) "Remove Priority" else "Mark as Priority", color = OikosTextPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Flag, contentDescription = null, tint = OikosWarmHoney)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEditItem(item.copy(isPriority = !item.isPriority))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = OikosError) },
                                leadingIcon = {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = OikosError)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteItem(item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
