package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.util.EnglishTranslator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VoiceRecordingUiState(
    val isRecording: Boolean = true,
    val isPaused: Boolean = false,
    val timerText: String = "00:00",
    val spokenText: String = "",
    val translatedText: String = "",
    val recordedText: String = "", // Holds translated text for compatibility
    val detectedLanguage: String = "",
    val isTranslating: Boolean = false,
    val statusText: String = "Listening to your voice...",
    val errorMessage: String? = null
)

sealed class VoiceRecordingNavigationEvent {
    object NavigateBack : VoiceRecordingNavigationEvent()
    data class NavigateToReviewTranscript(val transcript: String) : VoiceRecordingNavigationEvent()
}

class VoiceRecordingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceRecordingUiState())
    val uiState: StateFlow<VoiceRecordingUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<VoiceRecordingNavigationEvent>()
    val navigationEvent: SharedFlow<VoiceRecordingNavigationEvent> = _navigationEvent.asSharedFlow()

    private var translationJob: Job? = null

    fun onPauseClicked() {
        _uiState.update { 
            it.copy(
                isPaused = true,
                statusText = "Recording paused."
            ) 
        }
    }

    fun onResumeClicked() {
        _uiState.update { 
            it.copy(
                isPaused = false,
                statusText = "Listening to your voice..."
            ) 
        }
    }

    fun onSpeechRecognized(rawSpeech: String) {
        if (rawSpeech.isBlank()) return

        _uiState.update {
            it.copy(
                spokenText = rawSpeech,
                isTranslating = true,
                statusText = "Translating speech to proper English..."
            )
        }

        translationJob?.cancel()
        translationJob = viewModelScope.launch {
            try {
                val result = EnglishTranslator.translateToProperEnglish(rawSpeech)
                _uiState.update {
                    it.copy(
                        spokenText = rawSpeech,
                        translatedText = result.translatedEnglish,
                        recordedText = result.translatedEnglish,
                        detectedLanguage = result.detectedLanguage,
                        isTranslating = false,
                        statusText = "Live speech recorded and translated."
                    )
                }
            } catch (e: Exception) {
                val fallback = EnglishTranslator.polishEnglish(rawSpeech)
                _uiState.update {
                    it.copy(
                        spokenText = rawSpeech,
                        translatedText = fallback,
                        recordedText = fallback,
                        detectedLanguage = "English",
                        isTranslating = false,
                        statusText = "Live speech recorded."
                    )
                }
            }
        }
    }

    fun onPartialSpeech(partialText: String) {
        if (partialText.isNotBlank()) {
            _uiState.update {
                it.copy(
                    spokenText = partialText,
                    statusText = "Listening..."
                )
            }
        }
    }

    fun onTimerTick(seconds: Int) {
        val minutesStr = (seconds / 60).toString().padStart(2, '0')
        val secondsStr = (seconds % 60).toString().padStart(2, '0')
        _uiState.update { it.copy(timerText = "$minutesStr:$secondsStr") }
    }

    fun onStopClicked() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isRecording = false,
                    statusText = "Recording completed."
                ) 
            }

            val transcript = when {
                _uiState.value.translatedText.isNotBlank() -> _uiState.value.translatedText
                _uiState.value.spokenText.isNotBlank() -> EnglishTranslator.polishEnglish(_uiState.value.spokenText)
                _uiState.value.recordedText.isNotBlank() -> _uiState.value.recordedText
                else -> "Attended lectures and worked on a project."
            }

            _navigationEvent.emit(VoiceRecordingNavigationEvent.NavigateToReviewTranscript(transcript))
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

    fun setStatus(status: String) {
        _uiState.update { it.copy(statusText = status) }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(errorMessage = error) }
    }
}
