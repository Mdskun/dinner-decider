package com.mdskun.dinnerdecider.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mdskun.dinnerdecider.notification.NotificationHelper

class DinnerViewModel(application: Application) : AndroidViewModel(application) {
    private val notificationHelper = NotificationHelper(application)

    private val defaultList = listOf(
        "Dal Tadka & Roti",
        "Pasta Arrabiata",
        "Grilled Chicken & Rice",
        "Paneer Butter Masala",
        "Vegetable Fried Rice",
        "Rajma Chawal",
        "Aloo Paratha",
        "Biryani",
        "Soup & Bread",
        "Chole Bhature"
    )

    // State variables - these generate their own setters
    var dinners by mutableStateOf(defaultList.toMutableList())
        private set

    var notifTime by mutableStateOf("17:00")
        private set

    var notifEnabled by mutableStateOf(true)
        private set

    var selectedScreen by mutableStateOf("home")
        private set

    var spinning by mutableStateOf(false)
        private set

    var result by mutableStateOf<String?>(null)
        private set

    var showResult by mutableStateOf(false)
        private set

    init {
        scheduleNotification()
    }

    fun navigateTo(screen: String) {
        selectedScreen = screen
    }

    fun goBack() {
        selectedScreen = "home"
        result = null
        showResult = false
    }

    fun addDinner(name: String) {
        if (name.isNotBlank()) {
            dinners = dinners.toMutableList().apply { add(name.trim()) }
            updateSavedDinners()
        }
    }

    fun updateDinner(index: Int, newName: String) {
        if (newName.isNotBlank() && index in dinners.indices) {
            dinners = dinners.toMutableList().apply { this[index] = newName.trim() }
            updateSavedDinners()
        }
    }

    fun deleteDinner(index: Int) {
        if (index in dinners.indices) {
            dinners = dinners.toMutableList().apply { removeAt(index) }
            updateSavedDinners()
        }
    }

    // Renamed to avoid conflict with auto-generated setters
    fun toggleNotification(enabled: Boolean) {
        notifEnabled = enabled
        scheduleNotification()
    }

    fun updateNotifTime(time: String) {
        notifTime = time
        scheduleNotification()
    }

    fun startSpin() {
        spinning = true
    }

    fun stopSpin() {
        spinning = false
    }

    fun setSpinResult(dinner: String?) {
        result = dinner
    }

    fun displayResult(show: Boolean) {
        showResult = show
    }

    private fun scheduleNotification() {
        notificationHelper.scheduleNotification(notifTime, notifEnabled)
        updateSavedDinners()
    }

    private fun updateSavedDinners() {
        notificationHelper.saveDinners(dinners)
    }
}