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

data class CategoryBreakdownItem(
    val name: String,
    val count: Int,
    val progress: Float = 1f
)

data class MonthlyMemoryUiState(
    val monthTitle: String = "October 2026",
    val infoText: String = "Limited sample: only 5 entries from Oct 5–10. This is not a complete month.",
    val diaryEntriesCount: Int = 5,
    val categoriesCount: Int = 5,
    val summaryTitle: String = "October, in your words",
    val summaryText: String = "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.",
    val categoryBreakdown: List<CategoryBreakdownItem> = listOf(
        CategoryBreakdownItem("College", 1),
        CategoryBreakdownItem("Friends", 1),
        CategoryBreakdownItem("Health", 1),
        CategoryBreakdownItem("Work", 1),
        CategoryBreakdownItem("Family", 1)
    ),
    val footerText: String = "Highlights: project work, lunch with friends, a Calm walk, presentation preparation and family time."
)

sealed class MonthlyMemoryNavigationEvent {
    object NavigateBack : MonthlyMemoryNavigationEvent()
    object NavigateToAllActivities : MonthlyMemoryNavigationEvent()
    object NavigateToActivityAndMood : MonthlyMemoryNavigationEvent()
    object NavigateToActivityDetails : MonthlyMemoryNavigationEvent()
}

class MonthlyMemoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MonthlyMemoryUiState())
    val uiState: StateFlow<MonthlyMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<MonthlyMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<MonthlyMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(MonthlyMemoryNavigationEvent.NavigateBack)
        }
    }

    fun onMonthDropdownClicked() {
        // Handle month selection if needed
    }

    fun onSummaryCardClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(MonthlyMemoryNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onEntryItemClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(MonthlyMemoryNavigationEvent.NavigateToAllActivities)
        }
    }
}
