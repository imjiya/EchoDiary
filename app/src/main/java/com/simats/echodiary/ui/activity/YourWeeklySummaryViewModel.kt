package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
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
    val entries: List<WeeklySummaryEntry> = emptyList(),
    val moodSummary: String = "1 explicitly recorded mood: Calm, on Oct 7. The other 4 entries have no mood specified."
)

sealed class YourWeeklySummaryNavigationEvent {
    object NavigateBack : YourWeeklySummaryNavigationEvent()
    object NavigateToAllActivities : YourWeeklySummaryNavigationEvent()
    object NavigateToCreatingMemory : YourWeeklySummaryNavigationEvent()
    object NavigateToAnalysisResults : YourWeeklySummaryNavigationEvent()
}

class YourWeeklySummaryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(YourWeeklySummaryUiState())
    val uiState: StateFlow<YourWeeklySummaryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<YourWeeklySummaryNavigationEvent>()
    val navigationEvent: SharedFlow<YourWeeklySummaryNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                val distinctCats = activities.map { it.category }.distinct()
                val summaryEntries = activities.take(5).map {
                    WeeklySummaryEntry(
                        title = it.title,
                        categoryDetails = "${it.category} · ${it.date}",
                        icon = it.icon
                    )
                }
                _uiState.update {
                    it.copy(
                        diaryEntriesCount = activities.size,
                        categoriesCount = distinctCats.size,
                        entries = summaryEntries,
                        moodSummary = DiaryActivityStore.synthesizeMoodSummary(activities)
                    )
                }
            }
        }
    }

    fun onGenerateWeeklyMemoryClicked() {
        // Automatically create memory summary of a week
        DiaryActivityStore.generateWeeklySummary(viewModelScope)
        viewModelScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateToCreatingMemory)
        }
    }

    fun onViewAnalysisClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateToAnalysisResults)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateBack)
        }
    }
}
