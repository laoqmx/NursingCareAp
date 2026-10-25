package com.nursingcare.app.data.database

import androidx.room.*
import com.nursingcare.app.data.model.NursingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface NursingLogDao {
    @Query("SELECT * FROM nursing_logs WHERE patientId = :patientId ORDER BY date DESC")
    fun getNursingLogsByPatient(patientId: Long): Flow<List<NursingLog>>

    @Query("SELECT * FROM nursing_logs WHERE patientId = :patientId ORDER BY date DESC LIMIT :limit")
    fun getRecentNursingLogs(patientId: Long, limit: Int): Flow<List<NursingLog>>

    @Query("SELECT * FROM nursing_logs WHERE patientId = :patientId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getNursingLogsByDateRange(patientId: Long, startDate: Long, endDate: Long): Flow<List<NursingLog>>

    @Query("SELECT * FROM nursing_logs WHERE id = :id")
    suspend fun getNursingLogById(id: Long): NursingLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNursingLog(nursingLog: NursingLog): Long

    @Update
    suspend fun updateNursingLog(nursingLog: NursingLog)

    @Delete
    suspend fun deleteNursingLog(nursingLog: NursingLog)

    @Query("DELETE FROM nursing_logs WHERE id = :id")
    suspend fun deleteNursingLogById(id: Long)
}