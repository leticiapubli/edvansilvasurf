package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Booking
import com.example.data.model.SurfClassSlot
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface SurfDao {

    @Query("SELECT * FROM surf_slots ORDER BY orderIndex ASC, time ASC")
    fun getAllSlots(): Flow<List<SurfClassSlot>>

    @Query("SELECT * FROM surf_slots WHERE id = :id")
    suspend fun getSlotById(id: Long): SurfClassSlot?

    @Query("SELECT COUNT(*) FROM surf_slots")
    suspend fun getSlotCount(): Int

    @Query("SELECT * FROM surf_slots ORDER BY orderIndex ASC, time ASC")
    suspend fun getSlotListDirect(): List<SurfClassSlot>

    @Query("DELETE FROM surf_slots")
    suspend fun deleteAllSlots()

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<SurfClassSlot>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: SurfClassSlot): Long

    @Update
    suspend fun updateSlot(slot: SurfClassSlot)

    @Delete
    suspend fun deleteSlot(slot: SurfClassSlot)

    @Query("UPDATE surf_slots SET isWeatherCancelled = :cancelled, weatherCancelReason = :reason WHERE id = :slotId")
    suspend fun updateWeatherStatus(slotId: Long, cancelled: Boolean, reason: String)

    @Query("UPDATE surf_slots SET isClosedClass = :isClosed, closedClassName = :name WHERE id = :slotId")
    suspend fun updateClosedClassStatus(slotId: Long, isClosed: Boolean, name: String)

    @Query("SELECT * FROM bookings ORDER BY bookedAt ASC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE slotId = :slotId ORDER BY bookedAt ASC")
    fun getBookingsForSlot(slotId: Long): Flow<List<Booking>>

    @Query("SELECT COUNT(*) FROM bookings WHERE slotId = :slotId")
    suspend fun getBookingCountForSlot(slotId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Query("DELETE FROM bookings WHERE slotId = :slotId AND LOWER(TRIM(studentName)) = LOWER(TRIM(:studentName))")
    suspend fun deleteBooking(slotId: Long, studentName: String)

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBookingById(bookingId: Long)

    @Query("DELETE FROM bookings WHERE slotId = :slotId")
    suspend fun deleteAllBookingsForSlot(slotId: Long)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET userName = :name WHERE id = 1")
    suspend fun updateUserName(name: String)

    @Query("UPDATE user_profile SET isAdminMode = :isAdmin WHERE id = 1")
    suspend fun updateAdminMode(isAdmin: Boolean)
}
