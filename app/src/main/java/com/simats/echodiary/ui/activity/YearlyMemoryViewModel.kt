package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
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

data class SupportingRecord(
    val title: String,
    val date: String
)

data class YearlyMemoryUiState(
    val yearTitle: String = DiaryActivityStore.yearlyYearLabel.value,
    val infoText: String = "Source-grounded yearly memory from your recorded entries.",
    val chapterTitle: String = DiaryActivityStore.yearlyChapterTitle.value,
    val chapterText: String = DiaryActivityStore.yearlyChapterText.value,
    val categories: List<String> = DiaryActivityStore.yearlyCategories.value,
    val supportingRecords: List<SupportingRecord> = DiaryActivityStore.yearlySupportingRecords.value,
    val entries: List<DiaryEntryItem> = emptyList(),
    val footerText: String = DiaryActivityStore.yearlyFooterText.value
)

sealed class YearlyMemoryNavigationEvent {
    object NavigateBack : YearlyMemoryNavigationEvent()
    object NavigateToAllActivities : YearlyMemoryNavigationEvent()
    object NavigateToActivityDetails : YearlyMemoryNavigationEvent()
    object NavigateToCreatingMemory : YearlyMemoryNavigationEvent()
}

class YearlyMemoryViewModel(
    scope: CoroutineScope? = null
) : ViewModel() {
    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private val _uiState = MutableStateFlow(YearlyMemoryUiState())
    val uiState: StateFlow<YearlyMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<YearlyMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<YearlyMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        executionScope.launch {
            DiaryActivityStore.yearlyYearLabel.collectLatest { year ->
                _uiState.update { it.copy(yearTitle = year) }
            }
        }
        executionScope.launch {
            DiaryActivityStore.yearlyChapterTitle.collectLatest { title ->
                _uiState.update { it.copy(chapterTitle = title) }
            }
        }
        executionScope.launch {
            DiaryActivityStore.yearlyChapterText.collectLatest { text ->
                _uiState.update { it.copy(chapterText = text) }
            }
        }
        executionScope.launch {
            DiaryActivityStore.yearlyCategories.collectLatest { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
        executionScope.launch {
            DiaryActivityStore.yearlySupportingRecords.collectLatest { records ->
                _uiState.update {
                    it.copy(
                        supportingRecords = records,
                        infoText = "Source-grounded yearly memory across ${records.size} recorded entries."
                    )
                }
            }
        }
        executionScope.launch {
            DiaryActivityStore.yearlyFooterText.collectLatest { footer ->
                _uiState.update { it.copy(footerText = footer) }
            }
        }
        executionScope.launch {
            DiaryActivityStore.activities.collectLatest { list ->
                _uiState.update { it.copy(entries = list) }
            }
        }
    }

    fun onEntryClicked(entry: DiaryEntryItem) {
        DiaryActivityStore.selectActivity(entry)
        executionScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onGenerateYearlyMemoryClicked() {
        DiaryActivityStore.setGeneratingMemoryType("yearly")
        executionScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateToCreatingMemory)
        }
    }

    fun onShareClicked(context: android.content.Context) {
        val state = _uiState.value
        com.simats.echodiary.util.MemoryImageShareHelper.shareMemoryAsImage(
            context = context,
            title = state.chapterTitle,
            dateRange = state.yearTitle,
            narrative = state.chapterText,
            highlights = state.categories
        )
    }

    fun onBackClicked() {
        executionScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateBack)
        }
    }

    fun onYearDropdownClicked() {
        onGenerateYearlyMemoryClicked()
    }

    fun onChapterCardClicked() {
        executionScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onRecordClicked() {
        executionScope.launch {
            _navigationEvent.emit(YearlyMemoryNavigationEvent.NavigateToActivityDetails)
        }
    }
}
