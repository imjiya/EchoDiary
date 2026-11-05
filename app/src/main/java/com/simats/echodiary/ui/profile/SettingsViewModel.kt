package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class SettingsUiState(
    val userName: String = "Alex Morgan",
    val appearanceSummary: String = "Light · calm blue",
    val storageSummary: String = "5 entries · 1 saved memory",
    val notificationsSummary: String = "Daily at 8:00 PM"
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
            combine(DiaryActivityStore.themeMode, DiaryActivityStore.themePalette) { mode, palette ->
                "$mode · ${palette.lowercase(Locale.ROOT)}"
            }.collectLatest { summary ->
                _uiState.update { it.copy(appearanceSummary = summary) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { entries ->
                _uiState.update { it.copy(storageSummary = "${entries.size} entries · 1 saved memory") }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.notificationSummary.collectLatest { summary ->
                _uiState.update { it.copy(notificationsSummary = summary) }
            }
        }
    }
}
