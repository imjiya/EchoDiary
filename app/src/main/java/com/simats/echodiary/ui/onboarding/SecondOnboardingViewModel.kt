package com.simats.echodiary.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class SecondOnboardingNavigationEvent {
    object NavigateToThirdOnboarding : SecondOnboardingNavigationEvent()
    object NavigateToLogin : SecondOnboardingNavigationEvent()
}

class SecondOnboardingViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SecondOnboardingNavigationEvent>()
    val navigationEvent: SharedFlow<SecondOnboardingNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onContinueClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SecondOnboardingNavigationEvent.NavigateToThirdOnboarding)
        }
    }

    fun onSkipClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SecondOnboardingNavigationEvent.NavigateToLogin)
        }
    }
}
