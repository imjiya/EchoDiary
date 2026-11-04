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

data class DiaryEntryItem(
    val title: String,
    val category: String,
    val date: String,
    val description: String,
    val icon: String,
    val id: Int = 0,
    val mood: String = "Not specified",
    val hasPhoto: Boolean = false
)

data class AllActivitiesUiState(
    val searchQuery: String = "",
    val entries: List<DiaryEntryItem> = emptyList()
)

sealed class AllActivitiesNavigationEvent {
    object NavigateBack : AllActivitiesNavigationEvent()
}

class AllActivitiesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AllActivitiesUiState())
    val uiState: StateFlow<AllActivitiesUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AllActivitiesNavigationEvent>()
    val navigationEvent: SharedFlow<AllActivitiesNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { allActivities ->
                val query = _uiState.value.searchQuery.trim()
                val filtered = if (query.isEmpty()) {
                    allActivities
                } else {
                    allActivities.filter {
                        it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
                    }
                }
                _uiState.update { it.copy(entries = filtered) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        val allActivities = DiaryActivityStore.activities.value
        val cleanQuery = query.trim()
        val filtered = if (cleanQuery.isEmpty()) {
            allActivities
        } else {
            allActivities.filter {
                it.title.contains(cleanQuery, ignoreCase = true) ||
                it.description.contains(cleanQuery, ignoreCase = true) ||
                it.category.contains(cleanQuery, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(entries = filtered) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AllActivitiesNavigationEvent.NavigateBack)
        }
    }
}
