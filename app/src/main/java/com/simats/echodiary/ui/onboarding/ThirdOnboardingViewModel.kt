package com.simats.echodiary.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class ThirdOnboardingNavigationEvent {
    object NavigateToLogin : ThirdOnboardingNavigationEvent()
}

class ThirdOnboardingViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<ThirdOnboardingNavigationEvent>()
    val navigationEvent: SharedFlow<ThirdOnboardingNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onContinueClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ThirdOnboardingNavigationEvent.NavigateToLogin)
        }
    }

    fun onSkipClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ThirdOnboardingNavigationEvent.NavigateToLogin)
        }
    }
}
