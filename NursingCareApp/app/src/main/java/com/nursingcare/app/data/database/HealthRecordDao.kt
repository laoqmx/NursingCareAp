package com.nursingcare.app.data.database

import androidx.room.*
import com.nursingcare.app.data.model.HealthRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    @Query("SELECT * FROM health_records WHERE patientId = :patientId ORDER BY date DESC")
    fun getHealthRecordsByPatient(patientId: Long): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE patientId = :patientId ORDER BY date DESC LIMIT :limit")
    fun getRecentHealthRecords(patientId: Long, limit: Int): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE patientId = :patientId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getHealthRecordsByDateRange(patientId: Long, startDate: Long, endDate: Long): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE id = :id")
    suspend fun getHealthRecordById(id: Long): HealthRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(healthRecord: HealthRecord): Long

    @Update
    suspend fun updateHealthRecord(healthRecord: HealthRecord)

    @Delete
    suspend fun deleteHealthRecord(healthRecord: HealthRecord)

    @Query("DELETE FROM health_records WHERE id = :id")
    suspend fun deleteHealthRecordById(id: Long)
}