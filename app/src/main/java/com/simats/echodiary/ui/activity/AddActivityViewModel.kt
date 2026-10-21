package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class AddActivityNavigationEvent {
    object NavigateBack : AddActivityNavigationEvent()
    object NavigateToVoiceRecording : AddActivityNavigationEvent()
    object NavigateToWriteDiaryEntry : AddActivityNavigationEvent()
    object NavigateToFilterActivities : AddActivityNavigationEvent()
}

class AddActivityViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<AddActivityNavigationEvent>()
    val navigationEvent: SharedFlow<AddActivityNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AddActivityNavigationEvent.NavigateBack)
        }
    }

    fun onRecordByVoiceClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AddActivityNavigationEvent.NavigateToVoiceRecording)
        }
    }

    fun onWriteManuallyClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AddActivityNavigationEvent.NavigateToWriteDiaryEntry)
        }
    }

    fun onCategoryButtonClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AddActivityNavigationEvent.NavigateToFilterActivities)
        }
    }
}
