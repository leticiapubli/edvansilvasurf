package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Surfing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddSlotDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.CancelConfirmDialog
import com.example.ui.components.FooterSection
import com.example.ui.components.HeaderSection
import com.example.ui.components.ImportSlotsDialog
import com.example.ui.components.ManageStudentsDialog
import com.example.ui.components.NameInputDialog
import com.example.ui.components.SlotCard
import com.example.ui.components.StudentBookingsPanel
import com.example.ui.components.WeatherCancelDialog
import com.example.ui.viewmodel.SurfViewModel

@Composable
fun SurfApp(
    viewModel: SurfViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        val msg = uiState.userMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissToast()
        }
    }

    // Handle system back gestures properly
    BackHandler(enabled = uiState.isNamePromptOpen) {
        viewModel.dismissNameDialog()
    }
    BackHandler(enabled = uiState.pendingCancelSlot != null) {
        viewModel.dismissCancelDialog()
    }
    BackHandler(enabled = uiState.pendingWeatherSlot != null) {
        viewModel.dismissWeatherEditDialog()
    }
    BackHandler(enabled = uiState.pendingManageStudentsSlot != null) {
        viewModel.dismissManageStudentsDialog()
    }
    BackHandler(enabled = uiState.isAddSlotDialogOpen) {
        viewModel.dismissAddSlotDialog()
    }
    BackHandler(enabled = uiState.isAdminMode && !uiState.isAddSlotDialogOpen && uiState.pendingWeatherSlot == null && uiState.pendingManageStudentsSlot == null && !uiState.isAdminLoginOpen) {
        viewModel.openAdminLoginDialog()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.isAdminMode) {
                FloatingActionButton(
                    onClick = { viewModel.openAddSlotDialog() },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape,
                    modifier = Modifier.testTag("admin_add_slot_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar Horário de Aula"
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Section (Hero, Date, Ocean conditions, Admin toggle, User chip)
            item(key = "header_section") {
                HeaderSection(
                    userName = uiState.userName,
                    isAdminMode = uiState.isAdminMode,
                    onEditNameClicked = { viewModel.openNameDialog() },
                    onAdminClicked = { viewModel.openAdminLoginDialog() }
                )
            }

            // Dedicated Student Panel (Shows all confirmed bookings with individual cancellation)
            if (uiState.userName.isNotBlank()) {
                item(key = "student_panel") {
                    StudentBookingsPanel(
                        userName = uiState.userName,
                        myBookedSlots = uiState.myBookedSlots,
                        onCancelSlotClicked = { viewModel.onCancelAttendanceClicked(it) },
                        onEditNameClicked = { viewModel.openNameDialog() },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Information & Rules callout
            item(key = "info_banner") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Inscrição flexível em múltiplos horários:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Você pode agendar mais de uma aula no mesmo dia se houver vagas (limite estrito de 4 surfistas por horário). Cancele individualmente quando quiser.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Feedback visual: 🟢 Disponível (0-2) • 🟠 Quase Lotado (3/4) • 🔴 Esgotado (4/4)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Admin controls banner if in admin mode
            if (uiState.isAdminMode) {
                item(key = "admin_actions_row") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.openAddSlotDialog() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Novo Horário", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetSchedule() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restaurar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.openImportSlotsDialog() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Importar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Schedule Section Header
            item(key = "schedule_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GRADE DE HORÁRIOS: ${uiState.slots.firstOrNull()?.slot?.date ?: "Hoje"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${uiState.slots.size} aulas programadas",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Empty state if no slots
            if (uiState.slots.isEmpty()) {
                item(key = "empty_slots_view") {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Surfing,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhuma aula cadastrada para hoje",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Toque abaixo para recarregar os horários padrão da escola.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.resetSchedule() },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Carregar Horários de Hoje")
                            }
                        }
                    }
                }
            } else {
                // List of all slots for today
                items(
                    items = uiState.slots,
                    key = { it.slot.id }
                ) { slotWithBookings ->
                    SlotCard(
                        slotWithBookings = slotWithBookings,
                        currentUserName = uiState.userName,
                        isAdminMode = uiState.isAdminMode,
                        onWantToGoClicked = { viewModel.onWantToGoClicked(it) },
                        onCancelAttendanceClicked = { viewModel.onCancelAttendanceClicked(it) },
                        onToggleWeatherStatusClicked = { viewModel.openWeatherEditDialog(it) },
                        onManageStudentsClicked = { viewModel.openManageStudentsDialog(it) },
                        onClearSlotClicked = { viewModel.adminClearSlot(it.slot.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Footer Section
            item(key = "footer_section") {
                FooterSection()
            }
        }

        // Dialogs
        if (uiState.isNamePromptOpen) {
            NameInputDialog(
                initialName = uiState.userName,
                targetSlot = uiState.pendingBookingSlot,
                onConfirm = { name ->
                    if (uiState.pendingBookingSlot != null) {
                        viewModel.submitUserNameAndBook(name)
                    } else {
                        viewModel.updateUserNameOnly(name)
                    }
                },
                onDismiss = { viewModel.dismissNameDialog() }
            )
        }

        uiState.pendingCancelSlot?.let { slot ->
            CancelConfirmDialog(
                slotWithBookings = slot,
                onConfirm = { viewModel.confirmCancellation() },
                onDismiss = { viewModel.dismissCancelDialog() }
            )
        }

        uiState.pendingWeatherSlot?.let { slot ->
            WeatherCancelDialog(
                slotWithBookings = slot,
                onSaveStatus = { cancelled, reason ->
                    viewModel.setWeatherCancellation(slot.slot.id, cancelled, reason)
                },
                onDismiss = { viewModel.dismissWeatherEditDialog() }
            )
        }

        uiState.pendingManageStudentsSlot?.let { slot ->
            ManageStudentsDialog(
                slotWithBookings = slot,
                onAddStudent = { slotId, name ->
                    viewModel.adminAddStudent(slotId, name)
                },
                onRemoveBooking = { bookingId ->
                    viewModel.adminRemoveStudent(bookingId)
                },
                onSetClosedClass = { slotId, isClosed, name ->
                    viewModel.setClosedClassStatus(slotId, isClosed, name)
                    viewModel.dismissManageStudentsDialog()
                },
                onDismiss = { viewModel.dismissManageStudentsDialog() }
            )
        }

        if (uiState.isAddSlotDialogOpen) {
            AddSlotDialog(
                onAddSlot = { time, period, instructor ->
                    viewModel.addNewSlot(time, period, instructor)
                },
                onDismiss = { viewModel.dismissAddSlotDialog() }
            )
        }

        if (uiState.isImportSlotsDialogOpen) {
            ImportSlotsDialog(
                onImport = { text ->
                    viewModel.importSlots(text)
                },
                onDismiss = { viewModel.dismissImportSlotsDialog() }
            )
        }

        if (uiState.isAdminLoginOpen) {
            AdminLoginDialog(
                onLoginSuccess = { viewModel.loginAsAdmin() },
                onDismiss = { viewModel.dismissAdminLoginDialog() }
            )
        }
    }
}
