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

data class ActivityDetailsUiState(
    val date: String = "Monday, Oct 5, 2026",
    val title: String = "Lectures and project",
    val category: String = "College",
    val description: String = "Attended lectures and worked on a project.",
    val mood: String = "Not specified",
    val photoStatus: String = "No photo attached",
    val entryId: String = "Entry A1 · Included in your Oct 5–11 memory"
)

sealed class ActivityDetailsNavigationEvent {
    object NavigateBack : ActivityDetailsNavigationEvent()
    object NavigateToEdit : ActivityDetailsNavigationEvent()
    object NavigateToDelete : ActivityDetailsNavigationEvent()
}

class ActivityDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDetailsUiState())
    val uiState: StateFlow<ActivityDetailsUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ActivityDetailsNavigationEvent>()
    val navigationEvent: SharedFlow<ActivityDetailsNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onEditClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateToEdit)
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateToDelete)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateBack)
        }
    }
}
