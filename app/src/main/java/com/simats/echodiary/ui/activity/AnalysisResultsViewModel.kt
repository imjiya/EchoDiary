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

data class AnalysisItem(
    val title: String,
    val sublink: String,
    val icon: String
)

data class AnalysisResultsUiState(
    val headerSubtitle: String = "OCT 5–11 · BASED ON 5 ENTRIES",
    val title: String = "Grounded in your words.",
    val description: String = "Five categories, one entry each. Topics below come directly from your saved diary.",
    val items: List<AnalysisItem> = listOf(
        AnalysisItem("College · Lectures and project work", "View A1: Lectures and project · Oct 5, 2026", "🎓"),
        AnalysisItem("Friends · Lunch after class", "View A2: Lunch with friends · Oct 6, 2026", "👥"),
        AnalysisItem("Health · Evening walk · Calm recorded", "View A3: Evening walk · Oct 7, 2026", "❤️"),
        AnalysisItem("Work · Presentation preparation", "View A4: Presentation preparation · Oct 8, 2026", "💼"),
        AnalysisItem("Family · Time with family", "View A5: Weekend with family · Oct 10, 2026", "🏠")
    ),
    val moodEvidenceText: String = "Mood evidence: Calm on A3. No mood inferred for the other 4 entries."
)

sealed class AnalysisResultsNavigationEvent {
    object NavigateBack : AnalysisResultsNavigationEvent()
    object NavigateToWeeklyMemory : AnalysisResultsNavigationEvent()
    object NavigateToActivityDetails : AnalysisResultsNavigationEvent()
}

class AnalysisResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisResultsUiState())
    val uiState: StateFlow<AnalysisResultsUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AnalysisResultsNavigationEvent>()
    val navigationEvent: SharedFlow<AnalysisResultsNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onViewMemoryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AnalysisResultsNavigationEvent.NavigateToWeeklyMemory)
        }
    }

    fun onEntryItemClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AnalysisResultsNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AnalysisResultsNavigationEvent.NavigateBack)
        }
    }
}
