package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NotificationsUiState(
    val diaryRemindersEnabled: Boolean = true,
    val weeklyMemoryRemindersEnabled: Boolean = true,
    val summaryUpdatesEnabled: Boolean = false,
    val dailyReminderTime: String = "8:00 PM",
    val weeklyReminderTime: String = "Sunday · 8:00 PM"
)

class NotificationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun onDiaryRemindersToggled(enabled: Boolean) {
        _uiState.update { it.copy(diaryRemindersEnabled = enabled) }
    }

    fun onWeeklyMemoryRemindersToggled(enabled: Boolean) {
        _uiState.update { it.copy(weeklyMemoryRemindersEnabled = enabled) }
    }

    fun onSummaryUpdatesToggled(enabled: Boolean) {
        _uiState.update { it.copy(summaryUpdatesEnabled = enabled) }
    }

    fun onDailyReminderTimeChanged(time: String) {
        _uiState.update { it.copy(dailyReminderTime = time) }
    }

    fun onWeeklyReminderTimeChanged(time: String) {
        _uiState.update { it.copy(weeklyReminderTime = time) }
    }
}
