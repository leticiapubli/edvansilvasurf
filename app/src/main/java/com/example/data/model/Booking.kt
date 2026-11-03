package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val slotId: Long,
    val studentName: String,
    val bookedAt: Long = System.currentTimeMillis()
)
