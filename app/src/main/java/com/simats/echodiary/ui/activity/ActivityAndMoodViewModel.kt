package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MoodBreakdownItem(
    val name: String,
    val count: Int,
    val total: Int,
    val progress: Float
)

data class ActivityAndMoodUiState(
    val selectedTab: String = "Week",
    val dateRangeText: String = "",
    val activitiesCount: Int = 0,
    val categoriesCount: Int = 0,
    val categories: List<CategoryBreakdownItem> = emptyList(),
    val moodBreakdown: List<MoodBreakdownItem> = emptyList(),
    val moodNote: String = "",
    val sourceText: String = "",
    val featuredEntry: DiaryEntryItem? = null,
    val footerText: String = "No mood is inferred from entry descriptions; grounded directly in what you recorded."
)

sealed class ActivityAndMoodNavigationEvent {
    object NavigateBack : ActivityAndMoodNavigationEvent()
    object NavigateToActivityDetails : ActivityAndMoodNavigationEvent()
}

class ActivityAndMoodViewModel(
    scope: CoroutineScope? = null
) : ViewModel() {

    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private val _uiState = MutableStateFlow(
        computeInsights(
            allEntries = DiaryActivityStore.activities.value,
            tab = "Week"
        )
    )
    val uiState: StateFlow<ActivityAndMoodUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ActivityAndMoodNavigationEvent>()
    val navigationEvent: SharedFlow<ActivityAndMoodNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        try {
            executionScope.launch {
                DiaryActivityStore.activities.collect { entries ->
                    recompute(entries = entries)
                }
            }
        } catch (_: Exception) {}
    }

    fun onBackClicked() {
        try {
            executionScope.launch {
                _navigationEvent.emit(ActivityAndMoodNavigationEvent.NavigateBack)
            }
        } catch (_: Exception) {}
    }

    fun onTabSelected(tab: String) {
        recompute(tab = tab)
    }

    fun onActivitiesChanged(entries: List<DiaryEntryItem>) {
        recompute(entries = entries)
    }

    fun onSourceClicked() {
        val entry = _uiState.value.featuredEntry
        if (entry != null) {
            DiaryActivityStore.selectActivity(entry)
        }
        try {
            executionScope.launch {
                _navigationEvent.emit(ActivityAndMoodNavigationEvent.NavigateToActivityDetails)
            }
        } catch (_: Exception) {}
    }

    fun recompute(
        entries: List<DiaryEntryItem> = DiaryActivityStore.activities.value,
        tab: String = _uiState.value.selectedTab
    ) {
        _uiState.value = computeInsights(allEntries = entries, tab = tab)
    }

    companion object {
        private val datePatterns = listOf(
            "MMM d, yyyy",
            "MMM dd, yyyy",
            "yyyy-MM-dd",
            "EEEE, MMM d, yyyy"
        )

        @Synchronized
        fun parseDate(dateStr: String): Date? {
            val trimmed = dateStr.trim()
            if (trimmed.isBlank()) return null
            for (pattern in datePatterns) {
                try {
                    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                        isLenient = true
                    }
                    val d = sdf.parse(trimmed)
                    if (d != null) return d
                } catch (_: Exception) {}
            }
            return null
        }

        fun filterEntriesByTab(
            allEntries: List<DiaryEntryItem>,
            tab: String
        ): List<DiaryEntryItem> {
            if (allEntries.isEmpty()) return emptyList()

            // Find the most recent date among parsed entries to serve as the anchor
            val latestParsedDate = allEntries.mapNotNull { parseDate(it.date) }.maxOrNull()
                ?: return allEntries

            val anchorCal = Calendar.getInstance(Locale.ENGLISH).apply {
                firstDayOfWeek = Calendar.MONDAY
                minimalDaysInFirstWeek = 4
                time = latestParsedDate
            }
            val anchorYear = anchorCal.get(Calendar.YEAR)
            val anchorMonth = anchorCal.get(Calendar.MONTH)
            val anchorWeek = anchorCal.get(Calendar.WEEK_OF_YEAR)

            val filtered = when (tab) {
                "Week" -> {
                    allEntries.filter { entry ->
                        val date = parseDate(entry.date)
                        if (date != null) {
                            val cal = Calendar.getInstance(Locale.ENGLISH).apply {
                                firstDayOfWeek = Calendar.MONDAY
                                minimalDaysInFirstWeek = 4
                                time = date
                            }
                            (cal.get(Calendar.YEAR) == anchorYear && cal.get(Calendar.WEEK_OF_YEAR) == anchorWeek) ||
                                (Math.abs(latestParsedDate.time - date.time) <= 7L * 24 * 60 * 60 * 1000L)
                        } else true
                    }
                }
                "Month" -> {
                    allEntries.filter { entry ->
                        val date = parseDate(entry.date)
                        if (date != null) {
                            val cal = Calendar.getInstance(Locale.ENGLISH).apply { time = date }
                            cal.get(Calendar.YEAR) == anchorYear && cal.get(Calendar.MONTH) == anchorMonth
                        } else true
                    }
                }
                "Year" -> {
                    allEntries.filter { entry ->
                        val date = parseDate(entry.date)
                        if (date != null) {
                            val cal = Calendar.getInstance(Locale.ENGLISH).apply { time = date }
                            cal.get(Calendar.YEAR) == anchorYear
                        } else true
                    }
                }
                else -> allEntries
            }

            return if (filtered.isNotEmpty()) filtered else allEntries
        }

        fun computeDateRangeText(
            entries: List<DiaryEntryItem>,
            tab: String
        ): String {
            if (entries.isEmpty()) return "No recorded entries"

            val count = entries.size
            val entryLabel = if (count == 1) "recorded entry" else "recorded entries"

            val sortedDates = entries.mapNotNull { parseDate(it.date) }.sorted()
            if (sortedDates.isEmpty()) {
                return "$tab view · $count $entryLabel"
            }

            val earliest = sortedDates.first()
            val latest = sortedDates.last()

            return when (tab) {
                "Week" -> {
                    val monthFmt = SimpleDateFormat("MMM", Locale.ENGLISH)
                    val dayFmt = SimpleDateFormat("d", Locale.ENGLISH)
                    val yearFmt = SimpleDateFormat("yyyy", Locale.ENGLISH)

                    val startMonth = monthFmt.format(earliest)
                    val endMonth = monthFmt.format(latest)
                    val startDay = dayFmt.format(earliest)
                    val endDay = dayFmt.format(latest)
                    val year = yearFmt.format(latest)

                    if (startMonth == endMonth && startDay == endDay) {
                        "$startMonth $startDay, $year · $count $entryLabel"
                    } else if (startMonth == endMonth) {
                        "$startMonth $startDay–$endDay, $year · $count $entryLabel"
                    } else {
                        "$startMonth $startDay – $endMonth $endDay, $year · $count $entryLabel"
                    }
                }
                "Month" -> {
                    val monthYearFmt = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
                    "${monthYearFmt.format(latest)} · $count $entryLabel"
                }
                "Year" -> {
                    val yearFmt = SimpleDateFormat("yyyy", Locale.ENGLISH)
                    "${yearFmt.format(latest)} · $count $entryLabel"
                }
                else -> "$tab · $count $entryLabel"
            }
        }

        fun computeInsights(
            allEntries: List<DiaryEntryItem>,
            tab: String
        ): ActivityAndMoodUiState {
            if (allEntries.isEmpty()) {
                return ActivityAndMoodUiState(
                    selectedTab = tab,
                    dateRangeText = "No recorded entries",
                    activitiesCount = 0,
                    categoriesCount = 0,
                    categories = emptyList(),
                    moodBreakdown = emptyList(),
                    moodNote = "No entries recorded yet. Add an activity or write a diary entry to view insights.",
                    sourceText = "No source entry",
                    featuredEntry = null,
                    footerText = "No mood is inferred from entry descriptions; grounded directly in what you recorded."
                )
            }

            val entries = filterEntriesByTab(allEntries, tab)
            val totalCount = entries.size

            // 1. Categories Breakdown
            val categoryCounts = entries.groupBy { it.category.trim().ifBlank { "Other" } }
                .mapValues { it.value.size }

            val sortedCategories = categoryCounts.entries
                .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })

            val categoryBreakdown = sortedCategories.map { (catName, count) ->
                val progress = if (totalCount > 0) count.toFloat() / totalCount.toFloat() else 0f
                CategoryBreakdownItem(
                    name = catName,
                    count = count,
                    progress = progress
                )
            }
            val uniqueCategoriesCount = categoryBreakdown.size

            // 2. Mood Breakdown
            val explicitMoodEntries = mutableListOf<DiaryEntryItem>()
            var unspecifiedCount = 0

            entries.forEach { entry ->
                val mood = entry.mood.trim()
                if (mood.isNotBlank() && !mood.equals("Unspecified", ignoreCase = true) && !mood.equals("Not specified", ignoreCase = true)) {
                    explicitMoodEntries.add(entry)
                } else {
                    unspecifiedCount++
                }
            }

            val moodCounts = explicitMoodEntries.groupBy { it.mood.trim() }
                .mapValues { it.value.size }

            val sortedMoods = moodCounts.entries
                .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })

            val moodBreakdown = mutableListOf<MoodBreakdownItem>()
            sortedMoods.forEach { (moodName, count) ->
                val progress = if (totalCount > 0) count.toFloat() / totalCount.toFloat() else 0f
                moodBreakdown.add(
                    MoodBreakdownItem(
                        name = moodName,
                        count = count,
                        total = totalCount,
                        progress = progress
                    )
                )
            }

            if (unspecifiedCount > 0) {
                val progress = if (totalCount > 0) unspecifiedCount.toFloat() / totalCount.toFloat() else 0f
                moodBreakdown.add(
                    MoodBreakdownItem(
                        name = "Unspecified",
                        count = unspecifiedCount,
                        total = totalCount,
                        progress = progress
                    )
                )
            }

            // 3. Mood Note
            val moodNote = when {
                explicitMoodEntries.isEmpty() -> {
                    "All $totalCount ${if (totalCount == 1) "entry has" else "entries have"} no mood specified."
                }
                unspecifiedCount == 0 -> {
                    val summary = sortedMoods.joinToString(", ") { "${it.key}: ${it.value}" }
                    "$summary. All $totalCount entries have explicit recorded moods."
                }
                else -> {
                    val summary = sortedMoods.joinToString(", ") { "${it.key}: ${it.value}" }
                    "$summary. $unspecifiedCount ${if (unspecifiedCount == 1) "entry is" else "entries are"} unspecified."
                }
            }

            // 4. Date Range Text
            val dateRangeText = computeDateRangeText(entries, tab)

            // 5. Featured Source Entry
            val featuredEntry = explicitMoodEntries.firstOrNull() ?: entries.firstOrNull()
            val sourceText = if (featuredEntry != null) {
                "Source: ${featuredEntry.title} · ${featuredEntry.date} →"
            } else {
                "Source: No entries available"
            }

            return ActivityAndMoodUiState(
                selectedTab = tab,
                dateRangeText = dateRangeText,
                activitiesCount = totalCount,
                categoriesCount = uniqueCategoriesCount,
                categories = categoryBreakdown,
                moodBreakdown = moodBreakdown,
                moodNote = moodNote,
                sourceText = sourceText,
                featuredEntry = featuredEntry,
                footerText = "No mood is inferred from entry descriptions; grounded directly in what you recorded."
            )
        }
    }
}
