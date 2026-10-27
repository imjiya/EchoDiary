package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MemorySourceEntry(
    val title: String,
    val subtitle: String,
    val icon: String
)

data class MemoryDetailsUiState(
    val tagText: String = "SAVED · OCT 5–11, 2026",
    val title: String = "A week of progress and connection.",
    val summaryText: String = "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.",
    val highlights: List<String> = listOf("Project work", "Connection", "Calm walk"),
    val entries: List<MemorySourceEntry> = listOf(
        MemorySourceEntry("Lectures and project", "Oct 5, 2026 · College", "🎓"),
        MemorySourceEntry("Lunch with friends", "Oct 6, 2026 · Friends", "👥"),
        MemorySourceEntry("Evening walk", "Oct 7, 2026 · Health", "❤️"),
        MemorySourceEntry("Presentation preparation", "Oct 8, 2026 · Work", "💼"),
        MemorySourceEntry("Weekend with family", "Oct 10, 2026 · Family", "🏠")
    )
)

class MemoryDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MemoryDetailsUiState())
    val uiState: StateFlow<MemoryDetailsUiState> = _uiState.asStateFlow()
}
