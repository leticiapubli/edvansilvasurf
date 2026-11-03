package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurfSlotWithBookings
import com.example.ui.theme.ClosedClassPurple
import com.example.ui.theme.ClosedClassPurpleBg
import com.example.ui.theme.ClosedClassPurpleBorder
import com.example.ui.theme.WeatherWarningColor

@Composable
fun ManageStudentsDialog(
    slotWithBookings: SurfSlotWithBookings,
    onAddStudent: (slotId: Long, name: String) -> Unit,
    onRemoveBooking: (bookingId: Long) -> Unit,
    onSetClosedClass: (slotId: Long, isClosed: Boolean, studentName: String) -> Unit,
    onDismiss: () -> Unit
) {
    val slot = slotWithBookings.slot
    val bookings = slotWithBookings.bookings
    val totalSpots = slotWithBookings.totalSpots
    var newStudentName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Closed class state
    var isClosedChecked by remember { mutableStateOf(slot.isClosedClass) }
    var closedNameText by remember { mutableStateOf(slot.closedClassName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Gerenciar Horário • ${slot.time}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Section 1: "Aula Fechada" / Private Class Option
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isClosedChecked) ClosedClassPurpleBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (isClosedChecked) ClosedClassPurpleBorder else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isClosedChecked,
                                onCheckedChange = { isClosedChecked = it },
                                modifier = Modifier.testTag("closed_class_checkbox")
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isClosedChecked) ClosedClassPurple else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Marcar como 'Aula Fechada / Privada'",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isClosedChecked) ClosedClassPurple else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (isClosedChecked) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Isolou este horário para um aluno ou grupo exclusivo (ex: Aula Particular do João):",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = closedNameText,
                                onValueChange = { closedNameText = it },
                                label = { Text("Nome(s) do(s) aluno(s) da Aula Fechada") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("closed_class_name_input")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Public Bookings Roster
                Text(
                    text = "Alunos Inscritos (${bookings.size}/$totalSpots):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (bookings.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Nenhum aluno inscrito na grade pública.",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        bookings.forEachIndexed { index, booking ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.padding(5.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${index + 1}. ${booking.studentName}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    IconButton(
                                        onClick = { onRemoveBooking(booking.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remover aluno",
                                            tint = WeatherWarningColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add manual student if capacity allows
                if (bookings.size < totalSpots && !isClosedChecked) {
                    Text(
                        text = "Adicionar aluno manualmente:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newStudentName,
                            onValueChange = {
                                newStudentName = it
                                if (it.isNotBlank()) isError = false
                            },
                            label = { Text("Nome do aluno") },
                            singleLine = true,
                            isError = isError,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_add_student_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newStudentName.trim().isBlank()) {
                                    isError = true
                                } else {
                                    onAddStudent(slot.id, newStudentName.trim())
                                    newStudentName = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_add_student_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSetClosedClass(slot.id, isClosedChecked, closedNameText)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isClosedChecked) ClosedClassPurple else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("save_closed_class_button")
            ) {
                Text("Salvar Alterações", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_manage_students_button")
            ) {
                Text("Cancelar")
            }
        }
    )
}
