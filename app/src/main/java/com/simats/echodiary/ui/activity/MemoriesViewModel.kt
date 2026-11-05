package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
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
    val memorySubtitle: String = "Oct 5–11, 2026 · 5 diary entries"
)

class MemoriesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MemoriesUiState())
    val uiState: StateFlow<MemoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.weeklyMemoryTitle.collectLatest {
                updateCardContent()
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.weeklyEntriesCount.collectLatest {
                updateCardContent()
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.monthlySummaryTitle.collectLatest {
                updateCardContent()
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.yearlyChapterTitle.collectLatest {
                updateCardContent()
            }
        }
    }

    private fun updateCardContent() {
        val tab = _uiState.value.activeFilterTab
        when (tab) {
            "Weekly" -> {
                val title = DiaryActivityStore.weeklyMemoryTitle.value
                val count = DiaryActivityStore.weeklyEntriesCount.value
                _uiState.update {
                    it.copy(
                        memoryCardHeader = "SAVED WEEKLY MEMORY",
                        memoryTitle = title,
                        memorySubtitle = "Oct 5–11, 2026 · $count diary entries"
                    )
                }
            }
            "Monthly" -> {
                val title = DiaryActivityStore.monthlySummaryTitle.value
                val count = DiaryActivityStore.monthlyEntriesCount.value
                _uiState.update {
                    it.copy(
                        memoryCardHeader = "SAVED MONTHLY MEMORY",
                        memoryTitle = title,
                        memorySubtitle = "October 2026 · $count diary entries"
                    )
                }
            }
            "Yearly" -> {
                val title = DiaryActivityStore.yearlyChapterTitle.value
                val count = DiaryActivityStore.yearlySupportingRecords.value.size
                _uiState.update {
                    it.copy(
                        memoryCardHeader = "SAVED YEARLY MEMORY",
                        memoryTitle = title,
                        memorySubtitle = "2026 · $count diary entries"
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
