package com.simats.echodiary.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.DiaryEntryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarUiState(
    val selectedTab: Int = 1,
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 10,
    val monthTitle: String = "November 2026",
    val selectedDay: Int = 17,
    val selectedDateText: String = "",
    val selectedActivityDate: String = "",
    val selectedActivityTitle: String = "",
    val selectedActivityCategory: String = "",
    val selectedActivityDescription: String = "",
    val selectedEntry: DiaryEntryItem? = null,
    val hasEntry: Boolean = false,
    val daysWithEntries: Set<Int> = emptySet(),
    val weeks: List<List<Int?>> = emptyList()
)

class CalendarViewModel : ViewModel() {
    private val calendar = Calendar.getInstance()
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        val today = Calendar.getInstance()
        calendar.time = today.time
        val dayOfMonth = today.get(Calendar.DAY_OF_MONTH)

        recomputeCalendar(dayOfMonth)

        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest {
                recomputeCalendar(_uiState.value.selectedDay)
            }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        recomputeCalendar(1)
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        recomputeCalendar(1)
    }

    fun selectDay(day: Int) {
        recomputeCalendar(day)
    }

    fun onEntryCardClicked() {
        val entry = _uiState.value.selectedEntry
        if (entry != null) {
            DiaryActivityStore.selectActivity(entry)
        }
    }

    private fun recomputeCalendar(targetDay: Int) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val safeDay = targetDay.coerceIn(1, maxDays)

        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthTitle = monthFormat.format(calendar.time)

        // Compute weeks grid
        val tempCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
        val leadingBlanks = (firstDayOfWeek + 5) % 7 // Monday start: Mon=0, Tue=1, ..., Sun=6

        val allCells = mutableListOf<Int?>()
        for (i in 0 until leadingBlanks) allCells.add(null)
        for (d in 1..maxDays) allCells.add(d)
        while (allCells.size % 7 != 0) allCells.add(null)

        val weeks = allCells.chunked(7)

        val activities = DiaryActivityStore.activities.value
        val entryDays = mutableSetOf<Int>()
        var foundEntry: DiaryEntryItem? = null

        val shortMonthName = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time).lowercase(Locale.ROOT)
        val fullMonthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time).lowercase(Locale.ROOT)
        val yearStr = year.toString()

        for (item in activities) {
            val dateStr = item.date.lowercase(Locale.ROOT)
            val monthMatch = dateStr.contains(shortMonthName) || dateStr.contains(fullMonthName)
            if (monthMatch || activities.size <= 5) {
                val match = "\\b(\\d{1,2})\\b".toRegex().find(item.date)
                if (match != null) {
                    val d = match.groupValues[1].toIntOrNull()
                    if (d != null && d in 1..maxDays) {
                        entryDays.add(d)
                        if (d == safeDay && foundEntry == null) {
                            foundEntry = item
                        }
                    }
                }
            }
        }

        tempCal.set(Calendar.DAY_OF_MONTH, safeDay)
        val dayOfWeekName = SimpleDateFormat("EEEE", Locale.getDefault()).format(tempCal.time)
        val formattedDateText = "$dayOfWeekName, ${SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(tempCal.time)}"
        val formattedActivityDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(tempCal.time)

        val hasEntry = foundEntry != null
        val (title, category, desc) = if (foundEntry != null) {
            Triple(foundEntry.title, foundEntry.category, foundEntry.description)
        } else {
            Triple(
                "No diary entry for $formattedActivityDate",
                "Diary",
                "No previous entry found for this date. Tap Write Diary to create one."
            )
        }

        _uiState.update {
            it.copy(
                selectedYear = year,
                selectedMonth = month,
                monthTitle = monthTitle,
                selectedDay = safeDay,
                selectedDateText = formattedDateText,
                selectedActivityDate = formattedActivityDate,
                selectedActivityTitle = title,
                selectedActivityCategory = category,
                selectedActivityDescription = desc,
                selectedEntry = foundEntry,
                hasEntry = hasEntry,
                daysWithEntries = entryDays,
                weeks = weeks
            )
        }
    }
}
