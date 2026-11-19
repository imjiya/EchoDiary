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
    val memoryTitle: String = DiaryActivityStore.weeklyMemoryTitle.value,
    val memoryNarrative: String = DiaryActivityStore.weeklyMemoryNarrative.value,
    val memorySubtitle: String = "${DiaryActivityStore.getCurrentWeekLabel()} · ${DiaryActivityStore.activities.value.size} diary entries",
    val highlights: List<String> = DiaryActivityStore.weeklyHighlights.value,
    val entries: List<DiaryEntryItem> = emptyList(),
    val totalSavedMemoriesCount: Int = if (DiaryActivityStore.activities.value.isEmpty()) 0 else 1,
    val photosSubtitle: String = "No photo memories yet",
    val monthlyCardSubtitle: String = "${DiaryActivityStore.getCurrentMonthLabel()} · Based on your entries",
    val yearlyCardSubtitle: String = "${DiaryActivityStore.getCurrentYearLabel()} · Based on your entries"
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
                val photoEntries = activities.filter { it.getAllPhotos().isNotEmpty() }
                val sub = if (photoEntries.isNotEmpty()) {
                    val first = photoEntries.first()
                    "${first.title} · ${first.date}"
                } else {
                    val weeklyPhotos = linkedSetOf<String>()
                    DiaryActivityStore.weeklyMemoryPhotoUri.value?.let { if (it.isNotBlank()) weeklyPhotos.add(it) }
                    DiaryActivityStore.weeklyMemoryPhotoUris.value.forEach { if (it.isNotBlank()) weeklyPhotos.add(it) }
                    if (weeklyPhotos.isNotEmpty()) {
                        "Weekly Memory · Photo attached"
                    } else {
                        "No photo memories yet"
                    }
                }
                _uiState.update {
                    it.copy(
                        photosSubtitle = sub,
                        totalSavedMemoriesCount = if (activities.isEmpty()) 0 else 1,
                        monthlyCardSubtitle = "${DiaryActivityStore.getCurrentMonthLabel()} · ${activities.size} entries",
                        yearlyCardSubtitle = "${DiaryActivityStore.getCurrentYearLabel()} · ${activities.size} entries"
                    )
                }
                updateCardContent()
            }
        }
        executionScope.launch {
            DiaryActivityStore.weeklyMemoryTitle.collectLatest { updateCardContent() }
        }
        executionScope.launch {
            DiaryActivityStore.weeklyMemoryPhotoUri.collectLatest { photo ->
                if (!photo.isNullOrBlank() && _uiState.value.photosSubtitle == "No photo memories yet") {
                    _uiState.update {
                        it.copy(photosSubtitle = "Weekly Memory · Photo attached")
                    }
                }
                updateCardContent()
            }
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
                        memorySubtitle = "${DiaryActivityStore.getCurrentWeekLabel()} · $count diary entries",
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
                        memorySubtitle = "${DiaryActivityStore.getCurrentMonthLabel()} · $count diary entries",
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
                        memorySubtitle = "${DiaryActivityStore.getCurrentYearLabel()} · $count diary entries",
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
