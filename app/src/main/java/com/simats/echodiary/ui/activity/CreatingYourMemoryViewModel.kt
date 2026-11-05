package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatingYourMemoryUiState(
    val title: String = "Connecting your week.",
    val subtitle: String = "Bringing together your 5 entries from Oct 5–11,\nusing only what you recorded.",
    val progress: Float = 0.85f,
    val progressPercentageText: String = "85% · Drafting your source-based memory summary",
    val checklist: List<Pair<String, Boolean>> = listOf(
        "Gathering 5 diary entries" to true,
        "Identifying recorded categories" to true,
        "Drafting your weekly narrative" to true
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

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                val distinctCats = activities.map { it.category }.distinct()
                _uiState.update {
                    it.copy(
                        subtitle = "Bringing together your ${activities.size} entries,\nusing only what you recorded.",
                        checklist = listOf(
                            "Gathering ${activities.size} diary entries" to true,
                            "Identifying ${distinctCats.size} recorded categories" to true,
                            "Drafting your weekly narrative" to true
                        )
                    )
                }
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(CreatingYourMemoryNavigationEvent.NavigateBack)
        }
    }
}
