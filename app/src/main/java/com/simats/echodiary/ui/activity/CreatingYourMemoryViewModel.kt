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

data class CreatingYourMemoryUiState(
    val title: String = "Connecting your week.",
    val subtitle: String = "Bringing together your 5 entries from Oct 5–11,\nusing only what you recorded.",
    val progress: Float = 0.65f,
    val progressPercentageText: String = "65% · Drafting a source-based summary",
    val checklist: List<Pair<String, Boolean>> = listOf(
        "Gathering 5 diary entries" to true,
        "Identifying recorded categories" to true,
        "Drafting your weekly narrative" to false
    )
)

sealed class CreatingYourMemoryNavigationEvent {
    object NavigateBack : CreatingYourMemoryNavigationEvent()
    object NavigateToWeeklySummary : CreatingYourMemoryNavigationEvent()
}

class CreatingYourMemoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreatingYourMemoryUiState())
    val uiState: StateFlow<CreatingYourMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CreatingYourMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<CreatingYourMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(CreatingYourMemoryNavigationEvent.NavigateBack)
        }
    }
}
