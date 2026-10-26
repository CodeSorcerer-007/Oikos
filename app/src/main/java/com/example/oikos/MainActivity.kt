package com.example.oikos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oikos.theme.OikosTheme
import com.example.oikos.tile.OikosShoppingTileService
import com.example.oikos.ui.main.ActiveModal
import com.example.oikos.ui.main.MainScreenViewModel

class MainActivity : ComponentActivity() {

    private var openShoppingModePending = false
    private var activeViewModel: MainScreenViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openShoppingModePending = intent?.getBooleanExtra(OikosShoppingTileService.EXTRA_OPEN_SHOPPING_MODE, false) ?: false

        setupDynamicShortcuts()
        enableEdgeToEdge()

        setContent {
            val viewModel: MainScreenViewModel = viewModel()
            activeViewModel = viewModel
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            LaunchedEffect(openShoppingModePending) {
                if (openShoppingModePending) {
                    viewModel.showModal(ActiveModal.ShoppingMode)
                    openShoppingModePending = false
                }
            }

            OikosTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainNavigation(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(OikosShoppingTileService.EXTRA_OPEN_SHOPPING_MODE, false)) {
            activeViewModel?.showModal(ActiveModal.ShoppingMode)
        }
    }

    private fun setupDynamicShortcuts() {
        val shoppingIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(OikosShoppingTileService.EXTRA_OPEN_SHOPPING_MODE, true)
        }

        val shortcut = ShortcutInfoCompat.Builder(this, "shortcut_shopping_mode")
            .setShortLabel("Shopping Mode")
            .setLongLabel("Start Shopping Trip")
            .setIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher))
            .setIntent(shoppingIntent)
            .build()

        ShortcutManagerCompat.addDynamicShortcuts(this, listOf(shortcut))
    }
}

