package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EmptyStatesUiState(
    val title: String = "Empty states"
)

class EmptyStatesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmptyStatesUiState())
    val uiState: StateFlow<EmptyStatesUiState> = _uiState.asStateFlow()
}
