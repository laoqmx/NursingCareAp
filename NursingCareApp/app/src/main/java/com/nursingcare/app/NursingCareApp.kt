package com.nursingcare.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.nursingcare.app.data.database.NursingCareDatabase
import com.nursingcare.app.data.repository.NursingRepository

class NursingCareApp : Application() {
    val database by lazy { NursingCareDatabase.getDatabase(this) }
    val repository by lazy {
        NursingRepository(
            database.patientDao(),
            database.medicationDao(),
            database.healthRecordDao(),
            database.nursingLogDao(),
            database.scheduleDao(),
            database.medicationLogDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                MEDICATION_CHANNEL_ID,
                "用药提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "提醒服药时间"
                enableVibration(true)
            }

            val channel2 = NotificationChannel(
                SCHEDULE_CHANNEL_ID,
                "日程提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "提醒日程安排"
            }

            val channel3 = NotificationChannel(
                EMERGENCY_CHANNEL_ID,
                "紧急通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "紧急呼叫通知"
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)
            notificationManager.createNotificationChannel(channel3)
        }
    }

    companion object {
        const val MEDICATION_CHANNEL_ID = "medication_reminder"
        const val SCHEDULE_CHANNEL_ID = "schedule_reminder"
        const val EMERGENCY_CHANNEL_ID = "emergency_alert"
    }
}