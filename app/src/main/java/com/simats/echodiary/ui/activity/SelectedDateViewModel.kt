package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SelectedDateUiState(
    val dayOfWeek: String = "",
    val fullDate: String = "",
    val entrySummary: String = "",
    val entryItem: DiaryEntryItem? = null,
    val hasEntry: Boolean = false
)

sealed class SelectedDateNavigationEvent {
    object NavigateBack : SelectedDateNavigationEvent()
    object NavigateToActivityDetails : SelectedDateNavigationEvent()
    object NavigateToAddEntry : SelectedDateNavigationEvent()
}

class SelectedDateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SelectedDateUiState())
    val uiState: StateFlow<SelectedDateUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SelectedDateNavigationEvent>()
    val navigationEvent: SharedFlow<SelectedDateNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.selectedActivity.collectLatest { item ->
                if (item != null) {
                    val dayName = try {
                        val parsed = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).parse(item.date)
                        if (parsed != null) SimpleDateFormat("EEEE", Locale.getDefault()).format(parsed).uppercase(Locale.ROOT) else "DIARY ENTRY"
                    } catch (_: Exception) {
                        "DIARY ENTRY"
                    }
                    _uiState.update {
                        it.copy(
                            dayOfWeek = dayName,
                            fullDate = item.date,
                            entrySummary = "1 diary entry · ${item.category}",
                            entryItem = item,
                            hasEntry = true
                        )
                    }
                } else {
                    val today = Date()
                    val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(today).uppercase(Locale.ROOT)
                    val fullDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(today)
                    _uiState.update {
                        it.copy(
                            dayOfWeek = dayOfWeek,
                            fullDate = fullDate,
                            entrySummary = "No diary entry for this date",
                            entryItem = null,
                            hasEntry = false
                        )
                    }
                }
            }
        }
    }

    fun onViewActivityDetailsClicked() {
        val item = _uiState.value.entryItem
        if (item != null) {
            DiaryActivityStore.selectActivity(item)
        }
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateToActivityDetails)
        }
    }

    fun onAddEntryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateToAddEntry)
        }
    }

    fun onBackToCalendarClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SelectedDateNavigationEvent.NavigateBack)
        }
    }
}
