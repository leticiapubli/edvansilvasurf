package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.example.ui.theme.WeatherWarningBg
import com.example.ui.theme.WeatherWarningColor

@Composable
fun WeatherCancelDialog(
    slotWithBookings: SurfSlotWithBookings,
    onSaveStatus: (cancelled: Boolean, reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    val slot = slotWithBookings.slot
    var isCancelled by remember { mutableStateOf(!slot.isWeatherCancelled) }
    var reasonText by remember {
        mutableStateOf(
            if (slot.weatherCancelReason.isNotBlank()) slot.weatherCancelReason
            else "Condições marítimas desfavoráveis (vento forte / correnteza perigosa)"
        )
    }

    val presetReasons = listOf(
        "Vento forte e ressaca no mar",
        "Chuva intensa e risco de raios",
        "Correnteza perigosa / mar revolto",
        "Mar sem ondas / flat total"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = if (isCancelled) WeatherWarningColor else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Status da Aula das ${slot.time}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Altere o status deste horário para todos os alunos visualizarem em tempo real.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Cancel for bad weather
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCancelled) WeatherWarningBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCancelled = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isCancelled,
                            onClick = { isCancelled = true }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "⚠️ Cancelada por tempo ruim",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCancelled) WeatherWarningColor else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Exibe alerta vermelho e bloqueia reservas",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Option 2: Active / Normal class
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isCancelled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCancelled = false }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isCancelled,
                            onClick = { isCancelled = false }
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "✓ Horário Confirmado e Aberto",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Aula liberada normalmente para os alunos",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                if (isCancelled) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Motivo do cancelamento (visível na tela):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        presetReasons.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { reasonText = preset }
                            ) {
                                Text(
                                    text = "• $preset",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveStatus(isCancelled, reasonText)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCancelled) WeatherWarningColor else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("save_weather_status_button")
            ) {
                Text(
                    text = if (isCancelled) "Salvar como Cancelada" else "Confirmar Reativação",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_weather_status_button")
            ) {
                Text("Cancelar")
            }
        }
    )
}
