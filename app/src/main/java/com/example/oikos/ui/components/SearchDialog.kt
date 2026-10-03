package com.example.oikos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryItem
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog(
    items: List<GroceryItem>,
    currencySymbol: String,
    onToggleCompletion: (String) -> Unit,
    onEditItem: (GroceryItem) -> Unit,
    onDeleteItem: (String) -> Unit,
    onHapticConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems by remember(searchQuery, items) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                val query = searchQuery.trim().lowercase()
                items.filter { item ->
                    item.name.lowercase().contains(query) ||
                    item.category.displayName.lowercase().contains(query) ||
                    (item.notes?.lowercase()?.contains(query) == true)
                }
            }
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
            // Search Input Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search your groceries...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OikosTextSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = OikosTextTertiary)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OikosForestGreen,
                        unfocusedBorderColor = OikosSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                    }
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Close", tint = OikosTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Results count
            if (searchQuery.isNotBlank()) {
                Text(
                    text = "${filteredItems.size} match${if (filteredItems.size != 1) "es" else ""} found",
                    style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Results list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                if (searchQuery.isBlank()) {
                    item {
                        Text(
                            text = "Type to search by grocery name, category, or notes",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OikosTextTertiary),
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else if (filteredItems.isEmpty()) {
                    item {
                        Text(
                            text = "No matching groceries found",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OikosTextTertiary),
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    items(filteredItems, key = { it.id }) { item ->
                        GroceryItemRow(
                            item = item,
                            currencySymbol = currencySymbol,
                            onToggleCompletion = onToggleCompletion,
                            onEditItem = onEditItem,
                            onDeleteItem = onDeleteItem,
                            onHapticConfirm = onHapticConfirm
                        )
                    }
                }
            }
        }
    }
}
