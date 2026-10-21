package com.simats.echodiary.ui.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalendarUiState(
    val selectedTab: Int = 1, // Calendar tab is selected
    val selectedDay: Int = 5,
    val selectedDateText: String = "Monday, October 5",
    val selectedActivityTitle: String = "Lectures and project",
    val selectedActivityCategory: String = "College",
    val selectedActivityDate: String = "Oct 5, 2026",
    val selectedActivityDescription: String = "Attended lectures and worked on a project."
)

class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun onTabSelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun selectDay(day: Int) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
    }
}
