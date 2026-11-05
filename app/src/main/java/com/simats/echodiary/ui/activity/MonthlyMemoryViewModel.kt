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

data class CategoryBreakdownItem(
    val name: String,
    val count: Int,
    val progress: Float = 1f
)

data class MonthlyMemoryUiState(
    val monthTitle: String = "October 2026",
    val infoText: String = "Source-grounded monthly memory from your recorded entries.",
    val diaryEntriesCount: Int = 5,
    val categoriesCount: Int = 5,
    val summaryTitle: String = "October, in your words",
    val summaryText: String = "This month, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.",
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val footerText: String = "Highlights: project work, lunch with friends, a Calm walk, presentation preparation and family time."
)

sealed class MonthlyMemoryNavigationEvent {
    object NavigateBack : MonthlyMemoryNavigationEvent()
    object NavigateToAllActivities : MonthlyMemoryNavigationEvent()
    object NavigateToActivityAndMood : MonthlyMemoryNavigationEvent()
    object NavigateToActivityDetails : MonthlyMemoryNavigationEvent()
}

class MonthlyMemoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MonthlyMemoryUiState())
    val uiState: StateFlow<MonthlyMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<MonthlyMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<MonthlyMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.monthlySummaryTitle.collectLatest { title ->
                _uiState.update { it.copy(summaryTitle = title) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.monthlySummaryText.collectLatest { text ->
                _uiState.update { it.copy(summaryText = text) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.monthlyCategoryBreakdown.collectLatest { breakdown ->
                _uiState.update { it.copy(categoryBreakdown = breakdown) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.monthlyEntriesCount.collectLatest { count ->
                _uiState.update {
                    it.copy(
                        diaryEntriesCount = count,
                        infoText = "Source-grounded monthly memory across $count recorded entries."
                    )
                }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.monthlyCategoriesCount.collectLatest { count ->
                _uiState.update { it.copy(categoriesCount = count) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.monthlyFooterText.collectLatest { footer ->
                _uiState.update { it.copy(footerText = footer) }
            }
        }
    }

    fun onGenerateMonthlyMemoryClicked() {
        DiaryActivityStore.generateMonthlySummary(viewModelScope)
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(MonthlyMemoryNavigationEvent.NavigateBack)
        }
    }

    fun onMonthDropdownClicked() {
        onGenerateMonthlyMemoryClicked()
    }

    fun onSummaryCardClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(MonthlyMemoryNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onEntryItemClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(MonthlyMemoryNavigationEvent.NavigateToAllActivities)
        }
    }
}
