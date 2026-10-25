package com.nursingcare.app.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nursingcare.app.data.model.*
import com.nursingcare.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    patient: Patient?,
    healthRecords: List<HealthRecord>,
    nursingLogs: List<NursingLog>,
    medications: List<Medication>,
    schedules: List<Schedule>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("导出数据") },
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
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (patient == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Warning.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Warning
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("请先选择患者再导出数据")
                    }
                }
            } else {
                // Patient info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "患者信息",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = patient.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${patient.age}岁 ${patient.gender}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGray
                        )
                    }
                }

                // Export options
                Text(
                    text = "选择要导出的数据",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ExportOptionCard(
                    icon = Icons.Default.Person,
                    title = "患者信息",
                    description = "基本信息、健康资料、紧急联系人",
                    count = "1条",
                    checked = true,
                    onCheckedChange = {}
                )

                ExportOptionCard(
                    icon = Icons.Default.Favorite,
                    title = "健康记录",
                    description = "体温、血压、血糖、体重等",
                    count = "${healthRecords.size}条",
                    checked = true,
                    onCheckedChange = {}
                )

                ExportOptionCard(
                    icon = Icons.Default.EditNote,
                    title = "护理日志",
                    description = "护理记录、心情、食欲、睡眠",
                    count = "${nursingLogs.size}条",
                    checked = true,
                    onCheckedChange = {}
                )

                ExportOptionCard(
                    icon = Icons.Default.Medication,
                    title = "用药记录",
                    description = "用药历史和提醒设置",
                    count = "${medications.size}条",
                    checked = true,
                    onCheckedChange = {}
                )

                ExportOptionCard(
                    icon = Icons.Default.Event,
                    title = "日程安排",
                    description = "复诊、康复训练等日程",
                    count = "${schedules.size}条",
                    checked = true,
                    onCheckedChange = {}
                )

                Spacer(modifier = Modifier.weight(1f))

                // Export button
                Button(
                    onClick = {
                        isExporting = true
                        exportData(
                            context = context,
                            patient = patient,
                            healthRecords = healthRecords,
                            nursingLogs = nursingLogs,
                            medications = medications,
                            schedules = schedules
                        )
                        isExporting = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isExporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = OnPrimary
                        )
                    } else {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("导出为文本文件", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Text(
                    text = "导出的文件可以分享给医生或保存备份",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun ExportOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    count: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray
                )
            }
            Text(
                text = count,
                style = MaterialTheme.typography.bodyMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun exportData(
    context: Context,
    patient: Patient,
    healthRecords: List<HealthRecord>,
    nursingLogs: List<NursingLog>,
    medications: List<Medication>,
    schedules: List<Schedule>
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val now = Date()

    val content = buildString {
        appendLine("=".repeat(50))
        appendLine("护理记录导出")
        appendLine("导出时间: ${dateFormat.format(now)}")
        appendLine("=".repeat(50))
        appendLine()

        // Patient info
        appendLine("【患者信息】")
        appendLine("姓名: ${patient.name}")
        appendLine("年龄: ${patient.age}岁")
        appendLine("性别: ${patient.gender}")
        if (patient.bloodType.isNotBlank()) appendLine("血型: ${patient.bloodType}")
        if (patient.allergies.isNotBlank()) appendLine("过敏源: ${patient.allergies}")
        if (patient.medicalHistory.isNotBlank()) appendLine("病史: ${patient.medicalHistory}")
        if (patient.emergencyContact.isNotBlank()) appendLine("紧急联系人: ${patient.emergencyContact}")
        if (patient.emergencyPhone.isNotBlank()) appendLine("紧急电话: ${patient.emergencyPhone}")
        appendLine()

        // Health records
        if (healthRecords.isNotEmpty()) {
            appendLine("【健康记录】")
            healthRecords.forEach { record ->
                appendLine("-".repeat(30))
                appendLine("日期: ${dateFormat.format(Date(record.date))}")
                record.temperature?.let { appendLine("体温: ${it}°C") }
                record.bloodPressureSystolic?.let {
                    appendLine("血压: ${it}/${record.bloodPressureDiastolic ?: 0} mmHg")
                }
                record.bloodSugar?.let { appendLine("血糖: ${it} mmol/L") }
                record.weight?.let { appendLine("体重: ${it} kg") }
                if (record.notes.isNotBlank()) appendLine("备注: ${record.notes}")
            }
            appendLine()
        }

        // Nursing logs
        if (nursingLogs.isNotEmpty()) {
            appendLine("【护理日志】")
            nursingLogs.forEach { log ->
                appendLine("-".repeat(30))
                appendLine("日期: ${dateFormat.format(Date(log.date))}")
                appendLine("内容: ${log.content}")
                appendLine("心情: ${"★".repeat(log.mood)}${"☆".repeat(5 - log.mood)}")
                appendLine("食欲: ${"★".repeat(log.appetite)}${"☆".repeat(5 - log.appetite)}")
                appendLine("睡眠: ${"★".repeat(log.sleepQuality)}${"☆".repeat(5 - log.sleepQuality)}")
            }
            appendLine()
        }

        // Medications
        if (medications.isNotEmpty()) {
            appendLine("【用药记录】")
            medications.forEach { med ->
                appendLine("-".repeat(30))
                appendLine("药品: ${med.name}")
                appendLine("剂量: ${med.dosage}")
                appendLine("频率: ${med.frequency}")
                appendLine("时间: ${med.times}")
                appendLine("开始: ${dateFormat.format(Date(med.startDate))}")
                med.endDate?.let { appendLine("结束: ${dateFormat.format(Date(it))}") }
                if (med.notes.isNotBlank()) appendLine("备注: ${med.notes}")
            }
            appendLine()
        }

        // Schedules
        if (schedules.isNotEmpty()) {
            appendLine("【日程安排】")
            schedules.forEach { schedule ->
                appendLine("-".repeat(30))
                appendLine("标题: ${schedule.title}")
                appendLine("类型: ${schedule.type}")
                appendLine("时间: ${dateFormat.format(Date(schedule.dateTime))}")
                appendLine("重复: ${schedule.repeatType}")
                if (schedule.notes.isNotBlank()) appendLine("备注: ${schedule.notes}")
            }
            appendLine()
        }

        appendLine("=".repeat(50))
        appendLine("导出完成")
        appendLine("=".repeat(50))
    }

    // Save and share
    val fileName = "护理记录_${fileNameFormat.format(now)}.txt"
    try {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write(content.toByteArray())
        }

        val file = context.getFileStreamPath(fileName)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            ))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "分享护理记录"))

        Toast.makeText(context, "导出成功", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "导出失败: ${e.message}", Toast.LENGTH_LONG).show()
    }
}