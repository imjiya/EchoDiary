package com.simats.echodiary.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _userName = MutableStateFlow("Jiya")
    val userName: StateFlow<String> = _userName.asStateFlow()
}
