package com.simats.echodiary.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SplashViewModel : ViewModel() {

    private val _navigateToOnboarding = MutableSharedFlow<Unit>()
    val navigateToOnboarding: SharedFlow<Unit> = _navigateToOnboarding.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(2500)
            _navigateToOnboarding.emit(Unit)
        }
    }
}
