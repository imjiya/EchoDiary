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

import kotlinx.coroutines.flow.collectLatest

data class ActivityDetailsUiState(
    val date: String = "",
    val title: String = "",
    val category: String = "Diary",
    val description: String = "",
    val mood: String = "Not specified",
    val photoStatus: String = "No photo attached",
    val hasPhoto: Boolean = false,
    val photoUri: String? = null,
    val entryIdText: String = "Entry · Stored in memory of activities",
    val entryId: Int = 0
)

sealed class ActivityDetailsNavigationEvent {
    object NavigateBack : ActivityDetailsNavigationEvent()
    object NavigateToEdit : ActivityDetailsNavigationEvent()
    object NavigateToDelete : ActivityDetailsNavigationEvent()
}

class ActivityDetailsViewModel(
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

    private val _uiState = MutableStateFlow(ActivityDetailsUiState())
    val uiState: StateFlow<ActivityDetailsUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ActivityDetailsNavigationEvent>()
    val navigationEvent: SharedFlow<ActivityDetailsNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        val current = DiaryActivityStore.selectedActivity.value
        if (current != null) {
            setEntryDetails(
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
        executionScope.launch {
            DiaryActivityStore.selectedActivity.collectLatest { item ->
                if (item != null) {
                    setEntryDetails(
                        title = item.title,
                        description = item.description,
                        date = item.date,
                        category = item.category,
                        mood = item.mood,
                        id = item.id,
                        photoUri = item.photoUri,
                        hasPhoto = item.hasPhoto
                    )
                }
            }
        }
    }

    fun setEntryDetails(
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
        val photoStatusText = if (hasAttachedPhoto) "1 photo attached" else "No photo attached"
        _uiState.update {
            it.copy(
                title = if (title.isNotBlank()) title else it.title,
                description = if (description.isNotBlank()) description else it.description,
                date = if (date.isNotBlank()) date else it.date,
                category = if (category.isNotBlank()) category else it.category,
                mood = if (mood.isNotBlank()) mood else it.mood,
                entryId = if (id > 0) id else it.entryId,
                hasPhoto = hasAttachedPhoto,
                photoUri = resolvedPhotoUri,
                photoStatus = photoStatusText,
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
        executionScope.launch {
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateToEdit)
        }
    }

    fun onDeleteClicked() {
        val state = _uiState.value
        DiaryActivityStore.deleteActivity(state.entryId, state.title)
        executionScope.launch {
            if (state.entryId > 0) {
                getRepository()?.deleteEntry(state.entryId)
            }
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateToDelete)
        }
    }

    fun onBackClicked() {
        executionScope.launch {
            _navigationEvent.emit(ActivityDetailsNavigationEvent.NavigateBack)
        }
    }
}
