package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeeklySummaryEntry(
    val title: String,
    val categoryDetails: String,
    val icon: String
)

data class YourWeeklySummaryUiState(
    val dateRange: String = "OCT 5–11, 2026",
    val diaryEntriesCount: Int = 5,
    val categoriesCount: Int = 5,
    val entries: List<WeeklySummaryEntry> = listOf(
        WeeklySummaryEntry("Lectures and project", "College · Oct 5", "🎓"),
        WeeklySummaryEntry("Lunch and family time", "Friends · Oct 6 / Family · Oct 10", "👥"),
        WeeklySummaryEntry("A walk and presentation prep", "Health · Oct 7 / Work · Oct 8", "❤️")
    ),
    val moodSummary: String = "1 explicitly recorded mood: Calm, on Oct 7. The other 4 entries have no mood specified."
)

sealed class YourWeeklySummaryNavigationEvent {
    object NavigateBack : YourWeeklySummaryNavigationEvent()
    object NavigateToAllActivities : YourWeeklySummaryNavigationEvent()
    object NavigateToCreatingMemory : YourWeeklySummaryNavigationEvent()
}

class YourWeeklySummaryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(YourWeeklySummaryUiState())
    val uiState: StateFlow<YourWeeklySummaryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<YourWeeklySummaryNavigationEvent>()
    val navigationEvent: SharedFlow<YourWeeklySummaryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onGenerateWeeklyMemoryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateToCreatingMemory)
        }
    }

    fun onViewAnalysisClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateToAllActivities)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateBack)
        }
    }
}
