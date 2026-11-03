package com.example.data.model

data class SurfSlotWithBookings(
    val slot: SurfClassSlot,
    val bookings: List<Booking> = emptyList()
) {
    val totalSpots: Int get() = slot.maxSpots
    val bookedCount: Int get() = bookings.size
    val availableSpots: Int get() = (totalSpots - bookedCount).coerceAtLeast(0)
    val isFull: Boolean get() = bookedCount >= totalSpots
    val isCancelled: Boolean get() = slot.isWeatherCancelled

    fun isBookedBy(studentName: String): Boolean {
        if (studentName.isBlank()) return false
        return bookings.any { it.studentName.trim().equals(studentName.trim(), ignoreCase = true) }
    }

    fun getBookingFor(studentName: String): Booking? {
        if (studentName.isBlank()) return null
        return bookings.firstOrNull { it.studentName.trim().equals(studentName.trim(), ignoreCase = true) }
    }
}
