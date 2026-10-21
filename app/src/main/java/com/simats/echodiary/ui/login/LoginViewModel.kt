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

data class LoginUiState(
    val email: String = "alex.morgan@example.com",
    val password: String = "••••••••••",
    val passwordVisible: Boolean = false
)

sealed class LoginNavigationEvent {
    object NavigateBack : LoginNavigationEvent()
    object NavigateToHome : LoginNavigationEvent()
    object NavigateToForgotPassword : LoginNavigationEvent()
    object NavigateToCreateAccount : LoginNavigationEvent()
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent: SharedFlow<LoginNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigateBack)
        }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
        }
    }

    fun onForgotPasswordClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigateToForgotPassword)
        }
    }

    fun onCreateAccountClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigateToCreateAccount)
        }
    }
}
