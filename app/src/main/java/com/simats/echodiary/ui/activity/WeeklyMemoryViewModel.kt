package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeeklyMemoryUiState(
    val dateRangeText: String = "OCT 5–11, 2026 · WEEKLY DRAFT",
    val title: String = "A week of progress and connection.",
    val summaryText: String = "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.",
    val highlights: List<String> = listOf("College project", "Friends & family", "Presentation prep", "Calm walk"),
    val footerText: String = "Based on your 5 entries. Review before saving."
)

sealed class WeeklyMemoryNavigationEvent {
    object NavigateBack : WeeklyMemoryNavigationEvent()
    object NavigateToMemories : WeeklyMemoryNavigationEvent()
    object NavigateToAllActivities : WeeklyMemoryNavigationEvent()
}

class WeeklyMemoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyMemoryUiState())
    val uiState: StateFlow<WeeklyMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<WeeklyMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<WeeklyMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onSaveMemoryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WeeklyMemoryNavigationEvent.NavigateToMemories)
        }
    }

    fun onEditSummaryClicked() {
        // Edit action
    }

    fun onShareClicked() {
        // Share action
    }

    fun onViewDetailsClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WeeklyMemoryNavigationEvent.NavigateToAllActivities)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WeeklyMemoryNavigationEvent.NavigateBack)
        }
    }
}
