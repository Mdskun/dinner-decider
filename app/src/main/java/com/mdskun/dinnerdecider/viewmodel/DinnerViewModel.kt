package com.mdskun.dinnerdecider.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mdskun.dinnerdecider.notification.NotificationHelper

class DinnerViewModel(application: Application) : AndroidViewModel(application) {
    private val notificationHelper = NotificationHelper(application)
    private val prefs = application.getSharedPreferences("dinner_settings", Context.MODE_PRIVATE)

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

    // Load saved data or use defaults
    var dinners by mutableStateOf(loadDinners())
        private set

    var notifTime by mutableStateOf(loadNotifTime())
        private set

    var notifEnabled by mutableStateOf(loadNotifEnabled())
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
        // Schedule notification with saved settings
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
            saveAllData()
        }
    }

    fun updateDinner(index: Int, newName: String) {
        if (newName.isNotBlank() && index in dinners.indices) {
            dinners = dinners.toMutableList().apply { this[index] = newName.trim() }
            saveAllData()
        }
    }

    fun deleteDinner(index: Int) {
        if (index in dinners.indices) {
            dinners = dinners.toMutableList().apply { removeAt(index) }
            saveAllData()
        }
    }

    fun toggleNotification(enabled: Boolean) {
        notifEnabled = enabled
        saveAllData()
        scheduleNotification()
    }

    fun updateNotifTime(time: String) {
        notifTime = time
        saveAllData()
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
        // Save dinners to notification helper for random selection
        notificationHelper.saveDinners(dinners)
    }

    private fun saveAllData() {
        prefs.edit().apply {
            putString("notif_time", notifTime)
            putBoolean("notif_enabled", notifEnabled)
            // Save dinners as a set
            putStringSet("dinners", dinners.toSet())
            apply() // Use apply() for async saving
        }

        // Also update notification schedule
        scheduleNotification()
    }

    private fun loadDinners(): MutableList<String> {
        val savedDinners = prefs.getStringSet("dinners", null)
        return if (savedDinners != null && savedDinners.isNotEmpty()) {
            savedDinners.toMutableList()
        } else {
            defaultList.toMutableList()
        }
    }

    private fun loadNotifTime(): String {
        return prefs.getString("notif_time", "17:00") ?: "17:00"
    }

    private fun loadNotifEnabled(): Boolean {
        return prefs.getBoolean("notif_enabled", true)
    }
}