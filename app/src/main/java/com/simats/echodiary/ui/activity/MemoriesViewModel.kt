package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MemoriesUiState(
    val selectedTab: Int = 2, // Memories tab is selected
    val activeFilterTab: String = "Weekly",
    val memoryCardHeader: String = "SAVED WEEKLY MEMORY",
    val memoryTitle: String = "A week of progress and connection.",
    val memoryNarrative: String = "This week, you recorded your thoughts and reflections. Your entries capture your experiences.",
    val memorySubtitle: String = "Oct 5–11, 2026 · 5 diary entries",
    val highlights: List<String> = emptyList(),
    val entries: List<DiaryEntryItem> = emptyList(),
    val totalSavedMemoriesCount: Int = 3,
    val photosSubtitle: String = "Lunch with friends · Oct 6"
)

class MemoriesViewModel(
    scope: CoroutineScope? = null
) : ViewModel() {
    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private val _uiState = MutableStateFlow(MemoriesUiState())
    val uiState: StateFlow<MemoriesUiState> = _uiState.asStateFlow()

    init {
        executionScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                val photoEntries = activities.filter { it.hasPhoto || !it.photoUri.isNullOrBlank() }
                val sub = if (photoEntries.isNotEmpty()) {
                    val first = photoEntries.first()
                    "${first.title} · ${first.date}"
                } else {
                    "No photo memories yet"
                }
                _uiState.update { it.copy(photosSubtitle = sub) }
                updateCardContent()
            }
        }
        executionScope.launch {
            DiaryActivityStore.weeklyMemoryTitle.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.weeklyMemoryNarrative.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.weeklyHighlights.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.weeklyEntriesCount.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.monthlySummaryTitle.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.monthlySummaryText.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.monthlyCategoryBreakdown.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.yearlyChapterTitle.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.yearlyChapterText.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.yearlyCategories.collectLatest { updateCardContent() }
        }
    }

    private fun updateCardContent() {
        val allActivities = DiaryActivityStore.activities.value
        val tab = _uiState.value.activeFilterTab
        when (tab) {
            "Weekly" -> {
                val title = DiaryActivityStore.weeklyMemoryTitle.value
                val narrative = DiaryActivityStore.weeklyMemoryNarrative.value
                val count = DiaryActivityStore.weeklyEntriesCount.value.coerceAtLeast(allActivities.size)
                val highlights = DiaryActivityStore.weeklyHighlights.value
                _uiState.update {
                    it.copy(
                        memoryCardHeader = "SAVED WEEKLY MEMORY",
                        memoryTitle = title,
                        memoryNarrative = narrative,
                        memorySubtitle = "Oct 5–11, 2026 · $count diary entries",
                        highlights = highlights,
                        entries = allActivities
                    )
                }
            }
            "Monthly" -> {
                val title = DiaryActivityStore.monthlySummaryTitle.value
                val narrative = DiaryActivityStore.monthlySummaryText.value
                val count = DiaryActivityStore.monthlyEntriesCount.value.coerceAtLeast(allActivities.size)
                val categories = DiaryActivityStore.monthlyCategoryBreakdown.value.map { it.name }
                _uiState.update {
                    it.copy(
                        memoryCardHeader = "SAVED MONTHLY MEMORY",
                        memoryTitle = title,
                        memoryNarrative = narrative,
                        memorySubtitle = "October 2026 · $count diary entries",
                        highlights = categories,
                        entries = allActivities
                    )
                }
            }
            "Yearly" -> {
                val title = DiaryActivityStore.yearlyChapterTitle.value
                val narrative = DiaryActivityStore.yearlyChapterText.value
                val count = DiaryActivityStore.yearlySupportingRecords.value.size.coerceAtLeast(allActivities.size)
                val categories = DiaryActivityStore.yearlyCategories.value
                _uiState.update {
                    it.copy(
                        memoryCardHeader = "SAVED YEARLY MEMORY",
                        memoryTitle = title,
                        memoryNarrative = narrative,
                        memorySubtitle = "2026 · $count diary entries",
                        highlights = categories,
                        entries = allActivities
                    )
                }
            }
        }
    }

    fun onBottomTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun onFilterTabSelected(tab: String) {
        _uiState.update { it.copy(activeFilterTab = tab) }
        updateCardContent()
    }
}
