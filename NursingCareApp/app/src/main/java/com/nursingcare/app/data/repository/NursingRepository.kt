package com.nursingcare.app.data.repository

import com.nursingcare.app.data.database.*
import com.nursingcare.app.data.model.*
import kotlinx.coroutines.flow.Flow

class NursingRepository(
    private val patientDao: PatientDao,
    private val medicationDao: MedicationDao,
    private val healthRecordDao: HealthRecordDao,
    private val nursingLogDao: NursingLogDao,
    private val scheduleDao: ScheduleDao,
    private val medicationLogDao: MedicationLogDao
) {
    // Patient operations
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()
    fun getPatientByIdFlow(id: Long): Flow<Patient?> = patientDao.getPatientByIdFlow(id)
    suspend fun getPatientById(id: Long): Patient? = patientDao.getPatientById(id)
    suspend fun insertPatient(patient: Patient): Long = patientDao.insertPatient(patient)
    suspend fun updatePatient(patient: Patient) = patientDao.updatePatient(patient)
    suspend fun deletePatient(patient: Patient) = patientDao.deletePatient(patient)
    fun getPatientCount(): Flow<Int> = patientDao.getPatientCount()

    // Medication operations
    fun getMedicationsByPatient(patientId: Long): Flow<List<Medication>> = medicationDao.getMedicationsByPatient(patientId)
    fun getActiveMedications(): Flow<List<Medication>> = medicationDao.getActiveMedications()
    suspend fun getMedicationById(id: Long): Medication? = medicationDao.getMedicationById(id)
    suspend fun insertMedication(medication: Medication): Long = medicationDao.insertMedication(medication)
    suspend fun updateMedication(medication: Medication) = medicationDao.updateMedication(medication)
    suspend fun deleteMedication(medication: Medication) = medicationDao.deleteMedication(medication)

    // Health Record operations
    fun getHealthRecordsByPatient(patientId: Long): Flow<List<HealthRecord>> = healthRecordDao.getHealthRecordsByPatient(patientId)
    fun getRecentHealthRecords(patientId: Long, limit: Int = 10): Flow<List<HealthRecord>> = healthRecordDao.getRecentHealthRecords(patientId, limit)
    fun getHealthRecordsByDateRange(patientId: Long, startDate: Long, endDate: Long): Flow<List<HealthRecord>> = healthRecordDao.getHealthRecordsByDateRange(patientId, startDate, endDate)
    suspend fun getHealthRecordById(id: Long): HealthRecord? = healthRecordDao.getHealthRecordById(id)
    suspend fun insertHealthRecord(healthRecord: HealthRecord): Long = healthRecordDao.insertHealthRecord(healthRecord)
    suspend fun updateHealthRecord(healthRecord: HealthRecord) = healthRecordDao.updateHealthRecord(healthRecord)
    suspend fun deleteHealthRecord(healthRecord: HealthRecord) = healthRecordDao.deleteHealthRecord(healthRecord)

    // Nursing Log operations
    fun getNursingLogsByPatient(patientId: Long): Flow<List<NursingLog>> = nursingLogDao.getNursingLogsByPatient(patientId)
    fun getRecentNursingLogs(patientId: Long, limit: Int = 10): Flow<List<NursingLog>> = nursingLogDao.getRecentNursingLogs(patientId, limit)
    fun getNursingLogsByDateRange(patientId: Long, startDate: Long, endDate: Long): Flow<List<NursingLog>> = nursingLogDao.getNursingLogsByDateRange(patientId, startDate, endDate)
    suspend fun getNursingLogById(id: Long): NursingLog? = nursingLogDao.getNursingLogById(id)
    suspend fun insertNursingLog(nursingLog: NursingLog): Long = nursingLogDao.insertNursingLog(nursingLog)
    suspend fun updateNursingLog(nursingLog: NursingLog) = nursingLogDao.updateNursingLog(nursingLog)
    suspend fun deleteNursingLog(nursingLog: NursingLog) = nursingLogDao.deleteNursingLog(nursingLog)

    // Schedule operations
    fun getSchedulesByPatient(patientId: Long): Flow<List<Schedule>> = scheduleDao.getSchedulesByPatient(patientId)
    fun getUpcomingSchedules(patientId: Long): Flow<List<Schedule>> = scheduleDao.getUpcomingSchedules(patientId, System.currentTimeMillis())
    fun getSchedulesByDateRange(startTime: Long, endTime: Long): Flow<List<Schedule>> = scheduleDao.getSchedulesByDateRange(startTime, endTime)
    suspend fun getScheduleById(id: Long): Schedule? = scheduleDao.getScheduleById(id)
    suspend fun insertSchedule(schedule: Schedule): Long = scheduleDao.insertSchedule(schedule)
    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.updateSchedule(schedule)
    suspend fun deleteSchedule(schedule: Schedule) = scheduleDao.deleteSchedule(schedule)
    suspend fun updateScheduleStatus(id: Long, isCompleted: Boolean) = scheduleDao.updateScheduleStatus(id, isCompleted)

    // Medication Log operations
    fun getMedicationLogsByMedication(medicationId: Long): Flow<List<MedicationLog>> = medicationLogDao.getMedicationLogsByMedication(medicationId)
    suspend fun insertMedicationLog(medicationLog: MedicationLog): Long = medicationLogDao.insertMedicationLog(medicationLog)
    suspend fun updateMedicationLogStatus(id: Long, status: String, actualTime: Long?) = medicationLogDao.updateMedicationLogStatus(id, status, actualTime)
}