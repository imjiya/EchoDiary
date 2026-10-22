package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DialogsAndFeedbackUiState(
    val title: String = "Dialogs & feedback"
)

class DialogsAndFeedbackViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DialogsAndFeedbackUiState())
    val uiState: StateFlow<DialogsAndFeedbackUiState> = _uiState.asStateFlow()
}
