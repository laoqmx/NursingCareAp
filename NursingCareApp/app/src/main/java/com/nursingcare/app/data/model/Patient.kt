package com.nursingcare.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String, // "男" or "女"
    val bloodType: String = "",
    val allergies: String = "",
    val medicalHistory: String = "",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val photoPath: String = "",
    val createdAt: Long = System.currentTimeMillis()
)