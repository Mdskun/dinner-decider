package com.mdskun.dinnerdecider

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mdskun.dinnerdecider.ui.DinnerApp
import com.mdskun.dinnerdecider.ui.theme.DinnerDeciderTheme
import com.mdskun.dinnerdecider.viewmodel.DinnerViewModel

class MainActivity : ComponentActivity() {

    private var activeViewModel: DinnerViewModel? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            activeViewModel?.toggleNotification(false)
            Toast.makeText(
                this,
                getString(R.string.notification_permission_required),
                Toast.LENGTH_LONG
            ).show()
        } else {
            activeViewModel?.toggleNotification(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display cleanly
        enableEdgeToEdge()

        setContent {
            val darkTheme = isSystemInDarkTheme()

            SideEffect {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !darkTheme
            }

            DinnerDeciderTheme(darkTheme = darkTheme) {
                val viewModel: DinnerViewModel = viewModel()
                activeViewModel = viewModel

                DinnerApp(
                    viewModel = viewModel,
                    onRequestNotificationPermission = { requestNotificationPermission() },
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding() // Padding for both status bar and navigation bar
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}