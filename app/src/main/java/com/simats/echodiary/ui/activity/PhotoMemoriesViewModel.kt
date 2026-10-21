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

data class PhotoMemoriesUiState(
    val headerTitle: String = "A moment, in a picture.",
    val headerSubtitle: String = "Photos connected to your diary entries.",
    val dateHeader: String = "October 6, 2026 · 1 photo",
    val imageCaption: String = "Lunch with friends · A2",
    val imageSubcaption: String = "Illustrative lunch photo associated with this entry.",
    val entryTitle: String = "Lunch with friends",
    val entrySubtitle: String = "Friends · Oct 6, 2026",
    val entryDescription: String = "Had lunch with friends after class.",
    val footerText: String = "Only this recorded lunch is represented."
)

sealed class PhotoMemoriesNavigationEvent {
    object NavigateBack : PhotoMemoriesNavigationEvent()
    object NavigateToActivityDetails : PhotoMemoriesNavigationEvent()
}

class PhotoMemoriesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoMemoriesUiState())
    val uiState: StateFlow<PhotoMemoriesUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<PhotoMemoriesNavigationEvent>()
    val navigationEvent: SharedFlow<PhotoMemoriesNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(PhotoMemoriesNavigationEvent.NavigateBack)
        }
    }

    fun onEntryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(PhotoMemoriesNavigationEvent.NavigateToActivityDetails)
        }
    }
}
