package com.nursingcare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nursingcare.app.ui.screens.*
import com.nursingcare.app.ui.theme.NursingCareTheme
import com.nursingcare.app.viewmodel.NursingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NursingCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NursingCareApp()
                }
            }
        }
    }
}

@Composable
fun NursingCareApp(viewModel: NursingViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    val patients by viewModel.allPatients.collectAsState()
    val selectedPatient by viewModel.selectedPatient.collectAsState()
    val medications by viewModel.currentPatientMedications.collectAsState()
    val healthRecords by viewModel.currentPatientHealthRecords.collectAsState()
    val nursingLogs by viewModel.currentPatientNursingLogs.collectAsState()
    val schedules by viewModel.currentPatientSchedules.collectAsState()

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            patients = patients,
            selectedPatient = selectedPatient,
            medications = medications,
            schedules = schedules,
            onNavigateToPatientInfo = { currentScreen = Screen.PATIENT_INFO },
            onNavigateToMedication = { currentScreen = Screen.MEDICATION },
            onNavigateToHealthRecord = { currentScreen = Screen.HEALTH_RECORD },
            onNavigateToNursingLog = { currentScreen = Screen.NURSING_LOG },
            onNavigateToSchedule = { currentScreen = Screen.SCHEDULE },
            onNavigateToEmergency = { currentScreen = Screen.EMERGENCY_CALL },
            onSelectPatient = { viewModel.selectPatient(it) }
        )

        Screen.PATIENT_INFO -> PatientInfoScreen(
            patient = selectedPatient,
            onSave = { patient ->
                if (patient.id == 0L) {
                    viewModel.insertPatient(patient)
                } else {
                    viewModel.updatePatient(patient)
                }
                currentScreen = Screen.HOME
            },
            onDelete = { patient ->
                viewModel.deletePatient(patient)
                currentScreen = Screen.HOME
            },
            onBack = { currentScreen = Screen.HOME }
        )

        Screen.MEDICATION -> MedicationScreen(
            medications = medications,
            patientId = selectedPatient?.id,
            onAdd = { viewModel.insertMedication(it) },
            onUpdate = { viewModel.updateMedication(it) },
            onDelete = { viewModel.deleteMedication(it) },
            onBack = { currentScreen = Screen.HOME }
        )

        Screen.HEALTH_RECORD -> HealthRecordScreen(
            healthRecords = healthRecords,
            patientId = selectedPatient?.id,
            onAdd = { viewModel.insertHealthRecord(it) },
            onUpdate = { viewModel.updateHealthRecord(it) },
            onDelete = { viewModel.deleteHealthRecord(it) },
            onBack = { currentScreen = Screen.HOME }
        )

        Screen.NURSING_LOG -> NursingLogScreen(
            nursingLogs = nursingLogs,
            patientId = selectedPatient?.id,
            onAdd = { viewModel.insertNursingLog(it) },
            onUpdate = { viewModel.updateNursingLog(it) },
            onDelete = { viewModel.deleteNursingLog(it) },
            onBack = { currentScreen = Screen.HOME }
        )

        Screen.SCHEDULE -> ScheduleScreen(
            schedules = schedules,
            patientId = selectedPatient?.id,
            onAdd = { viewModel.insertSchedule(it) },
            onUpdate = { viewModel.updateSchedule(it) },
            onDelete = { viewModel.deleteSchedule(it) },
            onToggleComplete = { viewModel.toggleScheduleCompletion(it) },
            onBack = { currentScreen = Screen.HOME }
        )

        Screen.EMERGENCY_CALL -> EmergencyCallScreen(
            patient = selectedPatient,
            onBack = { currentScreen = Screen.HOME }
        )

        Screen.EXPORT -> ExportScreen(
            patient = selectedPatient,
            healthRecords = healthRecords,
            nursingLogs = nursingLogs,
            medications = medications,
            schedules = schedules,
            onBack = { currentScreen = Screen.HOME }
        )
    }

    // Add export button to home screen
    LaunchedEffect(currentScreen) {
        // Could add export button to navigation
    }
}

enum class Screen {
    HOME,
    PATIENT_INFO,
    MEDICATION,
    HEALTH_RECORD,
    NURSING_LOG,
    SCHEDULE,
    EMERGENCY_CALL,
    EXPORT
}