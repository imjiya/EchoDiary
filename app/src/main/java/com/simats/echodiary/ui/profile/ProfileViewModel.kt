package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val userName: String = "Alex Morgan",
    val userEmail: String = "alex.morgan@example.com",
    val userInitials: String = "AM",
    val diaryEntriesCount: Int = 5,
    val savedMemoriesCount: Int = 1,
    val selectedTab: Int = 3
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
