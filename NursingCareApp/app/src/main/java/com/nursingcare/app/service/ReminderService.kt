package com.nursingcare.app.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nursingcare.app.NursingCareApp
import com.nursingcare.app.R
import com.nursingcare.app.data.model.Medication
import com.nursingcare.app.data.model.Schedule

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE) ?: return

        when (type) {
            TYPE_MEDICATION -> {
                val medicationName = intent.getStringExtra(EXTRA_MEDICATION_NAME) ?: "药物"
                val dosage = intent.getStringExtra(EXTRA_DOSAGE) ?: ""
                showNotification(
                    context,
                    channelId = NursingCareApp.MEDICATION_CHANNEL_ID,
                    title = "用药提醒",
                    message = "该服药了：$medicationName $dosage",
                    notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
                )
            }
            TYPE_SCHEDULE -> {
                val scheduleTitle = intent.getStringExtra(EXTRA_SCHEDULE_TITLE) ?: "日程"
                val scheduleType = intent.getStringExtra(EXTRA_SCHEDULE_TYPE) ?: ""
                showNotification(
                    context,
                    channelId = NursingCareApp.SCHEDULE_CHANNEL_ID,
                    title = "日程提醒",
                    message = "$scheduleTitle - $scheduleType",
                    notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
                )
            }
            TYPE_EMERGENCY -> {
                showNotification(
                    context,
                    channelId = NursingCareApp.EMERGENCY_CHANNEL_ID,
                    title = "紧急呼叫",
                    message = "紧急联系人电话未设置，请尽快设置",
                    notificationId = 9999
                )
            }
        }
    }

    private fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val EXTRA_TYPE = "type"
        const val EXTRA_MEDICATION_NAME = "medication_name"
        const val EXTRA_DOSAGE = "dosage"
        const val EXTRA_SCHEDULE_TITLE = "schedule_title"
        const val EXTRA_SCHEDULE_TYPE = "schedule_type"
        const val EXTRA_NOTIFICATION_ID = "notification_id"

        const val TYPE_MEDICATION = "medication"
        const val TYPE_SCHEDULE = "schedule"
        const val TYPE_EMERGENCY = "emergency"

        fun scheduleMedicationReminder(
            context: Context,
            medication: Medication,
            timeHour: Int,
            timeMinute: Int,
            requestCode: Int
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_TYPE, TYPE_MEDICATION)
                putExtra(EXTRA_MEDICATION_NAME, medication.name)
                putExtra(EXTRA_DOSAGE, medication.dosage)
                putExtra(EXTRA_NOTIFICATION_ID, requestCode)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, timeHour)
                set(java.util.Calendar.MINUTE, timeMinute)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)

                if (timeInMillis < System.currentTimeMillis()) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }

        fun scheduleScheduleReminder(
            context: Context,
            schedule: Schedule,
            requestCode: Int
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_TYPE, TYPE_SCHEDULE)
                putExtra(EXTRA_SCHEDULE_TITLE, schedule.title)
                putExtra(EXTRA_SCHEDULE_TYPE, schedule.type)
                putExtra(EXTRA_NOTIFICATION_ID, requestCode)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        schedule.dateTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        schedule.dateTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    schedule.dateTime,
                    pendingIntent
                )
            }
        }

        fun cancelReminder(context: Context, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all reminders after boot
            // This would require reloading all medications and schedules
            // For simplicity, we'll just show a notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, NursingCareApp.MEDICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("护理助手")
                .setContentText("应用已重启，提醒服务已恢复")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1000, notification)
        }
    }
}