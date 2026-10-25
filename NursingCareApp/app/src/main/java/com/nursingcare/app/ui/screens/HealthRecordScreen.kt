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
import com.nursingcare.app.data.model.HealthRecord
import com.nursingcare.app.ui.components.*
import com.nursingcare.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordScreen(
    healthRecords: List<HealthRecord>,
    patientId: Long?,
    onAdd: (HealthRecord) -> Unit,
    onUpdate: (HealthRecord) -> Unit,
    onDelete: (HealthRecord) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<HealthRecord?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("健康记录") },
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
                containerColor = Error
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加", tint = OnPrimary)
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (healthRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    message = "暂无健康记录\n点击右下角添加",
                    icon = Icons.Default.Favorite
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
                items(healthRecords) { record ->
                    HealthRecordCard(
                        record = record,
                        onEdit = { editingRecord = record },
                        onDelete = { onDelete(record) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        HealthRecordDialog(
            record = null,
            patientId = patientId,
            onDismiss = { showAddDialog = false },
            onSave = { record ->
                onAdd(record)
                showAddDialog = false
            }
        )
    }

    editingRecord?.let { record ->
        HealthRecordDialog(
            record = record,
            patientId = patientId,
            onDismiss = { editingRecord = null },
            onSave = { updatedRecord ->
                onUpdate(updatedRecord)
                editingRecord = null
            }
        )
    }
}

@Composable
fun HealthRecordCard(
    record: HealthRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(record.date)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
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
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                record.temperature?.let {
                    HealthMetric(
                        icon = Icons.Default.Thermostat,
                        label = "体温",
                        value = "${it}°C",
                        color = Warning
                    )
                }
                record.bloodPressureSystolic?.let {
                    HealthMetric(
                        icon = Icons.Default.Favorite,
                        label = "血压",
                        value = "${it}/${record.bloodPressureDiastolic ?: 0}",
                        color = Error
                    )
                }
                record.bloodSugar?.let {
                    HealthMetric(
                        icon = Icons.Default.Bloodtype,
                        label = "血糖",
                        value = "${it}",
                        color = Primary
                    )
                }
                record.weight?.let {
                    HealthMetric(
                        icon = Icons.Default.MonitorWeight,
                        label = "体重",
                        value = "${it}kg",
                        color = Secondary
                    )
                }
            }

            if (record.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = record.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray
                )
            }
        }
    }
}

@Composable
fun HealthMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DarkGray
        )
    }
}

@Composable
fun HealthRecordDialog(
    record: HealthRecord?,
    patientId: Long?,
    onDismiss: () -> Unit,
    onSave: (HealthRecord) -> Unit
) {
    var temperature by remember { mutableStateOf(record?.temperature?.toString() ?: "") }
    var bpSystolic by remember { mutableStateOf(record?.bloodPressureSystolic?.toString() ?: "") }
    var bpDiastolic by remember { mutableStateOf(record?.bloodPressureDiastolic?.toString() ?: "") }
    var bloodSugar by remember { mutableStateOf(record?.bloodSugar?.toString() ?: "") }
    var weight by remember { mutableStateOf(record?.weight?.toString() ?: "") }
    var notes by remember { mutableStateOf(record?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (record == null) "添加健康记录" else "编辑健康记录") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = temperature,
                        onValueChange = { temperature = it },
                        label = { Text("体温 (°C)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("体重 (kg)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = bpSystolic,
                        onValueChange = { bpSystolic = it },
                        label = { Text("收缩压") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bpDiastolic,
                        onValueChange = { bpDiastolic = it },
                        label = { Text("舒张压") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = bloodSugar,
                    onValueChange = { bloodSugar = it },
                    label = { Text("血糖 (mmol/L)") },
                    modifier = Modifier.fillMaxWidth()
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
                    if (patientId != null) {
                        onSave(
                            HealthRecord(
                                id = record?.id ?: 0,
                                patientId = patientId,
                                date = record?.date ?: System.currentTimeMillis(),
                                temperature = temperature.toFloatOrNull(),
                                bloodPressureSystolic = bpSystolic.toIntOrNull(),
                                bloodPressureDiastolic = bpDiastolic.toIntOrNull(),
                                bloodSugar = bloodSugar.toFloatOrNull(),
                                weight = weight.toFloatOrNull(),
                                notes = notes
                            )
                        )
                    }
                }
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