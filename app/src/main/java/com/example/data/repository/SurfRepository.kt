package com.example.data.repository

import com.example.data.local.SurfDao
import com.example.data.model.Booking
import com.example.data.model.SurfClassSlot
import com.example.data.model.SurfSlotWithBookings
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class SurfRepository(private val surfDao: SurfDao) {

    val slotsWithBookings: Flow<List<SurfSlotWithBookings>> = combine(
        surfDao.getAllSlots(),
        surfDao.getAllBookings()
    ) { slots, allBookings ->
        slots.map { slot ->
            val slotBookings = allBookings.filter { it.slotId == slot.id }
            SurfSlotWithBookings(slot = slot, bookings = slotBookings)
        }
    }

    val userProfile: Flow<UserProfile?> = surfDao.getUserProfile()

    suspend fun initializeDefaultDataIfEmpty() {
        val currentSlots = surfDao.getSlotListDirect()
        val requestedTimes = setOf("06:30", "07:30", "11:30", "14:30")
        val currentTimes = currentSlots.map { it.time }.toSet()

        if (currentSlots.isEmpty() || currentTimes != requestedTimes) {
            setupRequestedSchedule()
        }
    }

    private suspend fun setupRequestedSchedule() {
        surfDao.deleteAllSlots()

        val initialSlots = listOf(
            SurfClassSlot(
                id = 1,
                time = "06:30",
                period = "Primeira Caída",
                instructor = "Instrutor Lucas",
                maxSpots = 4,
                isWeatherCancelled = false,
                orderIndex = 1
            ),
            SurfClassSlot(
                id = 2,
                time = "07:30",
                period = "Manhã",
                instructor = "Instrutora Maya",
                maxSpots = 4,
                isWeatherCancelled = false,
                orderIndex = 2
            ),
            SurfClassSlot(
                id = 3,
                time = "11:30",
                period = "Meio-Dia",
                instructor = "Instrutor Pedro",
                maxSpots = 4,
                isWeatherCancelled = false,
                orderIndex = 3
            ),
            SurfClassSlot(
                id = 4,
                time = "14:30",
                period = "Tarde",
                instructor = "Instrutor Gabriel",
                maxSpots = 4,
                isWeatherCancelled = false,
                orderIndex = 4
            )
        )
        surfDao.insertSlots(initialSlots)

        // Initial realistic attendance to showcase 4-spot limit on the requested slots:
        // 06:30: 1 spot filled, 3 free
        surfDao.deleteAllBookingsForSlot(1)
        surfDao.insertBooking(Booking(slotId = 1, studentName = "Felipe M."))

        // 07:30: 2 spots filled, 2 free
        surfDao.deleteAllBookingsForSlot(2)
        surfDao.insertBooking(Booking(slotId = 2, studentName = "Marina S."))
        surfDao.insertBooking(Booking(slotId = 2, studentName = "Lucas P."))

        // 11:30: 4 spots filled (Full / Lotada showcase)
        surfDao.deleteAllBookingsForSlot(3)
        surfDao.insertBooking(Booking(slotId = 3, studentName = "Thiago M."))
        surfDao.insertBooking(Booking(slotId = 3, studentName = "Carolina R."))
        surfDao.insertBooking(Booking(slotId = 3, studentName = "Gabriel F."))
        surfDao.insertBooking(Booking(slotId = 3, studentName = "Beatriz L."))

        // 14:30: 0 spots filled (4/4 free for immediate bookings)
        surfDao.deleteAllBookingsForSlot(4)

        // Initialize user profile if null
        val existingProfile = surfDao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            surfDao.saveUserProfile(
                UserProfile(
                    id = 1,
                    userName = "",
                    isAdminMode = false
                )
            )
        }
    }

    suspend fun bookSlot(slotId: Long, studentName: String): Result<String> {
        val trimmed = studentName.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Nome não pode ficar em branco"))
        }

        val slot = surfDao.getSlotById(slotId)
            ?: return Result.failure(IllegalStateException("Horário não encontrado"))

        if (slot.isWeatherCancelled) {
            return Result.failure(IllegalStateException("Este horário foi cancelado por tempo ruim"))
        }

        val currentCount = surfDao.getBookingCountForSlot(slotId)
        if (currentCount >= slot.maxSpots) {
            return Result.failure(IllegalStateException("Turma cheia (limite de ${slot.maxSpots} alunos atingido)"))
        }

        // Check if student already booked
        val existing = surfDao.getBookingsForSlot(slotId).firstOrNull()
        if (existing?.any { it.studentName.trim().equals(trimmed, ignoreCase = true) } == true) {
            return Result.failure(IllegalStateException("Você já confirmou presença neste horário!"))
        }

        surfDao.insertBooking(
            Booking(
                slotId = slotId,
                studentName = trimmed
            )
        )
        return Result.success("Presença confirmada para às ${slot.time}! Prancha reservada para $trimmed.")
    }

    suspend fun cancelBooking(slotId: Long, studentName: String): Result<String> {
        val trimmed = studentName.trim()
        val slot = surfDao.getSlotById(slotId)
        surfDao.deleteBooking(slotId, trimmed)
        val timeInfo = slot?.time?.let { " das $it" } ?: ""
        return Result.success("Presença cancelada na aula$timeInfo. Vaga liberada com sucesso.")
    }

    suspend fun setWeatherCancellation(slotId: Long, cancelled: Boolean, reason: String = "") {
        val finalReason = if (reason.isBlank()) {
            "Condições marítimas desfavoráveis (vento forte e mar revolto)"
        } else {
            reason
        }
        surfDao.updateWeatherStatus(slotId, cancelled, finalReason)
    }

    suspend fun setClosedClassStatus(slotId: Long, isClosed: Boolean, studentName: String) {
        surfDao.updateClosedClassStatus(slotId, isClosed, studentName.trim())
    }

    suspend fun setUserName(name: String) {
        val current = surfDao.getUserProfile().firstOrNull()
        if (current == null) {
            surfDao.saveUserProfile(UserProfile(id = 1, userName = name.trim(), isAdminMode = false))
        } else {
            surfDao.updateUserName(name.trim())
        }
    }

    suspend fun setAdminMode(isAdmin: Boolean) {
        val current = surfDao.getUserProfile().firstOrNull()
        if (current == null) {
            surfDao.saveUserProfile(UserProfile(id = 1, userName = "", isAdminMode = isAdmin))
        } else {
            surfDao.updateAdminMode(isAdmin)
        }
    }

    suspend fun clearSlotBookings(slotId: Long) {
        surfDao.deleteAllBookingsForSlot(slotId)
    }

    suspend fun removeBooking(bookingId: Long) {
        surfDao.deleteBookingById(bookingId)
    }

    suspend fun addStudentManually(slotId: Long, name: String): Result<Unit> {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return Result.failure(IllegalArgumentException("Nome inválido"))

        val slot = surfDao.getSlotById(slotId) ?: return Result.failure(IllegalStateException("Horário não encontrado"))
        val currentCount = surfDao.getBookingCountForSlot(slotId)
        if (currentCount >= slot.maxSpots) {
            return Result.failure(IllegalStateException("Limite rígido de ${slot.maxSpots} pessoas atingido"))
        }

        surfDao.insertBooking(Booking(slotId = slotId, studentName = trimmed))
        return Result.success(Unit)
    }

    suspend fun createNewSlot(time: String, period: String, instructor: String) {
        val maxOrder = surfDao.getSlotCount() + 1
        surfDao.insertSlot(
            SurfClassSlot(
                time = time,
                period = period,
                instructor = instructor,
                maxSpots = 4,
                orderIndex = maxOrder
            )
        )
    }

    suspend fun resetDaySchedule() {
        setupRequestedSchedule()
    }

    suspend fun replaceSchedule(text: String) {
        val lines = text.trim().lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.size < 2) return

        val date = lines[1]
        val instructorLine = lines.find { it.contains("📍") } ?: "📍Instrutor"
        val instructor = instructorLine.substringAfter("📍").trim()

        val newSlots = mutableListOf<SurfClassSlot>()
        var orderIndex = 1

        lines.forEach { line ->
            if (line.contains("🏄")) {
                // Extracts "6:30" from "🏄🏼♀️6:30 as 7:30"
                val timePart = line.substringAfter("🏄").trim().substringBefore(" ").trim()
                
                newSlots.add(SurfClassSlot(
                    time = timePart,
                    instructor = instructor,
                    date = date,
                    orderIndex = orderIndex++
                ))
            }
        }

        surfDao.deleteAllBookings()
        surfDao.deleteAllSlots()
        surfDao.insertSlots(newSlots)
    }
}
