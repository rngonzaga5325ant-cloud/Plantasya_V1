package com.example.plantasya_mobileapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.plantasya_mobileapp.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAllTasks(context)
            return
        }

        val taskName = intent.getStringExtra("TASK_NAME") ?: "Plant Care Task"
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "Your Plant"
        val taskId = intent.getIntExtra("TASK_ID", 0)

        showNotification(context, taskName, plantName, taskId)
        
        // Reschedule if needed (handled in TaskScheduler)
        TaskScheduler.scheduleNextReminder(context, taskId, taskName, plantName, intent.getStringExtra("FREQUENCY"))
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

    private fun showNotification(context: Context, taskName: String, plantName: String, taskId: Int) {
        val channelId = "plant_care_tasks"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Plant Care Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, taskId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_information_logo) // Use an existing icon
            .setContentTitle("Time to care for your $plantName!")
            .setContentText("Task: $taskName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(taskId, notification)
    }
}
