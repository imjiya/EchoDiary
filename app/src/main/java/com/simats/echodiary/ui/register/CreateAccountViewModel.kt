package com.simats.echodiary.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateAccountUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed class CreateAccountNavigationEvent {
    object NavigateBack : CreateAccountNavigationEvent()
    object NavigateToLogin : CreateAccountNavigationEvent()
    object NavigateToHome : CreateAccountNavigationEvent()
}

class CreateAccountViewModel(
    private val authRepository: AuthRepository = EchoDiaryApp.getAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CreateAccountNavigationEvent>()
    val navigationEvent: SharedFlow<CreateAccountNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(CreateAccountNavigationEvent.NavigateBack)
        }
    }

    fun onLoginLinkClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(CreateAccountNavigationEvent.NavigateToLogin)
        }
    }

    fun onCreateAccountClicked() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        val fullName = currentState.fullName.trim()
        val email = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        if (fullName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter your full name.") }
            return
        }

        if (email.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email address.") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        if (password.length < 8) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters long.") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match. Please try again.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.register(fullName, email, password)
            result.fold(
                onSuccess = { response ->
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, errorMessage = null) }
                    if (!response.token.isNullOrBlank()) {
                        _navigationEvent.emit(CreateAccountNavigationEvent.NavigateToHome)
                    } else {
                        _navigationEvent.emit(CreateAccountNavigationEvent.NavigateToLogin)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = error.message ?: "Account creation failed."
                        )
                    }
                }
            )
        }
    }
}
