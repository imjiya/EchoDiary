package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.repository.MemoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeeklySummaryEntry(
    val title: String,
    val categoryDetails: String,
    val icon: String
)

data class YourWeeklySummaryUiState(
    val dateRange: String = DiaryActivityStore.getCurrentWeekLabel().uppercase(),
    val diaryEntriesCount: Int = DiaryActivityStore.activities.value.size,
    val categoriesCount: Int = DiaryActivityStore.activities.value.map { it.category }.distinct().size,
    val entries: List<WeeklySummaryEntry> = emptyList(),
    val moodSummary: String = DiaryActivityStore.synthesizeMoodSummary(DiaryActivityStore.activities.value)
)

sealed class YourWeeklySummaryNavigationEvent {
    object NavigateBack : YourWeeklySummaryNavigationEvent()
    object NavigateToAllActivities : YourWeeklySummaryNavigationEvent()
    object NavigateToCreatingMemory : YourWeeklySummaryNavigationEvent()
    object NavigateToAnalysisResults : YourWeeklySummaryNavigationEvent()
}

class YourWeeklySummaryViewModel(
    private val memoryRepository: MemoryRepository? = null,
    scope: CoroutineScope? = null
) : ViewModel() {

    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private fun getRepository(): MemoryRepository? {
        return memoryRepository ?: try {
            EchoDiaryApp.getMemoryRepository()
        } catch (_: Exception) {
            null
        }
    }

    private val _uiState = MutableStateFlow(YourWeeklySummaryUiState())
    val uiState: StateFlow<YourWeeklySummaryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<YourWeeklySummaryNavigationEvent>()
    val navigationEvent: SharedFlow<YourWeeklySummaryNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        executionScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                val distinctCats = activities.map { it.category }.distinct()
                val summaryEntries = activities.take(5).map {
                    WeeklySummaryEntry(
                        title = it.title,
                        categoryDetails = "${it.category} · ${it.date}",
                        icon = it.icon
                    )
                }
                _uiState.update {
                    it.copy(
                        dateRange = DiaryActivityStore.getCurrentWeekLabel().uppercase(),
                        diaryEntriesCount = activities.size,
                        categoriesCount = distinctCats.size,
                        entries = summaryEntries,
                        moodSummary = DiaryActivityStore.synthesizeMoodSummary(activities)
                    )
                }
            }
        }
    }

    fun onGenerateWeeklyMemoryClicked() {
        // Automatically create memory summary of a week using RAG & SLM synthesis
        DiaryActivityStore.setGeneratingMemoryType("weekly")
        DiaryActivityStore.generateWeeklySummary(executionScope)
        executionScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateToCreatingMemory)
        }
    }

    fun onViewAnalysisClicked() {
        executionScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateToAnalysisResults)
        }
    }

    fun onBackClicked() {
        executionScope.launch {
            _navigationEvent.emit(YourWeeklySummaryNavigationEvent.NavigateBack)
        }
    }
}
