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

data class SelectedDateUiState(
    val dayOfWeek: String = "MONDAY",
    val fullDate: String = "October 5, 2026",
    val entrySummary: String = "1 diary entry · College",
    val entryItem: DiaryEntryItem = DiaryEntryItem("Lectures and project", "College", "Oct 5, 2026", "Attended lectures and worked on a project.", "🎓")
)

sealed class SelectedDateNavigationEvent {
    object NavigateBack : SelectedDateNavigationEvent()
    object NavigateToActivityDetails : SelectedDateNavigationEvent()
    object NavigateToAddEntry : SelectedDateNavigationEvent()
}

class SelectedDateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SelectedDateUiState())
    val uiState: StateFlow<SelectedDateUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SelectedDateNavigationEvent>()
    val navigationEvent: SharedFlow<SelectedDateNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onViewActivityDetailsClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onAddEntryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateToAddEntry)
        }
    }

    fun onBackToCalendarClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateBack)
        }
    }
}
