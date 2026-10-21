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

data class DiaryEntryItem(
    val title: String,
    val category: String,
    val date: String,
    val description: String,
    val icon: String
)

data class AllActivitiesUiState(
    val searchQuery: String = "",
    val entries: List<DiaryEntryItem> = listOf(
        DiaryEntryItem("Lectures and project", "College", "Oct 5, 2026", "Attended lectures and worked on a project.", "🎓"),
        DiaryEntryItem("Lunch with friends", "Friends", "Oct 6, 2026", "Had lunch with friends after class.", "👥"),
        DiaryEntryItem("Evening walk", "Health", "Oct 7, 2026", "Went for an evening walk.", "❤️"),
        DiaryEntryItem("Presentation preparation", "Work", "Oct 8, 2026", "Prepared a presentation.", "💼"),
        DiaryEntryItem("Weekend with family", "Family", "Oct 10, 2026", "Spent time with family over the weekend.", "🏠")
    )
)

sealed class AllActivitiesNavigationEvent {
    object NavigateBack : AllActivitiesNavigationEvent()
}

class AllActivitiesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AllActivitiesUiState())
    val uiState: StateFlow<AllActivitiesUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AllActivitiesNavigationEvent>()
    val navigationEvent: SharedFlow<AllActivitiesNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AllActivitiesNavigationEvent.NavigateBack)
        }
    }
}
