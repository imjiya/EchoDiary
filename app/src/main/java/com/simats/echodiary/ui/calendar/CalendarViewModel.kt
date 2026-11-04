package com.simats.echodiary.ui.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalendarUiState(
    val selectedTab: Int = 1, // Calendar tab is selected
    val selectedDay: Int = 5,
    val selectedDateText: String = "Monday, October 5, 2026",
    val selectedActivityTitle: String = "Lectures and project",
    val selectedActivityCategory: String = "College",
    val selectedActivityDate: String = "Oct 5, 2026",
    val selectedActivityDescription: String = "Attended lectures and worked on a project.",
    val hasEntry: Boolean = true
)

class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun selectDay(day: Int) {
        val weekdays = listOf("Thursday", "Friday", "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday")
        // Oct 1 was Thursday (index 0)
        val weekday = weekdays[(day - 1) % 7]
        val formattedDateText = "$weekday, October $day, 2026"
        val formattedActivityDate = "Oct $day, 2026"

        val hasPredefinedEntry = day in listOf(5, 6, 7, 8, 10, 11)
        val (title, category, desc) = when (day) {
            5 -> Triple("Lectures and project", "College", "Attended lectures and worked on a project.")
            6 -> Triple("Team study session", "College", "Reviewed algorithms with the study group.")
            7 -> Triple("Evening workout", "Exercise", "Completed 45 minutes of cardio and strength training.")
            8 -> Triple("Dinner with friends", "Friends", "Met up with college friends for dinner and caught up.")
            10 -> Triple("Weekend project build", "Learning", "Built and tested the EchoDiary voice translation module.")
            11 -> Triple("Reflective diary entry", "Daily", "Organized notes and planned upcoming weekly goals.")
            else -> Triple("Selected date: Oct $day", "Diary", "No previous entry found for this date. Tap Write Diary to create one.")
        }

        _uiState.update {
            it.copy(
                selectedDay = day,
                selectedDateText = formattedDateText,
                selectedActivityDate = formattedActivityDate,
                selectedActivityTitle = title,
                selectedActivityCategory = category,
                selectedActivityDescription = desc,
                hasEntry = hasPredefinedEntry
            )
        }
    }
}
