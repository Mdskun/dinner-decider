package com.mdskun.dinnerdecider

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mdskun.dinnerdecider.ui.DinnerApp
import com.mdskun.dinnerdecider.ui.theme.DinnerDeciderTheme
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel
//import com.mdskun.dinnerdecider.ui.screens.DinnerApp

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Notification permission result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Make status bar transparent
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        // Use WindowInsetsControllerCompat for better compatibility
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        setContent {
            DinnerDeciderTheme {
                val viewModel: DinnerViewModel = viewModel()
                DinnerApp(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding() // Adds padding for status bar
                )
            }
        }
    }
}