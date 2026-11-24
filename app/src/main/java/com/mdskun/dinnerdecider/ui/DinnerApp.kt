package com.mdskun.dinnerdecider.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mdskun.dinnerdecider.ui.screens.HomeScreen
import com.mdskun.dinnerdecider.ui.screens.ListScreen
import com.mdskun.dinnerdecider.ui.screens.SettingsScreen
import com.mdskun.dinnerdecider.ui.screens.SpinScreen
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel

@Composable
fun DinnerApp(
    viewModel: DinnerViewModel,
    onRequestNotificationPermission: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Intercept back button when not on home screen
    BackHandler(enabled = viewModel.selectedScreen != "home") {
        viewModel.goBack()
    }

    when (viewModel.selectedScreen) {
        "home" -> HomeScreen(viewModel = viewModel, modifier = modifier)
        "list" -> ListScreen(viewModel = viewModel, modifier = modifier)
        "settings" -> SettingsScreen(
            viewModel = viewModel,
            onRequestNotificationPermission = onRequestNotificationPermission,
            modifier = modifier
        )
        "spin" -> SpinScreen(viewModel = viewModel, modifier = modifier)
    }
}