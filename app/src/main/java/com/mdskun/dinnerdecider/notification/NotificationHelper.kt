package com.mdskun.dinnerdecider.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mdskun.dinnerdecider.MainActivity
import java.util.Calendar

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "dinner_suggestion"
        const val NOTIFICATION_ID = 1
        const val PREFS_NAME = "dinner_settings"
        const val KEY_DINNERS = "dinners"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Dinner Suggestions",
                NotificationManager.IMPORTANCE_HIGH // Changed to HIGH for better visibility
            ).apply {
                description = "Daily dinner suggestion notifications"
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(time: String, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel any existing alarms first
        alarmManager.cancel(pendingIntent)

        if (enabled) {
            try {
                val parts = time.split(":")
                if (parts.size != 2) return

                val hour = parts[0].toInt()
                val minute = parts[1].toInt()

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    // If time already passed today, schedule for tomorrow
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                // Schedule the alarm
                try {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    // Handle exact alarm permission for Android 12+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                        )
                    }
                }

                // Also schedule a boot receiver to reschedule after reboot
                scheduleBootReceiver()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun scheduleBootReceiver() {
        // Save the notification settings for boot receiver
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Settings are already saved, just need to ensure boot receiver is enabled
        val bootReceiver = android.content.ComponentName(context, BootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            bootReceiver,
            android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            android.content.pm.PackageManager.DONT_KILL_APP
        )
    }

    fun showNotification(dinner: String) {
        // Create intent to open app when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // System icon
            .setContentTitle("🍽️ Dinner Decider")
            .setContentText("Tonight's dinner suggestion: $dinner")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tonight's dinner suggestion:\n\n$dinner\n\nTap to open the app and see more options!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(100, 200, 300))
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun saveDinners(dinners: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_DINNERS, dinners.toSet()).apply()
    }

    fun getDinners(): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_DINNERS, emptySet())?.toList() ?: emptyList()
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val helper = NotificationHelper(context)
        val dinners = helper.getDinners()

        if (dinners.isNotEmpty()) {
            val randomDinner = dinners.random()
            helper.showNotification(randomDinner)
        } else {
            // If no dinners saved, show default message
            helper.showNotification("Time to add some dinners to your list!")
        }
    }
}

// Add BootReceiver to reschedule alarms after device restart
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("dinner_settings", Context.MODE_PRIVATE)
            val time = prefs.getString("notif_time", "17:00") ?: "17:00"
            val enabled = prefs.getBoolean("notif_enabled", true)

            if (enabled) {
                NotificationHelper(context).scheduleNotification(time, enabled)
            }
        }
    }
}