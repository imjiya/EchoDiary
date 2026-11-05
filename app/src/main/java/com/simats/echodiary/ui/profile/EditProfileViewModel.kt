package com.simats.echodiary.ui.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val fullName: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val userInitials: String = "AM",
    val isSaving: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val feedbackMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed class EditProfileEvent {
    object SavedSuccessfully : EditProfileEvent()
}

class EditProfileViewModel(
    private val authRepository: AuthRepository = EchoDiaryApp.getAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditProfileEvent>()
    val events: SharedFlow<EditProfileEvent> = _events.asSharedFlow()

    init {
        val currentName = DiaryActivityStore.userFullName.value
        val currentEmail = DiaryActivityStore.userEmail.value
        val initials = DiaryActivityStore.computeInitials(currentName)
        _uiState.update {
            it.copy(
                fullName = currentName,
                email = currentEmail,
                userInitials = initials
            )
        }
    }

    fun onFullNameChanged(name: String) {
        val initials = DiaryActivityStore.computeInitials(name)
        _uiState.update {
            it.copy(
                fullName = name,
                userInitials = initials,
                nameError = if (name.trim().isBlank()) "Name cannot be empty" else null,
                feedbackMessage = null
            )
        }
    }

    fun onEmailChanged(email: String) {
        val isInvalid = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        _uiState.update {
            it.copy(
                email = email,
                emailError = if (isInvalid) "Please enter a valid email address" else null,
                feedbackMessage = null
            )
        }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val name = _uiState.value.fullName.trim()
        val email = _uiState.value.email.trim()

        var hasError = false
        var nameErr: String? = null
        var emailErr: String? = null

        if (name.isBlank()) {
            nameErr = "Please enter your full name"
            hasError = true
        }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Please enter a valid email address"
            hasError = true
        }

        if (hasError) {
            _uiState.update { it.copy(nameError = nameErr, emailError = emailErr) }
            return
        }

        _uiState.update { it.copy(isSaving = true, nameError = null, emailError = null, feedbackMessage = null) }

        // Immediately update reactive app state and local session storage
        DiaryActivityStore.setProfile(name, email)
        EchoDiaryApp.getSessionManager().updateUserDetails(name, email)

        viewModelScope.launch {
            val result = authRepository.updateProfile(name, email)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSuccess = true,
                            feedbackMessage = "Profile updated successfully."
                        )
                    }
                    _events.emit(EditProfileEvent.SavedSuccessfully)
                    onSuccess()
                },
                onFailure = { err ->
                    // Even if backend fails (e.g. offline), local changes are saved
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSuccess = true,
                            feedbackMessage = err.message ?: "Saved locally."
                        )
                    }
                    _events.emit(EditProfileEvent.SavedSuccessfully)
                    onSuccess()
                }
            )
        }
    }
}
