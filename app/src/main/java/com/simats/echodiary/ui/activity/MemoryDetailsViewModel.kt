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

data class MemorySourceEntry(
    val title: String,
    val subtitle: String,
    val icon: String,
    val rawEntry: DiaryEntryItem? = null
)

data class MemoryDetailsUiState(
    val tagText: String = "",
    val title: String = "",
    val summaryText: String = "",
    val highlights: List<String> = emptyList(),
    val entries: List<MemorySourceEntry> = emptyList()
)

class MemoryDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MemoryDetailsUiState())
    val uiState: StateFlow<MemoryDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                val sourceEntries = activities.map {
                    MemorySourceEntry(
                        title = it.title,
                        subtitle = "${it.date} · ${it.category}",
                        icon = it.icon.ifBlank { DiaryActivityStore.getCategoryIcon(it.category) },
                        rawEntry = it
                    )
                }
                val weekRange = DiaryActivityStore.weeklyMemoryDateRange.value
                val title = DiaryActivityStore.weeklyMemoryTitle.value
                val narrative = DiaryActivityStore.weeklyMemoryNarrative.value
                val highlights = DiaryActivityStore.weeklyHighlights.value

                _uiState.update {
                    it.copy(
                        tagText = "SAVED · ${weekRange.substringBefore(" ·")}",
                        title = title,
                        summaryText = narrative,
                        highlights = highlights,
                        entries = sourceEntries
                    )
                }
            }
        }
    }
}
