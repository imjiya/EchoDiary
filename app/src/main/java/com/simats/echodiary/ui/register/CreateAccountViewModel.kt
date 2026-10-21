package com.simats.echodiary.ui.register

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

data class CreateAccountUiState(
    val fullName: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val password: String = "••••••••••",
    val confirmPassword: String = "••••••",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val errorMessage: String? = "Passwords don't match. Please try again."
)

sealed class CreateAccountNavigationEvent {
    object NavigateBack : CreateAccountNavigationEvent()
    object NavigateToLogin : CreateAccountNavigationEvent()
    object NavigateToHome : CreateAccountNavigationEvent()
}

class CreateAccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CreateAccountNavigationEvent>()
    val navigationEvent: SharedFlow<CreateAccountNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(CreateAccountNavigationEvent.NavigateBack)
        }
    }

    fun onCreateAccountClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(CreateAccountNavigationEvent.NavigateToHome)
        }
    }

    fun onLoginLinkClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(CreateAccountNavigationEvent.NavigateToLogin)
        }
    }
}
