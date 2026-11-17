package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
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

data class WeeklyMemoryUiState(
    val dateRangeText: String = DiaryActivityStore.weeklyMemoryDateRange.value,
    val title: String = DiaryActivityStore.weeklyMemoryTitle.value,
    val summaryText: String = DiaryActivityStore.weeklyMemoryNarrative.value,
    val highlights: List<String> = DiaryActivityStore.weeklyHighlights.value,
    val footerText: String = "Based on your ${DiaryActivityStore.activities.value.size} entries. Review before saving.",
    val photoUri: String? = DiaryActivityStore.weeklyMemoryPhotoUri.value,
    val hasPhoto: Boolean = !DiaryActivityStore.weeklyMemoryPhotoUri.value.isNullOrBlank()
)

sealed class WeeklyMemoryNavigationEvent {
    object NavigateBack : WeeklyMemoryNavigationEvent()
    object NavigateToMemories : WeeklyMemoryNavigationEvent()
    object NavigateToAllActivities : WeeklyMemoryNavigationEvent()
    object NavigateToActivityAndMood : WeeklyMemoryNavigationEvent()
    object NavigateToEditYourMemory : WeeklyMemoryNavigationEvent()
    object NavigateToMemoryDetails : WeeklyMemoryNavigationEvent()
}

class WeeklyMemoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyMemoryUiState())
    val uiState: StateFlow<WeeklyMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<WeeklyMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<WeeklyMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.weeklyMemoryTitle.collectLatest { title ->
                _uiState.update { it.copy(title = title) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.weeklyMemoryNarrative.collectLatest { narrative ->
                _uiState.update { it.copy(summaryText = narrative) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.weeklyHighlights.collectLatest { highlights ->
                _uiState.update { it.copy(highlights = highlights) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.weeklyMemoryDateRange.collectLatest { range ->
                _uiState.update { it.copy(dateRangeText = range) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                _uiState.update {
                    it.copy(footerText = "Based on your ${activities.size} entries. Review before saving.")
                }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.weeklyMemoryPhotoUri.collectLatest { photo ->
                _uiState.update {
                    it.copy(
                        photoUri = photo,
                        hasPhoto = !photo.isNullOrBlank()
                    )
                }
            }
        }
    }

    fun onSaveMemoryClicked() {
        viewModelScope.launch {
            try {
                EchoDiaryApp.getMemoryRepository().generateMemory(type = "weekly", save = true)
            } catch (_: Exception) {}
            _navigationEvent.emit(WeeklyMemoryNavigationEvent.NavigateToMemories)
        }
    }

    fun onEditSummaryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WeeklyMemoryNavigationEvent.NavigateToEditYourMemory)
        }
    }

    fun onShareClicked() {
        // Share action
    }

    fun onViewDetailsClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WeeklyMemoryNavigationEvent.NavigateToMemoryDetails)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WeeklyMemoryNavigationEvent.NavigateBack)
        }
    }
}
