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

data class EditActivityUiState(
    val title: String = "Lectures and project",
    val description: String = "Attended lectures and worked on a project.",
    val date: String = "Oct 5, 2026",
    val category: String = "College",
    val mood: String = "Not specified",
    val hasPhoto: Boolean = false
)

sealed class EditActivityNavigationEvent {
    object NavigateBack : EditActivityNavigationEvent()
    object NavigateToActivityDetails : EditActivityNavigationEvent()
}

class EditActivityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditActivityUiState())
    val uiState: StateFlow<EditActivityUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<EditActivityNavigationEvent>()
    val navigationEvent: SharedFlow<EditActivityNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onDateChanged(newDate: String) {
        _uiState.update { it.copy(date = newDate) }
    }

    fun onCategoryChanged(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun onMoodChanged(newMood: String) {
        _uiState.update { it.copy(mood = newMood) }
    }

    fun onAddPhotoClicked() {
        _uiState.update { it.copy(hasPhoto = true) }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateBack)
        }
    }
}
