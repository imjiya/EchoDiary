package com.simats.echodiary.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.model.UserResponse
import com.simats.echodiary.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val user: UserResponse? = null,
    val token: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = EchoDiaryApp.getAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onLogin(email: String, password: String) {
        if (_uiState.value.isLoading) return

        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Email address is required.") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Password is required.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

        viewModelScope.launch {
            val result = authRepository.login(cleanEmail, password)
            result.fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null,
                            user = response.user,
                            token = response.token
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = error.message ?: "Login failed. Please check your credentials."
                        )
                    }
                }
            )
        }
    }

    fun onRegister(name: String, email: String, password: String) {
        if (_uiState.value.isLoading) return

        val cleanName = name.trim()
        val cleanEmail = email.trim()

        if (cleanName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Full name is required.") }
            return
        }

        if (cleanEmail.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Email address is required.") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        if (password.length < 8) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters long.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

        viewModelScope.launch {
            val result = authRepository.register(cleanName, cleanEmail, password)
            result.fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null,
                            user = response.user,
                            token = response.token
                        )
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}

