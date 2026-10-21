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

data class SupportingRecord(
    val title: String,
    val date: String
)

data class YearlyMemoryUiState(
    val yearTitle: String = "2026",
    val infoText: String = "Limited sample: 5 October entries only. No other months are represented.",
    val chapterTitle: String = "One small chapter of 2026.",
    val chapterText: String = "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.",
    val categories: List<String> = listOf("College", "Friends", "Health", "Work", "Family"),
    val supportingRecords: List<SupportingRecord> = listOf(
        SupportingRecord("Lectures and project", "Oct 5"),
        SupportingRecord("Lunch with friends", "Oct 6"),
        SupportingRecord("Evening walk", "Oct 7"),
        SupportingRecord("Presentation preparation", "Oct 8"),
        SupportingRecord("Weekend with family", "Oct 10")
    ),
    val footerText: String = "Topics come from these records only; no yearly trends are inferred."
)

sealed class YearlyMemoryNavigationEvent {
    object NavigateBack : YearlyMemoryNavigationEvent()
    object NavigateToAllActivities : YearlyMemoryNavigationEvent()
}

class YearlyMemoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(YearlyMemoryUiState())
    val uiState: StateFlow<YearlyMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<YearlyMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<YearlyMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateBack)
        }
    }

    fun onYearDropdownClicked() {
        // Handle year selection if needed
    }

    fun onRecordClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateToAllActivities)
        }
    }
}
