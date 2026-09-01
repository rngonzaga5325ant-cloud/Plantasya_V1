package com.example.plantasya_mobileapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object TaskScheduler {

    fun scheduleTask(context: Context, taskId: Int, taskName: String, plantName: String, frequency: String?) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.plantasya_mobileapp.ACTION_TASK_REMINDER"
            putExtra("TASK_ID", taskId)
            putExtra("TASK_NAME", taskName)
            putExtra("PLANT_NAME", plantName)
            putExtra("FREQUENCY", frequency)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextTime = calculateNextTime(frequency)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTime,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTime,
                pendingIntent
            )
        }
    }

    fun scheduleNextReminder(context: Context, taskId: Int, taskName: String, plantName: String, frequency: String?) {
        if (frequency != null) {
            scheduleTask(context, taskId, taskName, plantName, frequency)
        }
    }

    private fun calculateNextTime(frequency: String?): Long {
        val calendar = Calendar.getInstance()
        
        when (frequency?.lowercase()) {
            "daily" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            "weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "monthly" -> calendar.add(Calendar.MONTH, 1)
            "every 2 weeks" -> calendar.add(Calendar.DAY_OF_YEAR, 14)
            "every 3 days" -> calendar.add(Calendar.DAY_OF_YEAR, 3)
            "every 5-7 days" -> calendar.add(Calendar.DAY_OF_YEAR, 6)
            "every 7-10 days" -> calendar.add(Calendar.DAY_OF_YEAR, 8)
            "every 7-14 days" -> calendar.add(Calendar.DAY_OF_YEAR, 10)
            "every 21-30 days" -> calendar.add(Calendar.DAY_OF_YEAR, 25)
            "every 2-4 weeks" -> calendar.add(Calendar.WEEK_OF_YEAR, 3)
            "every 4-6 weeks" -> calendar.add(Calendar.WEEK_OF_YEAR, 5)
            "hourly" -> calendar.add(Calendar.HOUR_OF_DAY, 1)
            "twice weekly" -> calendar.add(Calendar.DAY_OF_YEAR, 3)
            "3 times per year" -> calendar.add(Calendar.MONTH, 4)
            else -> {
                // If frequency is complex, default to next day for demo
                if (frequency?.contains("monthly", ignoreCase = true) == true) {
                    calendar.add(Calendar.MONTH, 1)
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }
        
        // For testing/demo purposes, if it's "hourly", just add a minute
        if (frequency == "hourly") {
            // calendar.add(Calendar.MINUTE, 1) // Uncomment for quick testing
        }

        return calendar.timeInMillis
    }

    fun cancelTask(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.plantasya_mobileapp.ACTION_TASK_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}
