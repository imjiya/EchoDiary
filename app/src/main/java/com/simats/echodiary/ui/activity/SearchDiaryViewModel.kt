package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchDiaryUiState(
    val searchQuery: String = "project",
    val searchResultCount: Int = 1,
    val resultItem: DiaryEntryItem = DiaryEntryItem("Lectures and project", "College", "Oct 5, 2026", "Attended lectures and worked on a project.", "🎓"),
    val recentSearches: List<String> = listOf("project", "College")
)

sealed class SearchDiaryNavigationEvent {
    object NavigateBack : SearchDiaryNavigationEvent()
    object NavigateToActivityDetails : SearchDiaryNavigationEvent()
}

class SearchDiaryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchDiaryUiState())
    val uiState: StateFlow<SearchDiaryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SearchDiaryNavigationEvent>()
    val navigationEvent: SharedFlow<SearchDiaryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onClearQuery() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun onClearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun onViewActivityDetailsClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SearchDiaryNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SearchDiaryNavigationEvent.NavigateBack)
        }
    }
}
