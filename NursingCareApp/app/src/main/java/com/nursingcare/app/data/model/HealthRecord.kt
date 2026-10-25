package com.nursingcare.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_records",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val date: Long,
    val temperature: Float? = null, // 体温
    val bloodPressureSystolic: Int? = null, // 收缩压
    val bloodPressureDiastolic: Int? = null, // 舒张压
    val bloodSugar: Float? = null, // 血糖
    val weight: Float? = null, // 体重 kg
    val heartRate: Int? = null, // 心率
    val oxygenSaturation: Int? = null, // 血氧
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)