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
import com.nursingcare.app.data.model.Patient
import com.nursingcare.app.ui.components.*
import com.nursingcare.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInfoScreen(
    patient: Patient?,
    onSave: (Patient) -> Unit,
    onDelete: (Patient) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(patient) { mutableStateOf(patient?.name ?: "") }
    var age by remember(patient) { mutableStateOf(patient?.age?.toString() ?: "") }
    var gender by remember(patient) { mutableStateOf(patient?.gender ?: "男") }
    var bloodType by remember(patient) { mutableStateOf(patient?.bloodType ?: "") }
    var allergies by remember(patient) { mutableStateOf(patient?.allergies ?: "") }
    var medicalHistory by remember(patient) { mutableStateOf(patient?.medicalHistory ?: "") }
    var emergencyContact by remember(patient) { mutableStateOf(patient?.emergencyContact ?: "") }
    var emergencyPhone by remember(patient) { mutableStateOf(patient?.emergencyPhone ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (patient == null) "添加患者" else "患者信息") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = OnPrimary,
                    navigationIconContentColor = OnPrimary
                ),
                actions = {
                    if (patient != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除", tint = OnPrimary)
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "基本信息",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        FormTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "姓名"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FormNumberField(
                                value = age,
                                onValueChange = { age = it },
                                label = "年龄",
                                modifier = Modifier.weight(1f),
                                suffix = "岁"
                            )

                            FormDropdown(
                                value = gender,
                                onValueChange = { gender = it },
                                label = "性别",
                                options = listOf("男", "女"),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        FormDropdown(
                            value = bloodType,
                            onValueChange = { bloodType = it },
                            label = "血型",
                            options = listOf("", "A", "B", "AB", "O")
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "健康信息",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        FormTextField(
                            value = allergies,
                            onValueChange = { allergies = it },
                            label = "过敏源",
                            singleLine = false,
                            maxLines = 3
                        )

                        FormTextField(
                            value = medicalHistory,
                            onValueChange = { medicalHistory = it },
                            label = "病史",
                            singleLine = false,
                            maxLines = 4
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "紧急联系人",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        FormTextField(
                            value = emergencyContact,
                            onValueChange = { emergencyContact = it },
                            label = "联系人姓名"
                        )

                        FormTextField(
                            value = emergencyPhone,
                            onValueChange = { emergencyPhone = it },
                            label = "联系电话"
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (name.isNotBlank() && age.isNotBlank()) {
                            val newPatient = Patient(
                                id = patient?.id ?: 0,
                                name = name,
                                age = age.toIntOrNull() ?: 0,
                                gender = gender,
                                bloodType = bloodType,
                                allergies = allergies,
                                medicalHistory = medicalHistory,
                                emergencyContact = emergencyContact,
                                emergencyPhone = emergencyPhone
                            )
                            onSave(newPatient)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && age.isNotBlank()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("保存")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除该患者信息吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        patient?.let { onDelete(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("删除", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}