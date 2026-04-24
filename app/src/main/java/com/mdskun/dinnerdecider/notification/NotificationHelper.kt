package com.mdskun.dinnerdecider.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mdskun.dinnerdecider.MainActivity
import com.mdskun.dinnerdecider.R
import java.util.Calendar

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "dinner_suggestion"
        const val NOTIFICATION_ID = 1
        const val PREFS_NAME = "dinner_prefs"
        const val KEY_DINNERS = "dinners"
        const val KEY_NOTIF_TIME = "notif_time"
        const val KEY_NOTIF_ENABLED = "notif_enabled"
    }

    init { createNotificationChannel() }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Dinner Suggestions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Daily dinner suggestion notifications" }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(time: String, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (enabled) {
            try {
                val parts = time.split(":")
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
                }
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun showNotification(dinner: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🍽️ Dinner Decider")
            .setContentText("Tonight's dinner suggestion: $dinner")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tonight's dinner suggestion: $dinner\n\nTap to see more options!")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun saveDinners(dinners: List<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putStringSet(KEY_DINNERS, dinners.toSet()).apply()
    }

    fun getSavedDinners(): List<String>? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(KEY_DINNERS, null)?.toList()
    }

    fun saveNotifTime(time: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_NOTIF_TIME, time).apply()
    }

    fun getSavedNotifTime(): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_NOTIF_TIME, null)
    }

    fun saveNotifEnabled(enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()
    }

    fun getSavedNotifEnabled(): Boolean? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (prefs.contains(KEY_NOTIF_ENABLED)) prefs.getBoolean(KEY_NOTIF_ENABLED, true) else null
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val helper = NotificationHelper(context)
        val dinners = helper.getSavedDinners()
        if (!dinners.isNullOrEmpty()) {
            helper.showNotification(dinners.random())
        } else {
            helper.showNotification("Try something new tonight! 🍽️")
        }
    }
}