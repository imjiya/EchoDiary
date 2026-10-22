package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilterActivitiesUiState(
    val fromDate: String = "Oct 5, 2026",
    val toDate: String = "Oct 11, 2026",
    val selectedCategory: String = "All",
    val selectedSource: String = "All",
    val selectedMood: String = "All",
    val categories: List<String> = listOf("All", "College", "Work", "Friends", "Family", "Health", "Travel", "Exercise", "Learning", "Hobbies", "Other"),
    val sources: List<String> = listOf("All", "Voice", "Text"),
    val moods: List<String> = listOf("All", "Calm", "Unspecified")
)

sealed class FilterActivitiesNavigationEvent {
    object NavigateBack : FilterActivitiesNavigationEvent()
    object NavigateToActivityDetails : FilterActivitiesNavigationEvent()
    object NavigateToAllActivities : FilterActivitiesNavigationEvent()
}

class FilterActivitiesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FilterActivitiesUiState())
    val uiState: StateFlow<FilterActivitiesUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<FilterActivitiesNavigationEvent>()
    val navigationEvent: SharedFlow<FilterActivitiesNavigationEvent> = _navigationEvent.asSharedFlow()

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectSource(source: String) {
        _uiState.update { it.copy(selectedSource = source) }
    }

    fun selectMood(mood: String) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun onResetClicked() {
        _uiState.update {
            it.copy(
                selectedCategory = "All",
                selectedSource = "All",
                selectedMood = "All"
            )
        }
    }

    fun onApplyFiltersClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(FilterActivitiesNavigationEvent.NavigateToAllActivities)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(FilterActivitiesNavigationEvent.NavigateBack)
        }
    }
}
