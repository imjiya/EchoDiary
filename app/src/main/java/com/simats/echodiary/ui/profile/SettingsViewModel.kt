package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "Alex Morgan",
    val appearanceSummary: String = "Light · calm blue",
    val storageSummary: String = "5 entries · 1 saved memory"
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.userFullName.collectLatest { name ->
                _uiState.update { it.copy(userName = name) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { entries ->
                _uiState.update { it.copy(storageSummary = "${entries.size} entries · 1 saved memory") }
            }
        }
    }
}
