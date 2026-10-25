package com.nursingcare.app.data.database

import androidx.room.*
import com.nursingcare.app.data.model.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE patientId = :patientId ORDER BY dateTime ASC")
    fun getSchedulesByPatient(patientId: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE patientId = :patientId AND dateTime >= :currentTime ORDER BY dateTime ASC")
    fun getUpcomingSchedules(patientId: Long, currentTime: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE dateTime BETWEEN :startTime AND :endTime ORDER BY dateTime ASC")
    fun getSchedulesByDateRange(startTime: Long, endTime: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): Schedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)

    @Query("UPDATE schedules SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateScheduleStatus(id: Long, isCompleted: Boolean)
}