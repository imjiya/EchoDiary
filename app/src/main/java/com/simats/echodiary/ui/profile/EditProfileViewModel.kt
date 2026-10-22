package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditProfileUiState(
    val fullName: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val userInitials: String = "AM"
)

class EditProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(name: String) {
        val initials = computeInitials(name)
        _uiState.update { it.copy(fullName = name, userInitials = initials) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    private fun computeInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex())
        return when {
            parts.isEmpty() || parts[0].isEmpty() -> "AM"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts[0].first()}${parts[1].first()}".uppercase()
        }
    }
}
