package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.repository.EntryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActivityDetailsUiState(
    val date: String = "Monday, Oct 5, 2026",
    val title: String = "Diary Entry",
    val category: String = "College",
    val description: String = "Attended lectures and worked on a project.",
    val mood: String = "Not specified",
    val photoStatus: String = "No photo attached",
    val entryIdText: String = "Entry · Stored in memory of activities",
    val entryId: Int = 0
)

sealed class ActivityDetailsNavigationEvent {
    object NavigateBack : ActivityDetailsNavigationEvent()
    object NavigateToEdit : ActivityDetailsNavigationEvent()
    object NavigateToDelete : ActivityDetailsNavigationEvent()
}

class ActivityDetailsViewModel(
    private val entryRepository: EntryRepository = EchoDiaryApp.getEntryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDetailsUiState())
    val uiState: StateFlow<ActivityDetailsUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ActivityDetailsNavigationEvent>()
    val navigationEvent: SharedFlow<ActivityDetailsNavigationEvent> = _navigationEvent.asSharedFlow()

    fun setEntryDetails(
        title: String,
        description: String,
        date: String,
        category: String,
        mood: String = "Not specified",
        id: Int = 0
    ) {
        _uiState.update {
            it.copy(
                title = if (title.isNotBlank()) title else it.title,
                description = if (description.isNotBlank()) description else it.description,
                date = if (date.isNotBlank()) date else it.date,
                category = if (category.isNotBlank()) category else it.category,
                mood = if (mood.isNotBlank()) mood else it.mood,
                entryId = if (id > 0) id else it.entryId,
                entryIdText = "Entry · Stored in memory of activities"
            )
        }
    }

    fun setDescription(description: String) {
        if (description.isNotBlank()) {
            val titleSnippet = if (description.length > 35) {
                description.substring(0, 35).trim() + "..."
            } else {
                description
            }
            _uiState.update { 
                it.copy(
                    description = description,
                    title = titleSnippet
                ) 
            }
        }
    }

    fun onEditClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateToEdit)
        }
    }

    fun onDeleteClicked() {
        val state = _uiState.value
        DiaryActivityStore.deleteActivity(state.entryId, state.title)
        viewModelScope.launch {
            if (state.entryId > 0) {
                entryRepository.deleteEntry(state.entryId)
            }
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateToDelete)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateBack)
        }
    }
}
