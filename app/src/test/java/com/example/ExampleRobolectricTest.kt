package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Booking
import com.example.data.model.SurfClassSlot
import com.example.data.model.SurfSlotWithBookings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Onda Surf", appName)
  }

  @Test
  fun `slot capacity limit is strictly 4 spots`() {
    val slot = SurfClassSlot(
      id = 1,
      time = "06:30",
      maxSpots = 4
    )
    val bookings = listOf(
      Booking(slotId = 1, studentName = "Felipe"),
      Booking(slotId = 1, studentName = "Lucas")
    )
    val slotWithBookings = SurfSlotWithBookings(slot = slot, bookings = bookings)

    assertEquals(4, slotWithBookings.totalSpots)
    assertEquals(2, slotWithBookings.bookedCount)
    assertEquals(2, slotWithBookings.availableSpots)
    assertFalse(slotWithBookings.isFull)
    assertTrue(slotWithBookings.isBookedBy("Felipe"))
    assertFalse(slotWithBookings.isBookedBy("Marina"))
  }

  @Test
  fun `slot is full when 4 bookings are present`() {
    val slot = SurfClassSlot(
      id = 3,
      time = "11:30",
      maxSpots = 4
    )
    val bookings = listOf(
      Booking(slotId = 3, studentName = "Aluno 1"),
      Booking(slotId = 3, studentName = "Aluno 2"),
      Booking(slotId = 3, studentName = "Aluno 3"),
      Booking(slotId = 3, studentName = "Aluno 4")
    )
    val slotWithBookings = SurfSlotWithBookings(slot = slot, bookings = bookings)

    assertEquals(4, slotWithBookings.bookedCount)
    assertEquals(0, slotWithBookings.availableSpots)
    assertTrue(slotWithBookings.isFull)
  }

  @Test
  fun `slot has almost full state when 3 bookings are present`() {
    val slot = SurfClassSlot(
      id = 2,
      time = "07:30",
      maxSpots = 4
    )
    val bookings = listOf(
      Booking(slotId = 2, studentName = "Aluno 1"),
      Booking(slotId = 2, studentName = "Aluno 2"),
      Booking(slotId = 2, studentName = "Aluno 3")
    )
    val slotWithBookings = SurfSlotWithBookings(slot = slot, bookings = bookings)

    assertEquals(3, slotWithBookings.bookedCount)
    assertEquals(1, slotWithBookings.availableSpots)
    assertFalse(slotWithBookings.isFull)
  }

  @Test
  fun `student can enroll in multiple slots on the same day`() {
    val slotMorning = SurfClassSlot(id = 2, time = "07:30", maxSpots = 4)
    val slotAfternoon = SurfClassSlot(id = 4, time = "14:30", maxSpots = 4)

    val studentName = "Marina Silva"
    val booking1 = Booking(id = 10, slotId = 2, studentName = studentName)
    val booking2 = Booking(id = 11, slotId = 4, studentName = studentName)

    val morningWithBookings = SurfSlotWithBookings(slot = slotMorning, bookings = listOf(booking1))
    val afternoonWithBookings = SurfSlotWithBookings(slot = slotAfternoon, bookings = listOf(booking2))

    assertTrue(morningWithBookings.isBookedBy(studentName))
    assertTrue(afternoonWithBookings.isBookedBy(studentName))

    // Cancelling morning slot does not affect afternoon slot
    val updatedMorning = morningWithBookings.copy(bookings = emptyList())
    assertFalse(updatedMorning.isBookedBy(studentName))
    assertTrue(afternoonWithBookings.isBookedBy(studentName))
  }
}
