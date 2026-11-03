package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddSlotDialog(
    onAddSlot: (time: String, period: String, instructor: String) -> Unit,
    onDismiss: () -> Unit
) {
    var timeText by remember { mutableStateOf("11:30") }
    var periodText by remember { mutableStateOf("Manhã") }
    var instructorText by remember { mutableStateOf("Instrutor Gabriel") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Criar Novo Horário de Aula",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Adicione um novo horário para hoje com o limite padrão de 4 alunos.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = timeText,
                    onValueChange = {
                        timeText = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text("Horário (ex: 11:30)") },
                    singleLine = true,
                    isError = isError,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_slot_time_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = periodText,
                    onValueChange = { periodText = it },
                    label = { Text("Período (ex: Manhã, Tarde)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = instructorText,
                    onValueChange = { instructorText = it },
                    label = { Text("Instrutor Responsável") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (timeText.trim().isBlank()) {
                        isError = true
                    } else {
                        onAddSlot(timeText.trim(), periodText.trim(), instructorText.trim())
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_add_slot_button")
            ) {
                Text("Adicionar Horário", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_add_slot_button")
            ) {
                Text("Cancelar")
            }
        }
    )
}
