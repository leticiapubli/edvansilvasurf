package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Surfing
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurfSlotWithBookings

@Composable
fun NameInputDialog(
    initialName: String,
    targetSlot: SurfSlotWithBookings?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameText by remember { mutableStateOf(initialName) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Surfing,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = if (targetSlot != null) "Confirmar Presença • ${targetSlot.slot.time}" else "Identificação do Aluno",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Digite apenas seu primeiro nome ou apelido. Usamos apenas para o instrutor separar sua prancha e lycra na areia.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text("Seu primeiro nome (ex: Marina)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Por favor, informe seu nome para a lista") }
                    } else null,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameText.trim().isBlank()) {
                        isError = true
                    } else {
                        onConfirm(nameText.trim())
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_name_button")
            ) {
                Text(
                    text = if (targetSlot != null) "Confirmar Minha Vaga" else "Salvar Nome",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_name_button")
            ) {
                Text("Cancelar")
            }
        }
    )
}
