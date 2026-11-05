package com.simats.echodiary.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val diaryRemindersEnabled: Boolean = true,
    val weeklyMemoryRemindersEnabled: Boolean = true,
    val summaryUpdatesEnabled: Boolean = false,
    val dailyReminderTime: String = "8:00 PM",
    val weeklyReminderTime: String = "Sunday · 8:00 PM",
    val statusMessage: String? = null,
    val isSavedSuccess: Boolean = false
)

class NotificationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        NotificationsUiState(
            diaryRemindersEnabled = DiaryActivityStore.diaryRemindersEnabled.value,
            weeklyMemoryRemindersEnabled = DiaryActivityStore.weeklyMemoryRemindersEnabled.value,
            summaryUpdatesEnabled = DiaryActivityStore.summaryUpdatesEnabled.value,
            dailyReminderTime = DiaryActivityStore.dailyReminderTime.value,
            weeklyReminderTime = DiaryActivityStore.weeklyReminderTime.value
        )
    )
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun onDiaryRemindersToggled(enabled: Boolean) {
        _uiState.update { it.copy(diaryRemindersEnabled = enabled, statusMessage = null, isSavedSuccess = false) }
    }

    fun onWeeklyMemoryRemindersToggled(enabled: Boolean) {
        _uiState.update { it.copy(weeklyMemoryRemindersEnabled = enabled, statusMessage = null, isSavedSuccess = false) }
    }

    fun onSummaryUpdatesToggled(enabled: Boolean) {
        _uiState.update { it.copy(summaryUpdatesEnabled = enabled, statusMessage = null, isSavedSuccess = false) }
    }

    fun onDailyReminderTimeChanged(time: String) {
        _uiState.update { it.copy(dailyReminderTime = time, statusMessage = null, isSavedSuccess = false) }
    }

    fun onWeeklyReminderTimeChanged(time: String) {
        _uiState.update { it.copy(weeklyReminderTime = time, statusMessage = null, isSavedSuccess = false) }
    }

    fun savePreferences() {
        val current = _uiState.value
        DiaryActivityStore.updateNotificationSettings(
            diary = current.diaryRemindersEnabled,
            weekly = current.weeklyMemoryRemindersEnabled,
            summary = current.summaryUpdatesEnabled,
            dailyTime = current.dailyReminderTime,
            weeklyTime = current.weeklyReminderTime
        )
        _uiState.update {
            it.copy(
                isSavedSuccess = true,
                statusMessage = "Notification preferences saved successfully!"
            )
        }
    }

    fun sendTestNotification(context: Context) {
        val posted = NotificationHelper.showReminderNotification(
            context = context,
            title = "EchoDiary · Time to reflect ✨",
            message = "A gentle reminder to record your moments for today in your diary."
        )
        _uiState.update {
            it.copy(
                statusMessage = if (posted) {
                    "Test reminder notification dispatched!"
                } else {
                    "Reminder triggered (enable notification permissions in system settings if not visible)."
                },
                isSavedSuccess = true
            )
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null, isSavedSuccess = false) }
    }
}
