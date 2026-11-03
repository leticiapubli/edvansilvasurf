package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SurfSlotWithBookings
import com.example.ui.theme.WeatherWarningColor

@Composable
fun CancelConfirmDialog(
    slotWithBookings: SurfSlotWithBookings,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val slot = slotWithBookings.slot

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = null,
                tint = WeatherWarningColor,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Liberar vaga das ${slot.time}?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Você tem certeza de que deseja cancelar sua presença? A vaga ficará livre instantaneamente para outro surfista.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WeatherWarningColor
                ),
                modifier = Modifier.testTag("confirm_cancel_button")
            ) {
                Text(
                    text = "Sim, Cancelar Presença",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_cancel_button")
            ) {
                Text("Voltar")
            }
        }
    )
}
