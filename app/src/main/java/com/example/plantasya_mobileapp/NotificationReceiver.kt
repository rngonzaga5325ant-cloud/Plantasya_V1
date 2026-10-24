package com.example.plantasya_mobileapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.plantasya_mobileapp.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val OVERDUE_THRESHOLD_MILLIS = 24 * 60 * 60 * 1000L // 24 hours
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAllTasks(context)
            return
        }

        val taskName = intent.getStringExtra("TASK_NAME") ?: "Plant Care Task"
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "Your Plant"
        val taskId = intent.getIntExtra("TASK_ID", 0)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val task = db.taskDao().getTaskById(taskId)
            val plantId = task?.plantId ?: -1
            val currentTime = System.currentTimeMillis()
            val isOverdue = task != null && !task.taskDone && task.nextReminderTime != null && currentTime >= (task.nextReminderTime + OVERDUE_THRESHOLD_MILLIS)

            showNotification(context, taskName, plantName, taskId, plantId, isOverdue)
            
            if (task != null && task.taskDone) {
                // Task was completed previously; now sched is up, so reset taskDone to false!
                db.taskDao().update(task.copy(taskDone = false))
            }
        }
    }

    private fun rescheduleAllTasks(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val tasks = db.taskDao().getTasksWithPlantName()
            tasks.forEach { item ->
                TaskScheduler.scheduleTask(
                    context,
                    item.task.idTask,
                    item.task.taskName ?: "Care Task",
                    item.plantName ?: "Your Plant",
                    item.task.taskFrequency
                )
            }
        }
    }

    private fun showNotification(
        context: Context,
        taskName: String,
        plantName: String,
        taskId: Int,
        plantId: Int,
        isOverdue: Boolean
    ) {
        val channelId = if (isOverdue) "plant_care_urgent_overdue" else "plant_care_tasks"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = if (isOverdue) "Urgent Overdue Reminders" else "Plant Care Reminders"
            val importance = if (isOverdue) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                if (isOverdue) {
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_DASHBOARD", true)
            putExtra("SHOW_OVERDUE_DIALOG", isOverdue)
            putExtra("HIGHLIGHT_TASK_ID", taskId)
            putExtra("PLANT_ID", plantId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = if (isOverdue) "🚨 OVERDUE TASK: $plantName" else "🌱 Time to care for your $plantName!"
        val content = if (isOverdue) "Overdue task: $taskName. Tap to resolve now!" else "Task: $taskName"
        val icon = if (isOverdue) R.drawable.ic_alarm_alert else R.drawable.ic_information_logo

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setColor(if (isOverdue) Color.RED else Color.parseColor("#398945"))
            .setPriority(if (isOverdue) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setCategory(if (isOverdue) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(taskId, notification)
    }
}
