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
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToHome)
        }
    }

    fun onLogInClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToHome)
        }
    }
}

sealed class OnboardingNavigationEvent {
    object NavigateToHome : OnboardingNavigationEvent()
}
