package com.nursingcare.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nursingcare.app.data.model.*

@Database(
    entities = [
        Patient::class,
        Medication::class,
        HealthRecord::class,
        NursingLog::class,
        Schedule::class,
        MedicationLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NursingCareDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun medicationDao(): MedicationDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun nursingLogDao(): NursingLogDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun medicationLogDao(): MedicationLogDao

    companion object {
        @Volatile
        private var INSTANCE: NursingCareDatabase? = null

        fun getDatabase(context: Context): NursingCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NursingCareDatabase::class.java,
                    "nursing_care_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}