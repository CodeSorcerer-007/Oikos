package com.example.oikos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oikos.theme.OikosTheme
import com.example.oikos.tile.OikosShoppingTileService
import com.example.oikos.ui.intro.IntroScreen
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

            var showIntro by rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(openShoppingModePending, showIntro) {
                if (openShoppingModePending && !showIntro) {
                    viewModel.showModal(ActiveModal.ShoppingMode)
                    openShoppingModePending = false
                }
            }

            OikosTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = showIntro,
                        transitionSpec = {
                            if (!targetState) {
                                // Transition from Intro Screen to Main Navigation
                                (fadeIn(animationSpec = tween(480, easing = EaseInOutCubic)) +
                                        scaleIn(initialScale = 0.94f, animationSpec = tween(480, easing = EaseOutCubic)))
                                    .togetherWith(
                                        fadeOut(animationSpec = tween(420, easing = EaseInOutCubic)) +
                                                scaleOut(targetScale = 1.06f, animationSpec = tween(420, easing = EaseInCubic))
                                    )
                            } else {
                                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                            }
                        },
                        label = "AppIntroTransition"
                    ) { isIntro ->
                        if (isIntro) {
                            IntroScreen(
                                modifier = Modifier.fillMaxSize(),
                                onIntroFinished = {
                                    showIntro = false
                                }
                            )
                        } else {
                            MainNavigation(viewModel = viewModel)
                        }
                    }
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
