package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditYourMemoryUiState(
    val dateRangeText: String = "Weekly memory · Oct 5–11, 2026",
    val memoryTitle: String = "A week of progress and connection.",
    val weeklyNarrative: String = "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.",
    val sourceMaterialText: String = "Source material: 5 entries across College, Friends, Health, Work and Family."
)

class EditYourMemoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditYourMemoryUiState())
    val uiState: StateFlow<EditYourMemoryUiState> = _uiState.asStateFlow()

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(memoryTitle = title) }
    }

    fun onNarrativeChanged(narrative: String) {
        _uiState.update { it.copy(weeklyNarrative = narrative) }
    }
}
