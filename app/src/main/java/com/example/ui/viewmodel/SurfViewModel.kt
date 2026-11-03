package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.SurfSlotWithBookings
import com.example.data.repository.SurfRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SurfUiState(
    val slots: List<SurfSlotWithBookings> = emptyList(),
    val userName: String = "",
    val isAdminMode: Boolean = false,
    val isAdminLoginOpen: Boolean = false,
    val pendingBookingSlot: SurfSlotWithBookings? = null,
    val isNamePromptOpen: Boolean = false,
    val pendingCancelSlot: SurfSlotWithBookings? = null,
    val pendingWeatherSlot: SurfSlotWithBookings? = null,
    val pendingManageStudentsSlot: SurfSlotWithBookings? = null,
    val isAddSlotDialogOpen: Boolean = false,
    val isImportSlotsDialogOpen: Boolean = false,
    val userMessage: String? = null
) {
    val myBookedSlots: List<SurfSlotWithBookings>
        get() = if (userName.isBlank()) emptyList() else slots.filter { it.isBookedBy(userName) }
}

class SurfViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SurfRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = SurfRepository(db.surfDao())
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    private val _uiState = MutableStateFlow(SurfUiState())
    val uiState: StateFlow<SurfUiState> = combine(
        repository.slotsWithBookings,
        repository.userProfile,
        _uiState
    ) { slots, profile, state ->
        state.copy(
            slots = slots,
            userName = profile?.userName ?: "",
            isAdminMode = profile?.isAdminMode ?: false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SurfUiState()
    )

    fun onWantToGoClicked(slot: SurfSlotWithBookings) {
        val currentName = uiState.value.userName.trim()
        if (currentName.isBlank()) {
            // Ask for name first
            _uiState.update {
                it.copy(
                    pendingBookingSlot = slot,
                    isNamePromptOpen = true
                )
            }
        } else {
            // Instant 1-tap booking (allows multiple slots on the same day!)
            bookSlotInternal(slot.slot.id, currentName)
        }
    }

    fun submitUserNameAndBook(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return

        val targetSlot = _uiState.value.pendingBookingSlot
        viewModelScope.launch {
            repository.setUserName(trimmed)
            _uiState.update {
                it.copy(
                    isNamePromptOpen = false,
                    pendingBookingSlot = null
                )
            }
            if (targetSlot != null) {
                bookSlotInternal(targetSlot.slot.id, trimmed)
            }
        }
    }

    fun updateUserNameOnly(name: String) {
        val trimmed = name.trim()
        viewModelScope.launch {
            repository.setUserName(trimmed)
            _uiState.update { it.copy(isNamePromptOpen = false) }
            showToast("Nome do aluno atualizado para: $trimmed")
        }
    }

    fun openNameDialog() {
        _uiState.update { it.copy(isNamePromptOpen = true) }
    }

    fun dismissNameDialog() {
        _uiState.update {
            it.copy(
                isNamePromptOpen = false,
                pendingBookingSlot = null
            )
        }
    }

    private fun bookSlotInternal(slotId: Long, studentName: String) {
        viewModelScope.launch {
            val result = repository.bookSlot(slotId, studentName)
            result.onSuccess { message ->
                showToast(message)
            }.onFailure { err ->
                showToast(err.message ?: "Não foi possível confirmar a vaga.")
            }
        }
    }

    fun onCancelAttendanceClicked(slot: SurfSlotWithBookings) {
        _uiState.update { it.copy(pendingCancelSlot = slot) }
    }

    fun confirmCancellation() {
        val slot = _uiState.value.pendingCancelSlot ?: return
        val currentName = uiState.value.userName.trim()
        viewModelScope.launch {
            val result = repository.cancelBooking(slot.slot.id, currentName)
            _uiState.update { it.copy(pendingCancelSlot = null) }
            result.onSuccess { msg ->
                showToast(msg)
            }
        }
    }

    fun dismissCancelDialog() {
        _uiState.update { it.copy(pendingCancelSlot = null) }
    }

    fun openAdminLoginDialog() {
        if (uiState.value.isAdminMode) {
            // Already logged in as admin -> toggle off (logout)
            viewModelScope.launch {
                repository.setAdminMode(false)
                showToast("Sessão de Admin encerrada")
            }
        } else {
            _uiState.update { it.copy(isAdminLoginOpen = true) }
        }
    }

    fun dismissAdminLoginDialog() {
        _uiState.update { it.copy(isAdminLoginOpen = false) }
    }

    fun loginAsAdmin() {
        viewModelScope.launch {
            repository.setAdminMode(true)
            _uiState.update { it.copy(isAdminLoginOpen = false) }
            showToast("Login de Admin efetuado com sucesso!")
        }
    }

    fun openWeatherEditDialog(slot: SurfSlotWithBookings) {
        _uiState.update { it.copy(pendingWeatherSlot = slot) }
    }

    fun dismissWeatherEditDialog() {
        _uiState.update { it.copy(pendingWeatherSlot = null) }
    }

    fun setWeatherCancellation(slotId: Long, cancelled: Boolean, reason: String) {
        viewModelScope.launch {
            repository.setWeatherCancellation(slotId, cancelled, reason)
            _uiState.update { it.copy(pendingWeatherSlot = null) }
            val msg = if (cancelled) "Aula marcada como cancelada por tempo ruim" else "Horário reativado com sucesso!"
            showToast(msg)
        }
    }

    fun setClosedClassStatus(slotId: Long, isClosed: Boolean, studentName: String) {
        viewModelScope.launch {
            repository.setClosedClassStatus(slotId, isClosed, studentName)
            val msg = if (isClosed) "Aula marcada como 'Aula Fechada' para $studentName" else "Aula reaberta para reservas públicas"
            showToast(msg)
        }
    }

    fun openManageStudentsDialog(slot: SurfSlotWithBookings) {
        _uiState.update { it.copy(pendingManageStudentsSlot = slot) }
    }

    fun dismissManageStudentsDialog() {
        _uiState.update { it.copy(pendingManageStudentsSlot = null) }
    }

    fun adminAddStudent(slotId: Long, studentName: String) {
        viewModelScope.launch {
            val res = repository.addStudentManually(slotId, studentName)
            res.onSuccess {
                showToast("Aluno $studentName adicionado à turma")
            }.onFailure {
                showToast(it.message ?: "Erro ao adicionar aluno")
            }
        }
    }

    fun adminRemoveStudent(bookingId: Long) {
        viewModelScope.launch {
            repository.removeBooking(bookingId)
            showToast("Vaga liberada pelo instrutor")
        }
    }

    fun adminClearSlot(slotId: Long) {
        viewModelScope.launch {
            repository.clearSlotBookings(slotId)
            showToast("Todas as vagas deste horário foram liberadas")
        }
    }

    fun openAddSlotDialog() {
        _uiState.update { it.copy(isAddSlotDialogOpen = true) }
    }

    fun dismissAddSlotDialog() {
        _uiState.update { it.copy(isAddSlotDialogOpen = false) }
    }

    fun addNewSlot(time: String, period: String, instructor: String) {
        viewModelScope.launch {
            repository.createNewSlot(time.trim(), period.trim(), instructor.trim())
            _uiState.update { it.copy(isAddSlotDialogOpen = false) }
            showToast("Novo horário das $time adicionado!")
        }
    }

    fun resetSchedule() {
        viewModelScope.launch {
            repository.resetDaySchedule()
            showToast("Horários do dia restaurados para o padrão")
        }
    }

    fun openImportSlotsDialog() {
        _uiState.update { it.copy(isImportSlotsDialogOpen = true) }
    }

    fun dismissImportSlotsDialog() {
        _uiState.update { it.copy(isImportSlotsDialogOpen = false) }
    }

    fun importSlots(text: String) {
        viewModelScope.launch {
            repository.replaceSchedule(text)
            _uiState.update { it.copy(isImportSlotsDialogOpen = false) }
            showToast("Horários importados com sucesso!")
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun showToast(message: String) {
        _uiState.update { it.copy(userMessage = message) }
    }
}
