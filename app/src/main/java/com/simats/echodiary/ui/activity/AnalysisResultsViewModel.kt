package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalysisItem(
    val title: String,
    val sublink: String,
    val icon: String,
    val rawEntry: DiaryEntryItem? = null
)

data class AnalysisResultsUiState(
    val headerSubtitle: String = "",
    val title: String = "Grounded in your words.",
    val description: String = "",
    val items: List<AnalysisItem> = emptyList(),
    val moodEvidenceText: String = ""
)

sealed class AnalysisResultsNavigationEvent {
    object NavigateBack : AnalysisResultsNavigationEvent()
    object NavigateToWeeklyMemory : AnalysisResultsNavigationEvent()
    object NavigateToActivityDetails : AnalysisResultsNavigationEvent()
}

class AnalysisResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisResultsUiState())
    val uiState: StateFlow<AnalysisResultsUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AnalysisResultsNavigationEvent>()
    val navigationEvent: SharedFlow<AnalysisResultsNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                val count = activities.size
                val categories = activities.map { it.category }.distinct()
                val weekRange = DiaryActivityStore.weeklyMemoryDateRange.value

                val analysisItems = activities.mapIndexed { idx, entry ->
                    val moodSnippet = if (entry.mood.isNotBlank() && !entry.mood.equals("not specified", ignoreCase = true)) {
                        " · ${entry.mood} recorded"
                    } else ""
                    AnalysisItem(
                        title = "${entry.category} · ${entry.title}$moodSnippet",
                        sublink = "View A${idx + 1}: ${entry.title} · ${entry.date}",
                        icon = entry.icon.ifBlank { DiaryActivityStore.getCategoryIcon(entry.category) },
                        rawEntry = entry
                    )
                }

                val desc = if (count > 0) {
                    "${categories.size} categories across $count entries. Topics below come directly from your saved diary."
                } else {
                    "No diary entries recorded for this period yet."
                }

                val moodEvidence = DiaryActivityStore.synthesizeMoodSummary(activities)

                _uiState.update {
                    it.copy(
                        headerSubtitle = "${weekRange.substringBefore(" ·")} · BASED ON $count ENTRIES",
                        description = desc,
                        items = analysisItems,
                        moodEvidenceText = "Mood evidence: $moodEvidence"
                    )
                }
            }
        }
    }

    fun onViewMemoryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AnalysisResultsNavigationEvent.NavigateToWeeklyMemory)
        }
    }

    fun onEntryItemClicked(item: AnalysisItem? = null) {
        val entry = item?.rawEntry ?: _uiState.value.items.firstOrNull()?.rawEntry ?: DiaryActivityStore.selectedActivity.value
        if (entry != null) {
            DiaryActivityStore.selectActivity(entry)
        }
        viewModelScope.launch {
            _navigationEvent.emit(AnalysisResultsNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AnalysisResultsNavigationEvent.NavigateBack)
        }
    }
}
