package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
        viewModelScope.launch {
            DiaryActivityStore.userFullName.collectLatest { name ->
                _uiState.update { it.copy(userName = name) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.userEmail.collectLatest { email ->
                _uiState.update { it.copy(userEmail = email) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.userInitials.collectLatest { initials ->
                _uiState.update { it.copy(userInitials = initials) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                _uiState.update { it.copy(diaryEntriesCount = activities.size) }
            }
        }
        loadUserProfile()
    }

    fun loadUserProfile() {
        val session = EchoDiaryApp.getSessionManager()
        val localName = session.getUserName()
        val localEmail = session.getUserEmail()

        if (!localName.isNullOrBlank()) {
            DiaryActivityStore.setProfile(localName, localEmail ?: "")
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.getProfile()
            result.fold(
                onSuccess = { resp ->
                    val prof = resp.profile
                    if (prof != null) {
                        DiaryActivityStore.setProfile(prof.fullName, prof.email)
                        _uiState.update {
                            it.copy(
                                userName = prof.fullName,
                                userEmail = prof.email,
                                userInitials = prof.initials ?: DiaryActivityStore.computeInitials(prof.fullName),
                                diaryEntriesCount = if (prof.diaryEntriesCount > 0) prof.diaryEntriesCount else DiaryActivityStore.activities.value.size,
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
}
