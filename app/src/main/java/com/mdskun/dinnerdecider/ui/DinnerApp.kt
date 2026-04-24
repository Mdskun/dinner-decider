package com.mdskun.dinnerdecider.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel
import com.mdskun.dinnerdecider.ui.screens.HomeScreen
import com.mdskun.dinnerdecider.ui.screens.ListScreen
import com.mdskun.dinnerdecider.ui.screens.SettingsScreen
import com.mdskun.dinnerdecider.ui.screens.SpinScreen

@Composable
fun DinnerApp(
    viewModel: DinnerViewModel,
    modifier: Modifier = Modifier
) {
    when (viewModel.selectedScreen) {
        "home" -> HomeScreen(viewModel = viewModel, modifier = modifier)
        "list" -> ListScreen(viewModel = viewModel, modifier = modifier)
        "settings" -> SettingsScreen(viewModel = viewModel, modifier = modifier)
        "spin" -> SpinScreen(viewModel = viewModel, modifier = modifier)
    }
}