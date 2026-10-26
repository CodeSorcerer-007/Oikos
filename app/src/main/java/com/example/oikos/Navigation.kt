package com.example.oikos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.oikos.ui.main.MainScreen
import com.example.oikos.ui.main.MainScreenViewModel

@Composable
fun MainNavigation(viewModel: MainScreenViewModel = viewModel()) {
    val backStack = rememberNavBackStack(Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Main> {
                MainScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            }
        }
    )
}
