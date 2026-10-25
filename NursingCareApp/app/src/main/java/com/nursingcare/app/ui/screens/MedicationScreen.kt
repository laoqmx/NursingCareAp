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
import com.nursingcare.app.data.model.Medication
import com.nursingcare.app.ui.components.*
import com.nursingcare.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    medications: List<Medication>,
    patientId: Long?,
    onAdd: (Medication) -> Unit,
    onUpdate: (Medication) -> Unit,
    onDelete: (Medication) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<Medication?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("用药提醒") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = OnPrimary,
                    navigationIconContentColor = OnPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Success
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加", tint = OnPrimary)
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (medications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    message = "暂无用药提醒\n点击右下角添加",
                    icon = Icons.Default.Medication
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications) { medication ->
                    MedicationCard(
                        medication = medication,
                        onEdit = { editingMedication = medication },
                        onDelete = { onDelete(medication) },
                        onToggleActive = {
                            onUpdate(medication.copy(isActive = !medication.isActive))
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        MedicationDialog(
            medication = null,
            patientId = patientId,
            onDismiss = { showAddDialog = false },
            onSave = { medication ->
                onAdd(medication)
                showAddDialog = false
            }
        )
    }

    editingMedication?.let { medication ->
        MedicationDialog(
            medication = medication,
            patientId = patientId,
            onDismiss = { editingMedication = null },
            onSave = { updatedMedication ->
                onUpdate(updatedMedication)
                editingMedication = null
            }
        )
    }
}

@Composable
fun MedicationCard(
    medication: Medication,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (medication.isActive) Surface else LightGray.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = medication.dosage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGray
                    )
                }
                Row {
                    IconButton(onClick = onToggleActive) {
                        Icon(
                            imageVector = if (medication.isActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = "切换提醒",
                            tint = if (medication.isActive) Success else DarkGray
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = Error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow(
                    label = "频率",
                    value = medication.frequency
                )
                InfoRow(
                    label = "时间",
                    value = medication.times
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow(
                    label = "开始",
                    value = dateFormat.format(Date(medication.startDate))
                )
                medication.endDate?.let {
                    InfoRow(
                        label = "结束",
                        value = dateFormat.format(Date(it))
                    )
                }
            }

            if (medication.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = medication.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDialog(
    medication: Medication?,
    patientId: Long?,
    onDismiss: () -> Unit,
    onSave: (Medication) -> Unit
) {
    var name by remember { mutableStateOf(medication?.name ?: "") }
    var dosage by remember { mutableStateOf(medication?.dosage ?: "") }
    var frequency by remember { mutableStateOf(medication?.frequency ?: "每日1次") }
    var times by remember { mutableStateOf(medication?.times ?: "08:00") }
    var notes by remember { mutableStateOf(medication?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (medication == null) "添加用药" else "编辑用药") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("药品名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("剂量") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("如：1片") }
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("频率") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("每日1次", "每日2次", "每日3次", "每周1次", "每周2次", "每月1次").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    frequency = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = times,
                    onValueChange = { times = it },
                    label = { Text("提醒时间") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("如：08:00,12:00,18:00") }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("备注（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank() && patientId != null) {
                        onSave(
                            Medication(
                                id = medication?.id ?: 0,
                                patientId = patientId,
                                name = name,
                                dosage = dosage,
                                frequency = frequency,
                                times = times,
                                startDate = medication?.startDate ?: System.currentTimeMillis(),
                                endDate = medication?.endDate,
                                notes = notes,
                                isActive = medication?.isActive ?: true
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && dosage.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}