package com.example.oikos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryCategory
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
import com.example.oikos.theme.OikosWarmHoneyContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailSheet(
    item: GroceryItem,
    currencySymbol: String,
    onSaveItem: (GroceryItem) -> Unit,
    onDeleteItem: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableDoubleStateOf(item.quantity) }
    var unit by remember { mutableStateOf(item.unit) }
    var selectedCategory by remember { mutableStateOf(item.category) }
    var priceText by remember { mutableStateOf(item.estimatedPrice?.toString() ?: "") }
    var notes by remember { mutableStateOf(item.notes ?: "") }
    var isPriority by remember { mutableStateOf(item.isPriority) }

    val commonUnits = listOf("pcs", "kg", "g", "L", "ml", "pack", "loaf", "bunch", "bottle", "box", "can", "dozen")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = OikosSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Grocery Item",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OikosTextPrimary
                    )
                )
                IconButton(onClick = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                    }
                }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = OikosTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Item Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OikosForestGreen,
                    unfocusedBorderColor = OikosSurfaceBorder
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity Stepper & Unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, OikosSurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 0.5) quantity -= if (quantity <= 1.0) 0.5 else 1.0 },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = OikosForestGreen)
                    }

                    Text(
                        text = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else String.format("%.1f", quantity),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    IconButton(
                        onClick = { quantity += if (quantity < 1.0) 0.5 else 1.0 },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = OikosForestGreen)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Custom Unit Input
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OikosForestGreen,
                        unfocusedBorderColor = OikosSurfaceBorder
                    )
                )
            }

            // Quick Unit Selector
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonUnits.forEach { u ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (unit.equals(u, ignoreCase = true)) OikosForestGreenContainer else OikosSurfaceBorder.copy(alpha = 0.4f))
                            .clickable { unit = u }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = u,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (unit.equals(u, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal,
                                color = if (unit.equals(u, ignoreCase = true)) OikosForestGreen else OikosTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Picker
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = OikosTextPrimary)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GroceryCategory.entries.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) cat.getAdaptiveTagBgColor() else OikosSurfaceBorder.copy(alpha = 0.35f))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) cat.getAdaptiveTagTextColor() else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) cat.getAdaptiveTagTextColor() else OikosTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price & Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price ($currencySymbol)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OikosForestGreen,
                        unfocusedBorderColor = OikosSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isPriority,
                        onCheckedChange = { isPriority = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OikosForestGreen,
                            checkedTrackColor = OikosForestGreenContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (brand, size, store aisle...)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OikosForestGreen,
                    unfocusedBorderColor = OikosSurfaceBorder
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDeleteItem(item.id)
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OikosError)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete")
                }

                Button(
                    onClick = {
                        val parsedPrice = priceText.toDoubleOrNull()
                        onSaveItem(
                            item.copy(
                                name = name.trim().ifBlank { item.name },
                                quantity = quantity,
                                unit = unit.trim().ifBlank { "pcs" },
                                category = selectedCategory,
                                estimatedPrice = parsedPrice,
                                notes = notes.trim().ifBlank { null },
                                isPriority = isPriority
                            )
                        )
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
                    Text("Save Changes", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
