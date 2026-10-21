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

data class ReviewTranscriptUiState(
    val transcript: String = "Attended lectures and worked on a project."
)

sealed class ReviewTranscriptNavigationEvent {
    object NavigateBack : ReviewTranscriptNavigationEvent()
    object NavigateToVoiceRecording : ReviewTranscriptNavigationEvent()
    object NavigateToAllActivities : ReviewTranscriptNavigationEvent()
}

class ReviewTranscriptViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewTranscriptUiState())
    val uiState: StateFlow<ReviewTranscriptUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ReviewTranscriptNavigationEvent>()
    val navigationEvent: SharedFlow<ReviewTranscriptNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onTranscriptChanged(newText: String) {
        _uiState.update { it.copy(transcript = newText) }
    }

    fun onContinueToEntryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ReviewTranscriptNavigationEvent.NavigateToAllActivities)
        }
    }

    fun onRetakeClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ReviewTranscriptNavigationEvent.NavigateToVoiceRecording)
        }
    }

    fun onDiscardClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ReviewTranscriptNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ReviewTranscriptNavigationEvent.NavigateBack)
        }
    }
}
