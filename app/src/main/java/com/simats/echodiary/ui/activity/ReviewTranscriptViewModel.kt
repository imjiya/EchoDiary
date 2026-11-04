package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.repository.EntryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewTranscriptUiState(
    val transcript: String = "Attended lectures and worked on a project.",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

sealed class ReviewTranscriptNavigationEvent {
    object NavigateBack : ReviewTranscriptNavigationEvent()
    object NavigateToVoiceRecording : ReviewTranscriptNavigationEvent()
    data class NavigateToActivityDetails(val transcript: String) : ReviewTranscriptNavigationEvent()
}

class ReviewTranscriptViewModel(
    initialTranscript: String = "Attended lectures and worked on a project.",
    private val entryRepository: EntryRepository = EchoDiaryApp.getEntryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReviewTranscriptUiState(
            transcript = initialTranscript.ifBlank { "Attended lectures and worked on a project." }
        )
    )
    val uiState: StateFlow<ReviewTranscriptUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ReviewTranscriptNavigationEvent>()
    val navigationEvent: SharedFlow<ReviewTranscriptNavigationEvent> = _navigationEvent.asSharedFlow()

    fun setInitialTranscript(text: String) {
        if (text.isNotBlank()) {
            _uiState.update { 
                it.copy(
                    transcript = text,
                    errorMessage = null
                ) 
            }
        }
    }

    fun onTranscriptChanged(newText: String) {
        _uiState.update { 
            it.copy(
                transcript = newText,
                errorMessage = null
            ) 
        }
    }

    fun onSaveClicked() {
        val currentText = _uiState.value.transcript.trim()
        if (currentText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Transcript cannot be empty.") }
            return
        }

        _uiState.update { 
            it.copy(
                isSaving = true, 
                errorMessage = null,
                statusMessage = "Saving entry..."
            ) 
        }

        viewModelScope.launch {
            val result = entryRepository.createVoiceEntry(currentText)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        statusMessage = "Entry saved successfully to your diary!"
                    )
                }
                _navigationEvent.emit(ReviewTranscriptNavigationEvent.NavigateToActivityDetails(currentText))
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Could not sync with server, saved locally."
                    )
                }
                // Emit navigation so user can still proceed to view activity details
                _navigationEvent.emit(ReviewTranscriptNavigationEvent.NavigateToActivityDetails(currentText))
            }
        }
    }

    fun onContinueToEntryClicked() {
        onSaveClicked()
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
