package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.repository.EntryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditActivityUiState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val category: String = "College",
    val mood: String = "Not specified",
    val hasPhoto: Boolean = false,
    val photoUri: String? = null,
    val entryId: Int = 0,
    val originalTitle: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

sealed class EditActivityNavigationEvent {
    object NavigateBack : EditActivityNavigationEvent()
    object NavigateToAllActivities : EditActivityNavigationEvent()
}

class EditActivityViewModel(
    private val entryRepository: EntryRepository? = null,
    scope: CoroutineScope? = null
) : ViewModel() {

    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private fun getRepository(): EntryRepository? {
        return entryRepository ?: try {
            EchoDiaryApp.getEntryRepository()
        } catch (_: Exception) {
            null
        }
    }

    private val _uiState = MutableStateFlow(EditActivityUiState())
    val uiState: StateFlow<EditActivityUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<EditActivityNavigationEvent>()
    val navigationEvent: SharedFlow<EditActivityNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        val current = DiaryActivityStore.selectedActivity.value
        if (current != null) {
            setInitialDetails(
                title = current.title,
                description = current.description,
                date = current.date,
                category = current.category,
                mood = current.mood,
                id = current.id,
                photoUri = current.photoUri,
                hasPhoto = current.hasPhoto
            )
        }
    }

    fun setInitialDetails(
        title: String,
        description: String,
        date: String,
        category: String,
        mood: String = "Not specified",
        id: Int = 0,
        photoUri: String? = null,
        hasPhoto: Boolean = false
    ) {
        val selected = DiaryActivityStore.selectedActivity.value
        val resolvedPhotoUri = photoUri ?: selected?.photoUri
        val hasAttachedPhoto = hasPhoto || !resolvedPhotoUri.isNullOrBlank() || selected?.hasPhoto == true
        _uiState.update {
            it.copy(
                title = if (title.isNotBlank()) title else it.title,
                originalTitle = if (title.isNotBlank()) title else it.originalTitle,
                description = if (description.isNotBlank()) description else it.description,
                date = if (date.isNotBlank()) date else it.date,
                category = if (category.isNotBlank()) category else it.category,
                mood = if (mood.isNotBlank()) mood else it.mood,
                entryId = if (id > 0) id else it.entryId,
                hasPhoto = hasAttachedPhoto,
                photoUri = resolvedPhotoUri
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

    fun onPhotoSelected(uri: String) {
        _uiState.update { it.copy(photoUri = uri, hasPhoto = true) }
    }

    fun onRemovePhoto() {
        _uiState.update { it.copy(photoUri = null, hasPhoto = false) }
    }

    fun onPermissionDenied(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        val updatedTitle = state.title.trim().ifBlank { "Diary Entry" }
        val updatedDesc = state.description.trim()
        val hasAttachedPhoto = state.hasPhoto || !state.photoUri.isNullOrBlank()

        val updatedItem = DiaryEntryItem(
            id = state.entryId,
            title = updatedTitle,
            category = state.category.trim().ifBlank { "College" },
            date = state.date.trim().ifBlank {
                java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            },
            description = updatedDesc,
            icon = DiaryActivityStore.getCategoryIcon(state.category),
            mood = state.mood.trim().ifBlank { "Not specified" },
            hasPhoto = hasAttachedPhoto,
            photoUri = if (hasAttachedPhoto) state.photoUri else null
        )

        // Save updated activity in memory of activities
        DiaryActivityStore.updateActivity(state.originalTitle, updatedItem)
        DiaryActivityStore.selectActivity(updatedItem)

        executionScope.launch {
            if (state.entryId > 0) {
                getRepository()?.updateEntry(
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

    fun onDeleteClicked() {
        val state = _uiState.value
        DiaryActivityStore.deleteActivity(state.entryId, state.originalTitle)
        executionScope.launch {
            if (state.entryId > 0) {
                getRepository()?.deleteEntry(state.entryId)
            }
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateToAllActivities)
        }
    }

    fun onCancelClicked() {
        executionScope.launch {
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        executionScope.launch {
            _navigationEvent.emit(EditActivityNavigationEvent.NavigateBack)
        }
    }
}
