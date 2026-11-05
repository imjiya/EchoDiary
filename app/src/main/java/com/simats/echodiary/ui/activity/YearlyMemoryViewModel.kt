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

data class SupportingRecord(
    val title: String,
    val date: String
)

data class YearlyMemoryUiState(
    val yearTitle: String = "2026",
    val infoText: String = "Source-grounded yearly memory from your recorded entries.",
    val chapterTitle: String = "One small chapter of 2026.",
    val chapterText: String = "Across 2026, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.",
    val categories: List<String> = listOf("College", "Friends", "Health", "Work", "Family"),
    val supportingRecords: List<SupportingRecord> = emptyList(),
    val footerText: String = "Topics come from these records; grounded directly in what you recorded."
)

sealed class YearlyMemoryNavigationEvent {
    object NavigateBack : YearlyMemoryNavigationEvent()
    object NavigateToAllActivities : YearlyMemoryNavigationEvent()
    object NavigateToActivityDetails : YearlyMemoryNavigationEvent()
}

class YearlyMemoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(YearlyMemoryUiState())
    val uiState: StateFlow<YearlyMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<YearlyMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<YearlyMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.yearlyYearLabel.collectLatest { year ->
                _uiState.update { it.copy(yearTitle = year) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.yearlyChapterTitle.collectLatest { title ->
                _uiState.update { it.copy(chapterTitle = title) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.yearlyChapterText.collectLatest { text ->
                _uiState.update { it.copy(chapterText = text) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.yearlyCategories.collectLatest { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.yearlySupportingRecords.collectLatest { records ->
                _uiState.update {
                    it.copy(
                        supportingRecords = records,
                        infoText = "Source-grounded yearly memory across ${records.size} recorded entries."
                    )
                }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.yearlyFooterText.collectLatest { footer ->
                _uiState.update { it.copy(footerText = footer) }
            }
        }
    }

    fun onGenerateYearlyMemoryClicked() {
        DiaryActivityStore.generateYearlySummary(viewModelScope)
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateBack)
        }
    }

    fun onYearDropdownClicked() {
        onGenerateYearlyMemoryClicked()
    }

    fun onChapterCardClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onRecordClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateToActivityDetails)
        }
    }
}
