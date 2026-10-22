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

data class MoodBreakdownItem(
    val name: String,
    val count: Int,
    val total: Int,
    val progress: Float
)

data class ActivityAndMoodUiState(
    val selectedTab: String = "Week",
    val dateRangeText: String = "Oct 5–11, 2026 · Limited to 5 recorded entries",
    val activitiesCount: Int = 5,
    val categoriesCount: Int = 5,
    val categories: List<CategoryBreakdownItem> = listOf(
        CategoryBreakdownItem("College", 1, 1f),
        CategoryBreakdownItem("Friends", 1, 1f),
        CategoryBreakdownItem("Health", 1, 1f),
        CategoryBreakdownItem("Work", 1, 1f),
        CategoryBreakdownItem("Family", 1, 1f)
    ),
    val moodBreakdown: List<MoodBreakdownItem> = listOf(
        MoodBreakdownItem("Calm", 1, 5, 0.2f),
        MoodBreakdownItem("Unspecified", 4, 5, 0.8f)
    ),
    val moodNote: String = "Calm: 1 explicit mood. 4 entries unspecified.",
    val sourceText: String = "Source: A3 · Evening walk · Oct 7, 2026 →",
    val footerText: String = "No mood is inferred from the entry descriptions."
)

sealed class ActivityAndMoodNavigationEvent {
    object NavigateBack : ActivityAndMoodNavigationEvent()
    object NavigateToActivityDetails : ActivityAndMoodNavigationEvent()
}

class ActivityAndMoodViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityAndMoodUiState())
    val uiState: StateFlow<ActivityAndMoodUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ActivityAndMoodNavigationEvent>()
    val navigationEvent: SharedFlow<ActivityAndMoodNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ActivityAndMoodNavigationEvent.NavigateBack)
        }
    }

    fun onTabSelected(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onSourceClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ActivityAndMoodNavigationEvent.NavigateToActivityDetails)
        }
    }
}
