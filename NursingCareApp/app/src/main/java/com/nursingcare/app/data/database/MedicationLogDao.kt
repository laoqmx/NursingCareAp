package com.nursingcare.app.data.database

import androidx.room.*
import com.nursingcare.app.data.model.MedicationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationLogDao {
    @Query("SELECT * FROM medication_logs WHERE medicationId = :medicationId ORDER BY scheduledTime DESC")
    fun getMedicationLogsByMedication(medicationId: Long): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE scheduledTime BETWEEN :startTime AND :endTime ORDER BY scheduledTime ASC")
    fun getMedicationLogsByDateRange(startTime: Long, endTime: Long): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE status = :status ORDER BY scheduledTime ASC")
    fun getMedicationLogsByStatus(status: String): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE id = :id")
    suspend fun getMedicationLogById(id: Long): MedicationLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationLog(medicationLog: MedicationLog): Long

    @Update
    suspend fun updateMedicationLog(medicationLog: MedicationLog)

    @Delete
    suspend fun deleteMedicationLog(medicationLog: MedicationLog)

    @Query("UPDATE medication_logs SET status = :status, actualTime = :actualTime WHERE id = :id")
    suspend fun updateMedicationLogStatus(id: Long, status: String, actualTime: Long?)
}