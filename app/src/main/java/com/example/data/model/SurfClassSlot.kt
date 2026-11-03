package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surf_slots")
data class SurfClassSlot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val time: String,
    val period: String = "Manhã",
    val instructor: String = "Instrutor Gabriel",
    val maxSpots: Int = 4,
    val isWeatherCancelled: Boolean = false,
    val weatherCancelReason: String = "Condições marítimas desfavoráveis (vento forte / correnteza)",
    val isClosedClass: Boolean = false,
    val closedClassName: String = "",
    val date: String = "",
    val orderIndex: Int = 0
)
