package com.simats.echodiary.ui.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<OnboardingNavigationEvent>()
    val navigationEvent: SharedFlow<OnboardingNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onGetStartedClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToSecondOnboarding)
        }
    }

    fun onLogInClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToLogin)
        }
    }
}

sealed class OnboardingNavigationEvent {
    object NavigateToSecondOnboarding : OnboardingNavigationEvent()
    object NavigateToLogin : OnboardingNavigationEvent()
}
