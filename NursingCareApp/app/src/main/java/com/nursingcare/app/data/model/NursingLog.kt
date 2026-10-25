package com.nursingcare.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nursing_logs",
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
data class NursingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val date: Long,
    val content: String,
    val mood: Int = 3, // 1-5，心情评分
    val appetite: Int = 3, // 1-5，食欲评分
    val sleepQuality: Int = 3, // 1-5，睡眠质量评分
    val photos: String = "", // 逗号分隔的照片路径
    val tags: String = "", // 逗号分隔的标签
    val createdAt: Long = System.currentTimeMillis()
)