package com.simats.echodiary.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResetPasswordUiState(
    val email: String = "alex.morgan@example.com",
    val requestSent: Boolean = false
)

sealed class ResetPasswordNavigationEvent {
    object NavigateBack : ResetPasswordNavigationEvent()
}

class ResetPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ResetPasswordNavigationEvent>()
    val navigationEvent: SharedFlow<ResetPasswordNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onSendResetInstructionsClicked() {
        _uiState.update { it.copy(requestSent = true) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ResetPasswordNavigationEvent.NavigateBack)
        }
    }
}
