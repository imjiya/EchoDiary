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

data class VoiceRecordingUiState(
    val isPaused: Boolean = false,
    val timerText: String = "00:24"
)

sealed class VoiceRecordingNavigationEvent {
    object NavigateBack : VoiceRecordingNavigationEvent()
    object NavigateToReviewTranscript : VoiceRecordingNavigationEvent()
}

class VoiceRecordingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceRecordingUiState())
    val uiState: StateFlow<VoiceRecordingUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<VoiceRecordingNavigationEvent>()
    val navigationEvent: SharedFlow<VoiceRecordingNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onPauseClicked() {
        _uiState.update { it.copy(isPaused = true) }
    }

    fun onResumeClicked() {
        _uiState.update { it.copy(isPaused = false) }
    }

    fun onStopClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(VoiceRecordingNavigationEvent.NavigateToReviewTranscript)
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(VoiceRecordingNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(VoiceRecordingNavigationEvent.NavigateBack)
        }
    }
}
