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
import com.nursingcare.app.data.model.NursingLog
import com.nursingcare.app.ui.components.*
import com.nursingcare.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursingLogScreen(
    nursingLogs: List<NursingLog>,
    patientId: Long?,
    onAdd: (NursingLog) -> Unit,
    onUpdate: (NursingLog) -> Unit,
    onDelete: (NursingLog) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingLog by remember { mutableStateOf<NursingLog?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("护理日志") },
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
                containerColor = Info
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加", tint = OnPrimary)
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (nursingLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    message = "暂无护理日志\n点击右下角添加",
                    icon = Icons.Default.EditNote
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
                items(nursingLogs) { log ->
                    NursingLogCard(
                        log = log,
                        onEdit = { editingLog = log },
                        onDelete = { onDelete(log) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        NursingLogDialog(
            log = null,
            patientId = patientId,
            onDismiss = { showAddDialog = false },
            onSave = { newLog ->
                onAdd(newLog)
                showAddDialog = false
            }
        )
    }

    editingLog?.let { log ->
        NursingLogDialog(
            log = log,
            patientId = patientId,
            onDismiss = { editingLog = null },
            onSave = { updatedLog ->
                onUpdate(updatedLog)
                editingLog = null
            }
        )
    }
}

@Composable
fun NursingLogCard(
    log: NursingLog,
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
                Column {
                    Text(
                        text = dateFormat.format(Date(log.date)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = Error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = log.content,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RatingDisplay(label = "心情", rating = log.mood)
                RatingDisplay(label = "食欲", rating = log.appetite)
                RatingDisplay(label = "睡眠", rating = log.sleepQuality)
            }
        }
    }
}

@Composable
fun RatingDisplay(label: String, rating: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DarkGray
        )
        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (index < rating) Warning else LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun NursingLogDialog(
    log: NursingLog?,
    patientId: Long?,
    onDismiss: () -> Unit,
    onSave: (NursingLog) -> Unit
) {
    var content by remember { mutableStateOf(log?.content ?: "") }
    var mood by remember { mutableIntStateOf(log?.mood ?: 3) }
    var appetite by remember { mutableIntStateOf(log?.appetite ?: 3) }
    var sleepQuality by remember { mutableIntStateOf(log?.sleepQuality ?: 3) }
    var notes by remember { mutableStateOf(log?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (log == null) "添加护理日志" else "编辑护理日志") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("护理内容") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Column {
                    Text("心情评分", style = MaterialTheme.typography.bodyMedium)
                    RatingSelector(selected = mood, onSelect = { mood = it })
                }

                Column {
                    Text("食欲评分", style = MaterialTheme.typography.bodyMedium)
                    RatingSelector(selected = appetite, onSelect = { appetite = it })
                }

                Column {
                    Text("睡眠质量", style = MaterialTheme.typography.bodyMedium)
                    RatingSelector(selected = sleepQuality, onSelect = { sleepQuality = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (content.isNotBlank() && patientId != null) {
                        onSave(
                            NursingLog(
                                id = log?.id ?: 0,
                                patientId = patientId,
                                date = log?.date ?: System.currentTimeMillis(),
                                content = content,
                                mood = mood,
                                appetite = appetite,
                                sleepQuality = sleepQuality,
                                notes = notes
                            )
                        )
                    }
                },
                enabled = content.isNotBlank()
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

@Composable
fun RatingSelector(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        repeat(5) { index ->
            IconButton(
                onClick = { onSelect(index + 1) }
            ) {
                Icon(
                    imageVector = if (index < selected) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (index < selected) Warning else LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}