package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.repository.EntryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WriteDiaryEntryUiState(
    val title: String = "",
    val description: String = "",
    val date: String = "Oct 5, 2026",
    val category: String = "College",
    val mood: String = "Good",
    val hasPhoto: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

sealed class WriteDiaryEntryNavigationEvent {
    object NavigateBack : WriteDiaryEntryNavigationEvent()
    data class NavigateToActivityDetails(
        val title: String,
        val description: String,
        val date: String,
        val category: String
    ) : WriteDiaryEntryNavigationEvent()
    object NavigateToChooseCategory : WriteDiaryEntryNavigationEvent()
    object NavigateToCalendar : WriteDiaryEntryNavigationEvent()
}

class WriteDiaryEntryViewModel(
    private val entryRepository: EntryRepository = EchoDiaryApp.getEntryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteDiaryEntryUiState())
    val uiState: StateFlow<WriteDiaryEntryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<WriteDiaryEntryNavigationEvent>()
    val navigationEvent: SharedFlow<WriteDiaryEntryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update { it.copy(description = newDescription, errorMessage = null) }
    }

    fun onDateChanged(newDate: String) {
        _uiState.update { it.copy(date = newDate, errorMessage = null) }
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

    fun onCategoryFieldClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WriteDiaryEntryNavigationEvent.NavigateToChooseCategory)
        }
    }

    fun onCalendarClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WriteDiaryEntryNavigationEvent.NavigateToCalendar)
        }
    }

    fun onSaveEntryClicked() {
        val title = _uiState.value.title.trim()
        val desc = _uiState.value.description.trim()

        if (title.isBlank() && desc.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a title or description.") }
            return
        }

        _uiState.update { 
            it.copy(
                isSaving = true, 
                errorMessage = null,
                statusMessage = "Saving diary entry..."
            ) 
        }

        viewModelScope.launch {
            val result = entryRepository.createManualEntry(
                title = title,
                description = desc,
                date = _uiState.value.date,
                category = _uiState.value.category,
                mood = _uiState.value.mood
            )

            val displayTitle = if (title.isNotBlank()) title else desc.take(40).trim()
            val displayDesc = if (desc.isNotBlank()) desc else displayTitle

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        statusMessage = "Diary entry stored successfully!"
                    )
                }
                _navigationEvent.emit(
                    WriteDiaryEntryNavigationEvent.NavigateToActivityDetails(
                        title = displayTitle,
                        description = displayDesc,
                        date = _uiState.value.date,
                        category = _uiState.value.category
                    )
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Failed to sync to server, saving locally."
                    )
                }
                _navigationEvent.emit(
                    WriteDiaryEntryNavigationEvent.NavigateToActivityDetails(
                        title = displayTitle,
                        description = displayDesc,
                        date = _uiState.value.date,
                        category = _uiState.value.category
                    )
                )
            }
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WriteDiaryEntryNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(WriteDiaryEntryNavigationEvent.NavigateBack)
        }
    }
}
