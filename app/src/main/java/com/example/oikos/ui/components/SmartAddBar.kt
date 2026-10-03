package com.example.oikos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.FrequentItem
import com.example.oikos.model.GroceryCategory
import com.example.oikos.model.GroceryItem
import com.example.oikos.nlp.SmartAddParser
import com.example.oikos.theme.*
import com.example.oikos.util.CurrencyFormatter

@Composable
fun SmartAddBar(
    existingItems: List<GroceryItem>,
    frequentItems: List<FrequentItem>,
    currencySymbol: String,
    onAddItem: (GroceryItem) -> Unit,
    onIncrementExistingItem: (GroceryItem) -> Unit,
    onHapticTick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val parsedResult by remember(textInput) {
        derivedStateOf {
            if (textInput.isNotBlank()) SmartAddParser.parse(textInput) else null
        }
    }

    // Check duplicate item
    val duplicateItem by remember(parsedResult, existingItems) {
        derivedStateOf {
            parsedResult?.let { parsed ->
                existingItems.firstOrNull { 
                    !it.isCompleted && it.name.equals(parsed.name, ignoreCase = true) 
                }
            }
        }
    }

    fun submitItem() {
        val parsed = parsedResult ?: return
        if (parsed.name.isBlank()) return
        onHapticTick()
        onAddItem(
            GroceryItem(
                name = parsed.name,
                quantity = parsed.quantity,
                unit = parsed.unit,
                category = parsed.category,
                estimatedPrice = parsed.estimatedPrice
            )
        )
        textInput = ""
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Main Input Field Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = OikosSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Input TextField
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Add item (e.g. 2L milk, 6 eggs, bread @ 80)...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = OikosTextTertiary)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submitItem() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        trailingIcon = {
                            if (textInput.isNotBlank()) {
                                IconButton(onClick = { textInput = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = OikosTextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Add Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (textInput.isNotBlank()) OikosForestGreen else OikosSurfaceBorder)
                            .clickable(enabled = textInput.isNotBlank()) { submitItem() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Item",
                            tint = if (textInput.isNotBlank()) OikosOnForestGreen else OikosTextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Live Parsed Preview Chips
                AnimatedVisibility(
                    visible = parsedResult != null && parsedResult?.name?.isNotBlank() == true,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    parsedResult?.let { parsed ->
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Smart Parser Indicator
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    tint = OikosWarmHoney,
                                    modifier = Modifier.size(14.dp)
                                )

                                // Quantity & Unit Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OikosForestGreenContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${if (parsed.quantity % 1.0 == 0.0) parsed.quantity.toInt() else parsed.quantity} ${parsed.unit}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = OikosForestGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                // Category Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(parsed.category.tagBgColor)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = parsed.category.displayName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = parsed.category.tagTextColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                // Price Pill if present
                                if (parsed.estimatedPrice != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(OikosWarmHoneyContainer)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = CurrencyFormatter.format(parsed.estimatedPrice, currencySymbol),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = OikosWarmHoney,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            // Duplicate notice if item already present
                            if (duplicateItem != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OikosWarmHoneyLight)
                                        .clickable {
                                            duplicateItem?.let { onIncrementExistingItem(it) }
                                            textInput = ""
                                            onHapticTick()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = OikosWarmHoney,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Already on list (${duplicateItem!!.formattedQuantity}). Tap to increase quantity!",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OikosWarmHoney,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestions Horizontal Chip Carousel
        if (textInput.isBlank() && frequentItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick add:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OikosTextTertiary,
                        fontWeight = FontWeight.Medium
                    )
                )

                frequentItems.take(7).forEach { freq ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onHapticTick()
                                onAddItem(
                                    GroceryItem(
                                        name = freq.name,
                                        quantity = freq.defaultQuantity,
                                        unit = freq.defaultUnit,
                                        category = freq.category
                                    )
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = OikosSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, OikosSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+ ${freq.name}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = OikosTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
