package com.simats.echodiary.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class FourthOnboardingNavigationEvent {
    object NavigateToCreateAccount : FourthOnboardingNavigationEvent()
}

class FourthOnboardingViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<FourthOnboardingNavigationEvent>()
    val navigationEvent: SharedFlow<FourthOnboardingNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onContinueClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(FourthOnboardingNavigationEvent.NavigateToCreateAccount)
        }
    }

    fun onSkipClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(FourthOnboardingNavigationEvent.NavigateToCreateAccount)
        }
    }
}
