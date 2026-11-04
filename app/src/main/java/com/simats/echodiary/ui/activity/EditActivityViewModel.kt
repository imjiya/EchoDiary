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

data class EditActivityUiState(
    val title: String = "Lectures and project",
    val description: String = "Attended lectures and worked on a project.",
    val date: String = "Oct 5, 2026",
    val category: String = "College",
    val mood: String = "Not specified",
    val hasPhoto: Boolean = false,
    val entryId: Int = 0,
    val originalTitle: String = "Lectures and project",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

sealed class EditActivityNavigationEvent {
    object NavigateBack : EditActivityNavigationEvent()
    object NavigateToAllActivities : EditActivityNavigationEvent()
}

class EditActivityViewModel(
    private val entryRepository: EntryRepository = EchoDiaryApp.getEntryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditActivityUiState())
    val uiState: StateFlow<EditActivityUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<EditActivityNavigationEvent>()
    val navigationEvent: SharedFlow<EditActivityNavigationEvent> = _navigationEvent.asSharedFlow()

    fun setInitialDetails(
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
                originalTitle = if (title.isNotBlank()) title else it.originalTitle,
                description = if (description.isNotBlank()) description else it.description,
                date = if (date.isNotBlank()) date else it.date,
                category = if (category.isNotBlank()) category else it.category,
                mood = if (mood.isNotBlank()) mood else it.mood,
                entryId = if (id > 0) id else it.entryId
            )
        }
    }

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onDateChanged(newDate: String) {
        _uiState.update { it.copy(date = newDate) }
    }

    fun onCategoryChanged(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun onMoodChanged(newMood: String) {
        _uiState.update { it.copy(mood = newMood) }
    }

    fun onAddPhotoClicked() {
        _uiState.update { it.copy(hasPhoto = true) }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        val updatedTitle = state.title.trim().ifBlank { "Diary Entry" }
        val updatedDesc = state.description.trim()

        val updatedItem = DiaryEntryItem(
            id = state.entryId,
            title = updatedTitle,
            category = state.category.trim().ifBlank { "College" },
            date = state.date.trim().ifBlank { "Oct 11, 2026" },
            description = updatedDesc,
            icon = DiaryActivityStore.getCategoryIcon(state.category),
            mood = state.mood.trim().ifBlank { "Not specified" },
            hasPhoto = state.hasPhoto
        )

        // Save updated activity in memory of activities
        DiaryActivityStore.updateActivity(state.originalTitle, updatedItem)

        viewModelScope.launch {
            if (state.entryId > 0) {
                entryRepository.updateEntry(
                    id = state.entryId,
                    title = updatedTitle,
                    description = updatedDesc,
                    date = state.date,
                    category = state.category,
                    mood = state.mood
                )
            }
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateToAllActivities)
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateBack)
        }
    }
}
