package com.example.oikos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryCategory
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosOnForestGreen
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreOrderSheet(
    currentOrder: List<GroceryCategory>,
    onSaveOrder: (List<GroceryCategory>) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var listOrder by remember { mutableStateOf(currentOrder) }

    fun moveCategory(index: Int, up: Boolean) {
        val targetIndex = if (up) index - 1 else index + 1
        if (targetIndex in listOrder.indices) {
            val mutable = listOrder.toMutableList()
            val item = mutable.removeAt(index)
            mutable.add(targetIndex, item)
            listOrder = mutable
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = OikosSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Store Layout Walking Order",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OikosTextPrimary
                        )
                    )
                    Text(
                        text = "Arrange categories to follow your local store aisle order",
                        style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary)
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                    }
                }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = OikosTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reorderable list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                itemsIndexed(listOrder) { index, category ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = OikosSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OikosTextTertiary
                                ),
                                modifier = Modifier.width(24.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(category.getAdaptiveTagBgColor())
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = category.getAdaptiveTagTextColor(),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Move Up
                            IconButton(
                                onClick = { moveCategory(index, up = true) },
                                enabled = index > 0,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move Up",
                                    tint = if (index > 0) OikosForestGreen else OikosTextTertiary.copy(alpha = 0.3f)
                                )
                            }

                            // Move Down
                            IconButton(
                                onClick = { moveCategory(index, up = false) },
                                enabled = index < listOrder.lastIndex,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move Down",
                                    tint = if (index < listOrder.lastIndex) OikosForestGreen else OikosTextTertiary.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        listOrder = GroceryCategory.entries
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset Default")
                }

                Button(
                    onClick = {
                        onSaveOrder(listOrder)
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OikosForestGreen,
                        contentColor = OikosOnForestGreen
                    )
                ) {
                    Text("Save Walking Order")
                }
            }
        }
    }
}
