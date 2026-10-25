package com.nursingcare.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nursingcare.app.data.model.*
import com.nursingcare.app.ui.components.*
import com.nursingcare.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    patients: List<Patient>,
    selectedPatient: Patient?,
    medications: List<Medication>,
    schedules: List<Schedule>,
    onNavigateToPatientInfo: () -> Unit,
    onNavigateToMedication: () -> Unit,
    onNavigateToHealthRecord: () -> Unit,
    onNavigateToNursingLog: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onSelectPatient: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "护理助手",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = OnPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Patient selector
            item {
                PatientSelector(
                    patients = patients,
                    selectedPatient = selectedPatient,
                    onSelectPatient = onSelectPatient
                )
            }

            if (selectedPatient != null) {
                // Quick stats
                item {
                    SectionHeader(title = "概览")
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardCard(
                            title = "用药提醒",
                            icon = Icons.Default.Medication,
                            count = medications.size.toString(),
                            subtitle = "待服药",
                            color = Primary,
                            onClick = onNavigateToMedication,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = "日程",
                            icon = Icons.Default.Event,
                            count = schedules.size.toString(),
                            subtitle = "今日安排",
                            color = Secondary,
                            onClick = onNavigateToSchedule,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Quick actions
                item {
                    SectionHeader(title = "快捷操作")
                }

                item {
                    QuickActionsGrid(
                        onPatientInfo = onNavigateToPatientInfo,
                        onMedication = onNavigateToMedication,
                        onHealthRecord = onNavigateToHealthRecord,
                        onNursingLog = onNavigateToNursingLog,
                        onSchedule = onNavigateToSchedule,
                        onEmergency = onNavigateToEmergency
                    )
                }

                // Today's reminders
                if (medications.isNotEmpty()) {
                    item {
                        SectionHeader(title = "今日用药")
                    }

                    items(medications.take(3)) { medication ->
                        MedicationReminderCard(medication = medication)
                    }
                }

                // Upcoming schedules
                if (schedules.isNotEmpty()) {
                    item {
                        SectionHeader(title = "即将到来")
                    }

                    items(schedules.take(3)) { schedule ->
                        ScheduleCard(schedule = schedule)
                    }
                }
            } else {
                item {
                    EmptyState(
                        message = "请先添加患者信息",
                        icon = Icons.Default.PersonAdd
                    )
                }
            }
        }
    }
}

@Composable
fun PatientSelector(
    patients: List<Patient>,
    selectedPatient: Patient?,
    onSelectPatient: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "当前患者",
                style = MaterialTheme.typography.labelMedium,
                color = DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedPatient?.name ?: "请选择患者",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    patients.forEach { patient ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(patient.name, fontWeight = FontWeight.Medium)
                                    Text(
                                        "${patient.age}岁 ${patient.gender}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkGray
                                    )
                                }
                            },
                            onClick = {
                                onSelectPatient(patient.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid(
    onPatientInfo: () -> Unit,
    onMedication: () -> Unit,
    onHealthRecord: () -> Unit,
    onNursingLog: () -> Unit,
    onSchedule: () -> Unit,
    onEmergency: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                icon = Icons.Default.Person,
                label = "患者信息",
                color = Primary,
                onClick = onPatientInfo,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.Medication,
                label = "用药提醒",
                color = Success,
                onClick = onMedication,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.Favorite,
                label = "健康记录",
                color = Error,
                onClick = onHealthRecord,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                icon = Icons.Default.EditNote,
                label = "护理日志",
                color = Info,
                onClick = onNursingLog,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.Event,
                label = "日程提醒",
                color = Warning,
                onClick = onSchedule,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Default.Phone,
                label = "紧急呼叫",
                color = Error,
                onClick = onEmergency,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MedicationReminderCard(medication: Medication) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = Success.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${medication.dosage} · ${medication.times}",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray
                )
            }
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeString = dateFormat.format(Date(schedule.dateTime))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = Warning.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = schedule.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray
                )
            }
            Text(
                text = timeString,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}