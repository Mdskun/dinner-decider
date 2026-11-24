package com.mdskun.dinnerdecider.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.mdskun.dinnerdecider.MainActivity
import com.mdskun.dinnerdecider.R
import org.json.JSONArray
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "dinner_suggestion"
        const val NOTIFICATION_ID = 1
        const val PREFS_NAME = "dinner_settings"

        const val KEY_DINNERS_JSON = "dinners_json"
        const val KEY_DINNERS_LEGACY = "dinners"
        const val KEY_NOTIF_TIME = "notif_time"
        const val KEY_NOTIF_ENABLED = "notif_enabled"
        const val KEY_AVOID_RECENT = "avoid_recent"
        const val KEY_RECENT_PICKS_JSON = "recent_picks_json"

        const val ACTION_SPIN_AGAIN = "com.mdskun.dinnerdecider.ACTION_SPIN_AGAIN"
        const val WORK_TAG = "dinner_daily_reminder_work"
        const val WORK_NAME = "dinner_daily_reminder"

        val DEFAULT_DINNERS = listOf(
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

        fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        fun loadDinners(context: Context): List<String> {
            val prefs = getPrefs(context)
            val jsonStr = prefs.getString(KEY_DINNERS_JSON, null)
            if (jsonStr != null) {
                return try {
                    val jsonArray = JSONArray(jsonStr)
                    val list = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        list.add(jsonArray.getString(i))
                    }
                    list
                } catch (e: Exception) {
                    DEFAULT_DINNERS
                }
            }

            // Legacy migration from StringSet
            val legacySet = prefs.getStringSet(KEY_DINNERS_LEGACY, null)
            if (legacySet != null && legacySet.isNotEmpty()) {
                val list = legacySet.toList()
                saveDinners(context, list)
                prefs.edit().remove(KEY_DINNERS_LEGACY).apply()
                return list
            }

            return DEFAULT_DINNERS
        }

        fun saveDinners(context: Context, dinners: List<String>) {
            val prefs = getPrefs(context)
            val jsonArray = JSONArray(dinners)
            prefs.edit().putString(KEY_DINNERS_JSON, jsonArray.toString()).apply()
        }

        fun loadRecentPicks(context: Context): List<String> {
            val prefs = getPrefs(context)
            val jsonStr = prefs.getString(KEY_RECENT_PICKS_JSON, null) ?: return emptyList()
            return try {
                val jsonArray = JSONArray(jsonStr)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    list.add(jsonArray.getString(i))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun saveRecentPicks(context: Context, recent: List<String>) {
            val prefs = getPrefs(context)
            val jsonArray = JSONArray(recent.takeLast(3))
            prefs.edit().putString(KEY_RECENT_PICKS_JSON, jsonArray.toString()).apply()
        }

        fun pickNextDinner(context: Context): String {
            val dinners = loadDinners(context)
            if (dinners.isEmpty()) {
                return "Time to add some dinners to your list!"
            }

            val prefs = getPrefs(context)
            val avoidRecent = prefs.getBoolean(KEY_AVOID_RECENT, true)
            val recent = loadRecentPicks(context)

            val candidatePool = if (avoidRecent && dinners.size > recent.size) {
                dinners.filter { it !in recent }
            } else {
                dinners
            }

            val pick = (candidatePool.ifEmpty { dinners }).random()
            saveRecentPicks(context, recent + pick)
            return pick
        }

        fun scheduleNotification(context: Context, time: String, enabled: Boolean) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME)

            if (!enabled) return

            val parts = time.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 17
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(now)) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val initialDelayMs = target.timeInMillis - now.timeInMillis

            val workRequest = OneTimeWorkRequestBuilder<DinnerNotificationWorker>()
                .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG)
                .build()

            workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Dinner Suggestions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily dinner suggestion notifications"
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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

        val actionIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SPIN_AGAIN
        }
        val actionPendingIntent = PendingIntent.getBroadcast(
            context, 1, actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text, dinner))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.notification_big_text, dinner))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_notification,
                context.getString(R.string.notification_action_spin),
                actionPendingIntent
            )
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
}

class DinnerNotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val prefs = NotificationHelper.getPrefs(appContext)
        val enabled = prefs.getBoolean(NotificationHelper.KEY_NOTIF_ENABLED, true)
        val time = prefs.getString(NotificationHelper.KEY_NOTIF_TIME, "17:00") ?: "17:00"

        if (enabled) {
            val dinner = NotificationHelper.pickNextDinner(appContext)
            val helper = NotificationHelper(appContext)
            helper.showNotification(dinner)

            // Reschedule for next day
            NotificationHelper.scheduleNotification(appContext, time, true)
        }

        return Result.success()
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dinner = NotificationHelper.pickNextDinner(context)
        NotificationHelper(context).showNotification(dinner)
    }
}

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NotificationHelper.ACTION_SPIN_AGAIN) {
            val dinner = NotificationHelper.pickNextDinner(context)
            NotificationHelper(context).showNotification(dinner)
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = NotificationHelper.getPrefs(context)
            val time = prefs.getString(NotificationHelper.KEY_NOTIF_TIME, "17:00") ?: "17:00"
            val enabled = prefs.getBoolean(NotificationHelper.KEY_NOTIF_ENABLED, true)

            if (enabled) {
                NotificationHelper.scheduleNotification(context, time, true)
            }
        }
    }
}