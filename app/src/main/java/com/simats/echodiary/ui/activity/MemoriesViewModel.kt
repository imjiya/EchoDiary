package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MemoriesUiState(
    val selectedTab: Int = 2, // Memories tab is selected
    val activeFilterTab: String = "Weekly",
    val memoryTitle: String = "A week of progress and connection.",
    val memorySubtitle: String = "Oct 5–11, 2026 · 5 diary entries"
)

class MemoriesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MemoriesUiState())
    val uiState: StateFlow<MemoriesUiState> = _uiState.asStateFlow()

    fun onBottomTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun onFilterTabSelected(tab: String) {
        _uiState.update { it.copy(activeFilterTab = tab) }
    }
}
