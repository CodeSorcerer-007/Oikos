package com.example.oikos.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oikos.theme.OikosForestGreen
import com.example.oikos.theme.OikosForestGreenContainer
import com.example.oikos.theme.OikosOnForestGreen
import com.example.oikos.theme.OikosSurface
import com.example.oikos.theme.OikosSurfaceBorder
import com.example.oikos.theme.OikosTextPrimary
import com.example.oikos.theme.OikosTextSecondary
import com.example.oikos.theme.OikosTextTertiary
import kotlinx.coroutines.launch

import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    currencySymbol: String,
    themeMode: String,
    hapticsEnabled: Boolean,
    keepScreenAwake: Boolean,
    onSetCurrency: (String) -> Unit,
    onSetThemeMode: (String) -> Unit,
    onSetHaptics: (Boolean) -> Unit,
    onSetKeepScreenAwake: (Boolean) -> Unit,
    onExportBackup: suspend () -> String,
    onImportBackup: suspend (String) -> Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var showExportedDialog by remember { mutableStateOf<String?>(null) }

    val currencies = listOf("₹", "$", "€", "£", "¥", "C$", "A$")

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
                    text = "Preferences & Backup",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OikosTextPrimary
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = OikosTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Appearance & Theme Mode Selector
            Text(
                text = "Appearance & Theme",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = OikosTextPrimary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val modes = listOf(
                    Triple("SYSTEM", "System", Icons.Outlined.BrightnessAuto),
                    Triple("LIGHT", "Light", Icons.Outlined.LightMode),
                    Triple("DARK", "Dark", Icons.Outlined.DarkMode)
                )
                modes.forEach { (modeKey, label, icon) ->
                    val isSelected = themeMode.equals(modeKey, ignoreCase = true)
                    Surface(
                        onClick = { onSetThemeMode(modeKey) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) OikosForestGreenContainer else OikosSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) OikosForestGreen else OikosSurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) OikosForestGreen else OikosTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) OikosForestGreen else OikosTextPrimary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Currency Selector
            Text(
                text = "Currency Symbol",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = OikosTextPrimary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currencies.forEach { curr ->
                    val isSelected = curr == currencySymbol
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) OikosForestGreenContainer else OikosSurfaceBorder.copy(alpha = 0.35f))
                            .border(
                                1.dp,
                                if (isSelected) OikosForestGreen else OikosSurfaceBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSetCurrency(curr) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = curr,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) OikosForestGreen else OikosTextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Haptic Feedback Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Vibration,
                        contentDescription = null,
                        tint = OikosForestGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tactile Haptics",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Vibrations for add, check, and delete actions",
                            style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary)
                        )
                    }
                }

                Switch(
                    checked = hapticsEnabled,
                    onCheckedChange = { onSetHaptics(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OikosForestGreen,
                        checkedTrackColor = OikosForestGreenContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Keep Screen Awake Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = OikosForestGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Keep Screen On",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Prevent sleep while in active Shopping Mode",
                            style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary)
                        )
                    }
                }

                Switch(
                    checked = keepScreenAwake,
                    onCheckedChange = { onSetKeepScreenAwake(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OikosForestGreen,
                        checkedTrackColor = OikosForestGreenContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Backup & Data Safety
            Text(
                text = "Data Safety & Backups (Offline-First)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = OikosTextPrimary)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Export Button
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            val json = onExportBackup()
                            showExportedDialog = json
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Oikos Backup", json))
                            Toast.makeText(context, "Backup copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Data")
                }

                // Import Button
                OutlinedButton(
                    onClick = { showImportDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import Data")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About Oikos Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = OikosForestGreenContainer.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OIKOS Native Grocery",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OikosForestGreen
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100% offline-first, private on-device architecture. Zero trackers, zero cloud dependencies. Your grocery data is yours forever.",
                        style = MaterialTheme.typography.bodySmall.copy(color = OikosForestGreen)
                    )
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Restore Oikos Backup") },
            text = {
                Column {
                    Text("Paste your exported JSON backup text below:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        placeholder = { Text("{\"version\": 1, \"lists\": [...]}") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = onImportBackup(importJsonText)
                            showImportDialog = false
                            importJsonText = ""
                            Toast.makeText(
                                context,
                                if (success) "Backup restored successfully!" else "Failed to parse backup JSON",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OikosForestGreen)
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Exported Dialog Preview
    showExportedDialog?.let { json ->
        AlertDialog(
            onDismissRequest = { showExportedDialog = null },
            title = { Text("Backup Generated") },
            text = {
                Column {
                    Text("Your backup JSON has been copied to your clipboard. You can paste it into any notes app or save it to a file.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = json.take(200) + if (json.length > 200) "\n..." else "",
                        style = MaterialTheme.typography.bodySmall.copy(color = OikosTextSecondary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportedDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = OikosForestGreen)
                ) {
                    Text("Done")
                }
            }
        )
    }
}
