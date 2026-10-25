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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.nursingcare.app.data.model.Schedule
import com.nursingcare.app.ui.components.*
import com.nursingcare.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    schedules: List<Schedule>,
    patientId: Long?,
    onAdd: (Schedule) -> Unit,
    onUpdate: (Schedule) -> Unit,
    onDelete: (Schedule) -> Unit,
    onToggleComplete: (Schedule) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<Schedule?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日程提醒") },
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
                containerColor = Warning
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加", tint = OnPrimary)
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    message = "暂无日程安排\n点击右下角添加",
                    icon = Icons.Default.Event
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
                items(schedules) { schedule ->
                    ScheduleCardWithActions(
                        schedule = schedule,
                        onEdit = { editingSchedule = schedule },
                        onDelete = { onDelete(schedule) },
                        onToggleComplete = { onToggleComplete(schedule) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ScheduleDialog(
            schedule = null,
            patientId = patientId,
            onDismiss = { showAddDialog = false },
            onSave = { newSchedule ->
                onAdd(newSchedule)
                showAddDialog = false
            }
        )
    }

    editingSchedule?.let { schedule ->
        ScheduleDialog(
            schedule = schedule,
            patientId = patientId,
            onDismiss = { editingSchedule = null },
            onSave = { updatedSchedule ->
                onUpdate(updatedSchedule)
                editingSchedule = null
            }
        )
    }
}

@Composable
fun ScheduleCardWithActions(
    schedule: Schedule,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(schedule.dateTime)

    val typeColor = when (schedule.type) {
        "复诊" -> Error
        "康复训练" -> Success
        "护理计划" -> Info
        else -> Primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isCompleted) LightGray.copy(alpha = 0.3f) else Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = schedule.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (schedule.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = typeColor.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = schedule.type,
                            style = MaterialTheme.typography.bodySmall,
                            color = typeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${dateFormat.format(date)} ${timeFormat.format(date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray
                )
                if (schedule.repeatType != "不重复") {
                    Text(
                        text = schedule.repeatType,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkGray
                    )
                }
            }

            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = Error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    schedule: Schedule?,
    patientId: Long?,
    onDismiss: () -> Unit,
    onSave: (Schedule) -> Unit
) {
    var title by remember { mutableStateOf(schedule?.title ?: "") }
    var type by remember { mutableStateOf(schedule?.type ?: "复诊") }
    var repeatType by remember { mutableStateOf(schedule?.repeatType ?: "不重复") }
    var notes by remember { mutableStateOf(schedule?.notes ?: "") }

    // Simple time selection (hours and minutes)
    var hour by remember { mutableStateOf(9) }
    var minute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (schedule == null) "添加日程" else "编辑日程") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("日程标题") },
                    modifier = Modifier.fillMaxWidth()
                )

                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("类型") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        listOf("复诊", "康复训练", "护理计划", "其他").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = hour.toString(),
                        onValueChange = { newHour ->
                            newHour.toIntOrNull()?.let {
                                if (it in 0..23) hour = it
                            }
                        },
                        label = { Text("小时") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minute.toString(),
                        onValueChange = { newMinute ->
                            newMinute.toIntOrNull()?.let {
                                if (it in 0..59) minute = it
                            }
                        },
                        label = { Text("分钟") },
                        modifier = Modifier.weight(1f)
                    )
                }

                var repeatExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = repeatExpanded,
                    onExpandedChange = { repeatExpanded = it }
                ) {
                    OutlinedTextField(
                        value = repeatType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("重复") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = repeatExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = repeatExpanded,
                        onDismissRequest = { repeatExpanded = false }
                    ) {
                        listOf("不重复", "每天", "每周", "每月").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    repeatType = option
                                    repeatExpanded = false
                                }
                            )
                        }
                    }
                }

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
                    if (title.isNotBlank() && patientId != null) {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        onSave(
                            Schedule(
                                id = schedule?.id ?: 0,
                                patientId = patientId,
                                title = title,
                                type = type,
                                dateTime = schedule?.dateTime ?: calendar.timeInMillis,
                                repeatType = repeatType,
                                notes = notes,
                                isCompleted = schedule?.isCompleted ?: false
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
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