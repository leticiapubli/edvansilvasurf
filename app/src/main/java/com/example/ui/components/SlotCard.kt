package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Surfing
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurfSlotWithBookings
import com.example.ui.theme.ClosedClassPurple
import com.example.ui.theme.ClosedClassPurpleBg
import com.example.ui.theme.ClosedClassPurpleBorder
import com.example.ui.theme.CounterAlmostFullOrange
import com.example.ui.theme.CounterAlmostFullOrangeBg
import com.example.ui.theme.CounterAlmostFullOrangeBorder
import com.example.ui.theme.CounterFullRed
import com.example.ui.theme.CounterFullRedBg
import com.example.ui.theme.CounterFullRedBorder
import com.example.ui.theme.WeatherWarningBg
import com.example.ui.theme.WeatherWarningColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SlotCard(
    slotWithBookings: SurfSlotWithBookings,
    currentUserName: String,
    isAdminMode: Boolean,
    onWantToGoClicked: (SurfSlotWithBookings) -> Unit,
    onCancelAttendanceClicked: (SurfSlotWithBookings) -> Unit,
    onToggleWeatherStatusClicked: (SurfSlotWithBookings) -> Unit,
    onManageStudentsClicked: (SurfSlotWithBookings) -> Unit,
    onClearSlotClicked: (SurfSlotWithBookings) -> Unit,
    modifier: Modifier = Modifier
) {
    val slot = slotWithBookings.slot
    val bookings = slotWithBookings.bookings
    val totalSpots = slotWithBookings.totalSpots
    val bookedCount = slotWithBookings.bookedCount
    val availableSpots = slotWithBookings.availableSpots
    val isCancelled = slotWithBookings.isCancelled
    val isUserBooked = slotWithBookings.isBookedBy(currentUserName)
    val isFull = bookedCount >= totalSpots
    val isAlmostFull = bookedCount == 3
    val isClosed = slot.isClosedClass

    // Urgency feedback colors for the counter (3 = Orange, 4 = Red)
    val counterTextColor = when {
        isCancelled -> WeatherWarningColor
        isClosed -> ClosedClassPurple
        isFull -> CounterFullRed
        isAlmostFull -> CounterAlmostFullOrange
        else -> MaterialTheme.colorScheme.primary
    }

    val counterBoxBackground = when {
        isCancelled -> WeatherWarningBg.copy(alpha = 0.5f)
        isClosed -> ClosedClassPurpleBg
        isFull -> CounterFullRedBg
        isAlmostFull -> CounterAlmostFullOrangeBg
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val counterBoxBorderColor = when {
        isCancelled -> WeatherWarningColor.copy(alpha = 0.5f)
        isClosed -> ClosedClassPurpleBorder
        isFull -> CounterFullRedBorder
        isAlmostFull -> CounterAlmostFullOrangeBorder
        else -> Color.Transparent
    }

    val statusBadgeColor = when {
        isCancelled -> WeatherWarningColor
        isClosed -> ClosedClassPurple
        isUserBooked -> MaterialTheme.colorScheme.primary
        isFull -> CounterFullRed
        isAlmostFull -> CounterAlmostFullOrange
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val statusBadgeTextColor = when {
        isCancelled || isClosed || isUserBooked || isFull || isAlmostFull -> Color.White
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    val statusBadgeText = when {
        isCancelled -> "CANCELADA"
        isClosed -> "AULA FECHADA"
        isUserBooked -> "SUA VAGA"
        isFull -> "LOTADA (4/4)"
        isAlmostFull -> "QUASE LOTADA (3/4)"
        else -> "$availableSpots VAGAS"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCancelled -> WeatherWarningBg.copy(alpha = 0.5f)
                isClosed -> ClosedClassPurpleBg.copy(alpha = 0.35f)
                isUserBooked -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                isFull -> CounterFullRedBg.copy(alpha = 0.25f)
                isAlmostFull -> CounterAlmostFullOrangeBg.copy(alpha = 0.25f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isClosed || isFull || isAlmostFull) 1.5.dp else 1.dp,
            color = when {
                isCancelled -> WeatherWarningColor.copy(alpha = 0.4f)
                isClosed -> ClosedClassPurpleBorder
                isFull -> CounterFullRedBorder
                isAlmostFull -> CounterAlmostFullOrangeBorder
                isUserBooked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("slot_card_${slot.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Time, Period & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = slot.time,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = when {
                            isCancelled -> WeatherWarningColor
                            isFull -> CounterFullRed
                            isAlmostFull -> CounterAlmostFullOrange
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = slot.period,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                isFull -> CounterFullRed
                                isAlmostFull -> CounterAlmostFullOrange
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        Text(
                            text = slot.instructor,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusBadgeColor
                ) {
                    Text(
                        text = statusBadgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = statusBadgeTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-Time Capacity Counter & 4 Spots Visual Indicator with Orange/Red feedback
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = counterBoxBackground,
                border = BorderStroke(1.dp, counterBoxBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capacity_counter_container_${slot.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Vagas em tempo real:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                            if (isAlmostFull && !isCancelled) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "• Resta 1 vaga!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CounterAlmostFullOrange
                                )
                            } else if (isFull && !isCancelled) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "• Esgotado",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CounterFullRed
                                )
                            }
                        }
                        Text(
                            text = "$bookedCount/$totalSpots preenchidas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = counterTextColor,
                            modifier = Modifier.testTag("counter_text_${slot.id}")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Visual Spots (Rigid limit of 4)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 0 until totalSpots) {
                            val booking = bookings.getOrNull(i)
                            val isOccupied = booking != null
                            val isCurrentUserSpot = booking != null &&
                                booking.studentName.trim().equals(currentUserName.trim(), ignoreCase = true)

                            val spotBoxBg = when {
                                isCancelled -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                                isCurrentUserSpot -> MaterialTheme.colorScheme.primary
                                isOccupied && isFull -> CounterFullRed.copy(alpha = 0.15f)
                                isOccupied && isAlmostFull -> CounterAlmostFullOrange.copy(alpha = 0.15f)
                                isOccupied -> MaterialTheme.colorScheme.primaryContainer
                                !isOccupied && isAlmostFull -> CounterAlmostFullOrangeBg
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val spotBoxBorder = when {
                                isCurrentUserSpot -> MaterialTheme.colorScheme.primary
                                isOccupied && isFull -> CounterFullRed.copy(alpha = 0.7f)
                                isOccupied && isAlmostFull -> CounterAlmostFullOrange.copy(alpha = 0.7f)
                                isOccupied -> MaterialTheme.colorScheme.primaryContainer
                                !isOccupied && isAlmostFull -> CounterAlmostFullOrange
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = spotBoxBg,
                                border = BorderStroke(1.dp, spotBoxBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    if (isCurrentUserSpot) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Você",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    } else if (isOccupied) {
                                        Text(
                                            text = booking.studentName.take(7),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isFull -> CounterFullRed
                                                isAlmostFull -> CounterAlmostFullOrange
                                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                                            },
                                            maxLines = 1
                                        )
                                    } else {
                                        Text(
                                            text = if (isAlmostFull) "1 Vaga!" else "Livre",
                                            fontSize = 11.sp,
                                            fontWeight = if (isAlmostFull) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isAlmostFull) CounterAlmostFullOrange else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


            // Closed Class / Private Class Box
            if (isClosed) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ClosedClassPurpleBg,
                    border = BorderStroke(1.dp, ClosedClassPurpleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Aula Fechada",
                            tint = ClosedClassPurple,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Aula Fechada / Particular",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClosedClassPurple
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (slot.closedClassName.isNotBlank()) "Reservado para: ${slot.closedClassName}" else "Horário reservado exclusivamente pelo instrutor.",
                                fontSize = 12.sp,
                                color = Color.Black, // Explicit black as requested
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Weather Cancellation Warning Box (Mandatory visual alert)
            if (isCancelled) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WeatherWarningBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Aviso de tempo ruim",
                            tint = WeatherWarningColor,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cancelada por tempo ruim",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WeatherWarningColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = slot.weatherCancelReason.ifBlank {
                                    "Condições do mar desfavoráveis. Treino suspenso para segurança dos alunos."
                                },
                                fontSize = 12.sp,
                                color = WeatherWarningColor.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            when {
                isClosed -> {
                    Button(
                        onClick = { },
                        enabled = false,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = ClosedClassPurple.copy(alpha = 0.2f),
                            disabledContentColor = ClosedClassPurple
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("closed_slot_button_${slot.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aula Exclusiva / Fechada",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                isCancelled -> {
                    Button(
                        onClick = { },
                        enabled = false,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("cancelled_slot_button_${slot.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aula Cancelada por Tempo Ruim",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                isUserBooked -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Presença confirmada! Nos vemos na areia.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Prominent Cancel Button
                        OutlinedButton(
                            onClick = { onCancelAttendanceClicked(slotWithBookings) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = WeatherWarningColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("cancel_button_${slot.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = WeatherWarningColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cancelar Presença",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = WeatherWarningColor
                            )
                        }
                    }
                }

                isFull -> {
                    Button(
                        onClick = { },
                        enabled = false,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("full_slot_button_${slot.id}")
                    ) {
                        Text(
                            text = "Turma Cheia (4/4 alunos)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> {
                    // "Quero Ir" Button
                    Button(
                        onClick = { onWantToGoClicked(slotWithBookings) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("want_to_go_button_${slot.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Surfing,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Quero Ir",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Admin Controls Toolbar (when Admin Mode is enabled)
            AnimatedVisibility(visible = isAdminMode) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Text(
                        text = "Painel do Instrutor para as ${slot.time}:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Weather cancellation toggle
                        Button(
                            onClick = { onToggleWeatherStatusClicked(slotWithBookings) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCancelled) MaterialTheme.colorScheme.primary else WeatherWarningColor
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("admin_weather_toggle_${slot.id}")
                        ) {
                            Icon(
                                imageVector = if (isCancelled) Icons.Default.Replay else Icons.Default.CloudOff,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCancelled) "Reativar Aula" else "Tempo Ruim",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        // Manage students
                        OutlinedButton(
                            onClick = { onManageStudentsClicked(slotWithBookings) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("admin_manage_students_${slot.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Alunos ($bookedCount)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        // Clear slot
                        OutlinedButton(
                            onClick = { onClearSlotClicked(slotWithBookings) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(40.dp)
                                .testTag("admin_clear_slot_${slot.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Limpar vagas",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Limpar",
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
