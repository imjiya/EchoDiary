package com.simats.echodiary.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HelpAndSupportUiState(
    val faqItem1Expanded: Boolean = false,
    val faqItem2Expanded: Boolean = false
)

class HelpAndSupportViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HelpAndSupportUiState())
    val uiState: StateFlow<HelpAndSupportUiState> = _uiState.asStateFlow()

    fun toggleFaq1() {
        _uiState.value = _uiState.value.copy(faqItem1Expanded = !_uiState.value.faqItem1Expanded)
    }

    fun toggleFaq2() {
        _uiState.value = _uiState.value.copy(faqItem2Expanded = !_uiState.value.faqItem2Expanded)
    }
}
