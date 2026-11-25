package com.mdskun.dinnerdecider.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.mdskun.dinnerdecider.R
import com.mdskun.dinnerdecider.notification.NotificationHelper

class DinnerViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = NotificationHelper.getPrefs(application)

    var dinners by mutableStateOf(NotificationHelper.loadDinners(application))
        private set

    var notifTime by mutableStateOf(prefs.getString(NotificationHelper.KEY_NOTIF_TIME, "17:00") ?: "17:00")
        private set

    var notifEnabled by mutableStateOf(prefs.getBoolean(NotificationHelper.KEY_NOTIF_ENABLED, true))
        private set

    var avoidRecentPicks by mutableStateOf(prefs.getBoolean(NotificationHelper.KEY_AVOID_RECENT, true))
        private set

    var selectedScreen by mutableStateOf("home")
        private set

    var spinning by mutableStateOf(false)
        private set

    var result by mutableStateOf<String?>(null)
        private set

    var showResult by mutableStateOf(false)
        private set

    var duplicateError by mutableStateOf<String?>(null)
        private set

    var lastDeletedItem by mutableStateOf<Pair<Int, String>?>(null)
        private set

    init {
        // Schedule notification once on startup if enabled
        if (notifEnabled) {
            NotificationHelper.scheduleNotification(getApplication(), notifTime, true)
        }
    }

    fun navigateTo(screen: String) {
        selectedScreen = screen
        duplicateError = null
    }

    fun goBack() {
        selectedScreen = "home"
        spinning = false
        result = null
        showResult = false
        duplicateError = null
    }

    fun clearDuplicateError() {
        duplicateError = null
    }

    fun addDinner(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return false

        if (dinners.any { it.equals(trimmed, ignoreCase = true) }) {
            duplicateError = getApplication<Application>().getString(R.string.duplicate_error, trimmed)
            return false
        }

        duplicateError = null
        dinners = dinners + trimmed
        saveDinnersData()
        return true
    }

    fun updateDinner(index: Int, newName: String): Boolean {
        val trimmed = newName.trim()
        if (trimmed.isBlank() || index !in dinners.indices) return false

        if (dinners.indices.any { i -> i != index && dinners[i].equals(trimmed, ignoreCase = true) }) {
            duplicateError = getApplication<Application>().getString(R.string.duplicate_error, trimmed)
            return false
        }

        duplicateError = null
        val mutable = dinners.toMutableList()
        mutable[index] = trimmed
        dinners = mutable
        saveDinnersData()
        return true
    }

    fun deleteDinner(index: Int) {
        if (index in dinners.indices) {
            val removedItem = dinners[index]
            lastDeletedItem = Pair(index, removedItem)
            val mutable = dinners.toMutableList()
            mutable.removeAt(index)
            dinners = mutable
            saveDinnersData()
        }
    }

    fun undoDelete() {
        val deleted = lastDeletedItem ?: return
        val (index, item) = deleted
        val mutable = dinners.toMutableList()
        val targetIndex = if (index in 0..mutable.size) index else mutable.size
        mutable.add(targetIndex, item)
        dinners = mutable
        lastDeletedItem = null
        saveDinnersData()
    }

    fun clearLastDeletedItem() {
        lastDeletedItem = null
    }

    fun resetToDefaultList() {
        dinners = NotificationHelper.DEFAULT_DINNERS
        saveDinnersData()
        NotificationHelper.saveRecentPicks(getApplication(), emptyList())
    }

    fun toggleAvoidRecentPicks(enabled: Boolean) {
        avoidRecentPicks = enabled
        prefs.edit().putBoolean(NotificationHelper.KEY_AVOID_RECENT, enabled).apply()
    }

    fun toggleNotification(enabled: Boolean) {
        notifEnabled = enabled
        prefs.edit().putBoolean(NotificationHelper.KEY_NOTIF_ENABLED, enabled).apply()
        NotificationHelper.scheduleNotification(getApplication(), notifTime, enabled)
    }

    fun updateNotifTime(time: String) {
        notifTime = time
        prefs.edit().putString(NotificationHelper.KEY_NOTIF_TIME, time).apply()
        if (notifEnabled) {
            NotificationHelper.scheduleNotification(getApplication(), time, true)
        }
    }

    fun pickDinnerForSpin(): String? {
        if (dinners.isEmpty()) return null
        return NotificationHelper.pickNextDinner(getApplication())
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

    private fun saveDinnersData() {
        NotificationHelper.saveDinners(getApplication(), dinners)
    }
}