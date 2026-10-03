package com.example.oikos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LinearScale
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.model.GroceryList
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosOnForestGreen
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary

@Composable
fun OikosTopBar(
    currentList: GroceryList?,
    lists: List<GroceryList>,
    onSelectList: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenShoppingMode: () -> Unit,
    onOpenListManager: () -> Unit,
    onOpenStoreOrder: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var listPickerExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title Area
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { listPickerExpanded = true }
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                // Mini Emblem
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(OikosForestGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentList?.emoji ?: "🌿",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentList?.name ?: "Oikos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OikosTextPrimary
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch List",
                            tint = OikosTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Lists dropdown selector
                    DropdownMenu(
                        expanded = listPickerExpanded,
                        onDismissRequest = { listPickerExpanded = false },
                        modifier = Modifier.background(OikosSurface)
                    ) {
                        lists.forEach { list ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = list.emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = list.name,
                                            fontWeight = if (list.id == currentList?.id) FontWeight.Bold else FontWeight.Normal,
                                            color = if (list.id == currentList?.id) OikosForestGreen else OikosTextPrimary
                                        )
                                    }
                                },
                                onClick = {
                                    listPickerExpanded = false
                                    onSelectList(list.id)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.ListAlt,
                                        contentDescription = null,
                                        tint = OikosForestGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Manage All Lists...", color = OikosForestGreen, fontWeight = FontWeight.Medium)
                                }
                            },
                            onClick = {
                                listPickerExpanded = false
                                onOpenListManager()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Shopping Mode Signature Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenShoppingMode() },
                color = OikosForestGreenContainer,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Mode",
                        tint = OikosForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Shop",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = OikosForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Search Icon
            IconButton(onClick = onOpenSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Groceries",
                    tint = OikosTextPrimary
                )
            }

            // More Options Menu
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = OikosTextPrimary
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(OikosSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Store Layout Order") },
                        leadingIcon = {
                            Icon(Icons.Outlined.LinearScale, contentDescription = null, tint = OikosTextSecondary)
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenStoreOrder()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Manage Lists") },
                        leadingIcon = {
                            Icon(Icons.Outlined.ListAlt, contentDescription = null, tint = OikosTextSecondary)
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenListManager()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings & Backup") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Settings, contentDescription = null, tint = OikosTextSecondary)
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenSettings()
                        }
                    )
                }
            }
        }
    }
}
