package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrivacyAndDataUiState(
    val exportAvailable: Boolean = true
)

class PrivacyAndDataViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrivacyAndDataUiState())
    val uiState: StateFlow<PrivacyAndDataUiState> = _uiState.asStateFlow()
}
