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

data class SearchDiaryUiState(
    val searchQuery: String = "",
    val searchResultCount: Int = 0,
    val results: List<DiaryEntryItem> = emptyList(),
    val recentSearches: List<String> = emptyList()
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

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest {
                performFilter(_uiState.value.searchQuery)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        performFilter(query)
    }

    fun onClearQuery() {
        _uiState.update { it.copy(searchQuery = "") }
        performFilter("")
    }

    fun onClearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    private fun performFilter(query: String) {
        val q = query.trim()
        val allEntries = DiaryActivityStore.activities.value
        val matches = if (q.isEmpty()) {
            emptyList()
        } else {
            allEntries.filter {
                it.title.contains(q, ignoreCase = true) ||
                it.description.contains(q, ignoreCase = true) ||
                it.category.contains(q, ignoreCase = true) ||
                it.mood.contains(q, ignoreCase = true)
            }
        }
        _uiState.update {
            it.copy(
                results = matches,
                searchResultCount = matches.size
            )
        }
    }

    fun onSelectEntry(entry: DiaryEntryItem) {
        DiaryActivityStore.selectActivity(entry)
        val q = _uiState.value.searchQuery.trim()
        if (q.isNotEmpty()) {
            val updatedSearches = (_uiState.value.recentSearches.filterNot { it.equals(q, ignoreCase = true) } + q).takeLast(5)
            _uiState.update { it.copy(recentSearches = updatedSearches) }
        }
        viewModelScope.launch {
            _navigationEvent.emit(SearchDiaryNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onViewActivityDetailsClicked() {
        val firstMatch = _uiState.value.results.firstOrNull() ?: DiaryActivityStore.selectedActivity.value
        if (firstMatch != null) {
            DiaryActivityStore.selectActivity(firstMatch)
        }
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
