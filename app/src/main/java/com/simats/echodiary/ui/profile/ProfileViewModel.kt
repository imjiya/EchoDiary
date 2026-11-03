package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Alex Morgan",
    val userEmail: String = "alex.morgan@example.com",
    val userInitials: String = "AM",
    val diaryEntriesCount: Int = 0,
    val savedMemoriesCount: Int = 0,
    val selectedTab: Int = 3,
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository = EchoDiaryApp.getAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val session = EchoDiaryApp.getSessionManager()
        val localName = session.getUserName()
        val localEmail = session.getUserEmail()

        if (!localName.isNullOrBlank()) {
            val initials = computeInitials(localName)
            _uiState.update {
                it.copy(
                    userName = localName,
                    userEmail = localEmail ?: it.userEmail,
                    userInitials = initials
                )
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.getProfile()
            result.fold(
                onSuccess = { resp ->
                    val prof = resp.profile
                    if (prof != null) {
                        _uiState.update {
                            it.copy(
                                userName = prof.fullName,
                                userEmail = prof.email,
                                userInitials = prof.initials ?: computeInitials(prof.fullName),
                                diaryEntriesCount = prof.diaryEntriesCount,
                                savedMemoriesCount = prof.savedMemoriesCount,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false) }
                }
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
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
