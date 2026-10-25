package com.nursingcare.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nursingcare.app.NursingCareApp
import com.nursingcare.app.data.model.*
import com.nursingcare.app.data.repository.NursingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class NursingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NursingRepository

    init {
        val nursingApp = application as NursingCareApp
        repository = nursingApp.repository
    }

    // Selected patient
    private val _selectedPatientId = MutableStateFlow<Long?>(null)
    val selectedPatientId: StateFlow<Long?> = _selectedPatientId.asStateFlow()

    // Patient operations
    val allPatients: StateFlow<List<Patient>> = repository.getAllPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPatient(patientId: Long) {
        _selectedPatientId.value = patientId
    }

    val selectedPatient: StateFlow<Patient?> = _selectedPatientId
        .flatMapLatest { id ->
            if (id != null) repository.getPatientByIdFlow(id)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun insertPatient(patient: Patient) = viewModelScope.launch {
        repository.insertPatient(patient)
    }

    fun updatePatient(patient: Patient) = viewModelScope.launch {
        repository.updatePatient(patient)
    }

    fun deletePatient(patient: Patient) = viewModelScope.launch {
        repository.deletePatient(patient)
    }

    // Medication operations
    val currentPatientMedications: StateFlow<List<Medication>> = _selectedPatientId
        .flatMapLatest { id ->
            if (id != null) repository.getMedicationsByPatient(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertMedication(medication: Medication) = viewModelScope.launch {
        repository.insertMedication(medication)
    }

    fun updateMedication(medication: Medication) = viewModelScope.launch {
        repository.updateMedication(medication)
    }

    fun deleteMedication(medication: Medication) = viewModelScope.launch {
        repository.deleteMedication(medication)
    }

    // Health Record operations
    val currentPatientHealthRecords: StateFlow<List<HealthRecord>> = _selectedPatientId
        .flatMapLatest { id ->
            if (id != null) repository.getHealthRecordsByPatient(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertHealthRecord(healthRecord: HealthRecord) = viewModelScope.launch {
        repository.insertHealthRecord(healthRecord)
    }

    fun updateHealthRecord(healthRecord: HealthRecord) = viewModelScope.launch {
        repository.updateHealthRecord(healthRecord)
    }

    fun deleteHealthRecord(healthRecord: HealthRecord) = viewModelScope.launch {
        repository.deleteHealthRecord(healthRecord)
    }

    // Nursing Log operations
    val currentPatientNursingLogs: StateFlow<List<NursingLog>> = _selectedPatientId
        .flatMapLatest { id ->
            if (id != null) repository.getNursingLogsByPatient(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertNursingLog(nursingLog: NursingLog) = viewModelScope.launch {
        repository.insertNursingLog(nursingLog)
    }

    fun updateNursingLog(nursingLog: NursingLog) = viewModelScope.launch {
        repository.updateNursingLog(nursingLog)
    }

    fun deleteNursingLog(nursingLog: NursingLog) = viewModelScope.launch {
        repository.deleteNursingLog(nursingLog)
    }

    // Schedule operations
    val currentPatientSchedules: StateFlow<List<Schedule>> = _selectedPatientId
        .flatMapLatest { id ->
            if (id != null) repository.getSchedulesByPatient(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertSchedule(schedule: Schedule) = viewModelScope.launch {
        repository.insertSchedule(schedule)
    }

    fun updateSchedule(schedule: Schedule) = viewModelScope.launch {
        repository.updateSchedule(schedule)
    }

    fun deleteSchedule(schedule: Schedule) = viewModelScope.launch {
        repository.deleteSchedule(schedule)
    }

    fun toggleScheduleCompletion(schedule: Schedule) = viewModelScope.launch {
        repository.updateScheduleStatus(schedule.id, !schedule.isCompleted)
    }

    // Get today's medication reminders
    fun getTodayMedicationReminders(): Flow<List<Medication>> = repository.getActiveMedications()

    // Get today's schedules
    fun getTodaySchedules(): Flow<List<Schedule>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis

        return repository.getSchedulesByDateRange(startOfDay, endOfDay)
    }
}