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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WriteDiaryEntryUiState(
    val title: String = "",
    val description: String = "",
    val date: String = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()),
    val category: String = "College",
    val mood: String = "Good",
    val hasPhoto: Boolean = false,
    val photoUri: String? = null,
    val photoUris: List<String> = emptyList(),
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

    private val _uiState = MutableStateFlow(
        WriteDiaryEntryUiState(
            date = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(Date())
        )
    )
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

    fun onPhotoSelected(uriString: String) {
        _uiState.update { current ->
            val updatedList = if (current.photoUris.contains(uriString)) current.photoUris else current.photoUris + uriString
            current.copy(
                hasPhoto = true,
                photoUri = updatedList.firstOrNull(),
                photoUris = updatedList,
                statusMessage = "Photo imported from gallery ✓",
                errorMessage = null
            )
        }
    }

    fun onPhotosSelected(uris: List<String>) {
        if (uris.isEmpty()) return
        _uiState.update { current ->
            val combined = (current.photoUris + uris).distinct()
            current.copy(
                hasPhoto = true,
                photoUri = combined.firstOrNull(),
                photoUris = combined,
                statusMessage = if (combined.size == 1) "Photo imported from gallery ✓" else "${combined.size} photos selected ✓",
                errorMessage = null
            )
        }
    }

    fun onRemovePhotoAt(index: Int) {
        _uiState.update { current ->
            val updatedList = current.photoUris.toMutableList()
            if (index in updatedList.indices) {
                updatedList.removeAt(index)
            }
            current.copy(
                hasPhoto = updatedList.isNotEmpty(),
                photoUri = updatedList.firstOrNull(),
                photoUris = updatedList,
                statusMessage = if (updatedList.isEmpty()) null else "${updatedList.size} photo(s) remaining"
            )
        }
    }

    fun onRemovePhoto() {
        _uiState.update {
            it.copy(
                hasPhoto = false,
                photoUri = null,
                photoUris = emptyList(),
                statusMessage = null
            )
        }
    }

    fun onPermissionDenied(message: String) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }

    fun onCategoryFieldClicked() {
        executionScope.launch {
            _navigationEvent.emit(WriteDiaryEntryNavigationEvent.NavigateToChooseCategory)
        }
    }

    fun onCalendarClicked() {
        executionScope.launch {
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

        val displayTitle = if (title.isNotBlank()) title else desc.take(40).trim()
        val displayDesc = if (desc.isNotBlank()) desc else displayTitle
        val entryDate = _uiState.value.date.ifBlank {
            SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(Date())
        }
        val entryCategory = _uiState.value.category.ifBlank { "College" }
        val entryMood = _uiState.value.mood.ifBlank { "Good" }

        executionScope.launch {
            val repository = getRepository()
            if (repository != null) {
                val result = repository.createManualEntry(
                    title = title,
                    description = desc,
                    date = entryDate,
                    category = entryCategory,
                    mood = entryMood
                )

                val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

                result.onSuccess { response ->
                    val serverId = response.entry?.id ?: ((System.currentTimeMillis() % 100000).toInt())
                    val newActivity = DiaryEntryItem(
                        id = serverId,
                        title = displayTitle,
                        category = entryCategory,
                        date = entryDate,
                        description = displayDesc,
                        icon = DiaryActivityStore.getCategoryIcon(entryCategory),
                        mood = entryMood,
                        hasPhoto = _uiState.value.hasPhoto,
                        photoUri = _uiState.value.photoUri,
                        photoUris = _uiState.value.photoUris,
                        time = currentTime
                    )
                    DiaryActivityStore.addActivity(newActivity)

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
                            date = entryDate,
                            category = entryCategory
                        )
                    )
                }.onFailure { error ->
                    val localId = (System.currentTimeMillis() % 100000).toInt()
                    val newActivity = DiaryEntryItem(
                        id = localId,
                        title = displayTitle,
                        category = entryCategory,
                        date = entryDate,
                        description = displayDesc,
                        icon = DiaryActivityStore.getCategoryIcon(entryCategory),
                        mood = entryMood,
                        hasPhoto = _uiState.value.hasPhoto,
                        photoUri = _uiState.value.photoUri,
                        photoUris = _uiState.value.photoUris,
                        time = currentTime
                    )
                    DiaryActivityStore.addActivity(newActivity)

                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Saved locally to memory of activities."
                        )
                    }
                    _navigationEvent.emit(
                        WriteDiaryEntryNavigationEvent.NavigateToActivityDetails(
                            title = displayTitle,
                            description = displayDesc,
                            date = entryDate,
                            category = entryCategory
                        )
                    )
                }
            } else {
                val localId = (System.currentTimeMillis() % 100000).toInt()
                val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                val newActivity = DiaryEntryItem(
                    id = localId,
                    title = displayTitle,
                    category = entryCategory,
                    date = entryDate,
                    description = displayDesc,
                    icon = DiaryActivityStore.getCategoryIcon(entryCategory),
                    mood = entryMood,
                    hasPhoto = _uiState.value.hasPhoto,
                    photoUri = _uiState.value.photoUri,
                    photoUris = _uiState.value.photoUris,
                    time = currentTime
                )
                DiaryActivityStore.addActivity(newActivity)

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
                        date = entryDate,
                        category = entryCategory
                    )
                )
            }
        }
    }

    fun onCancelClicked() {
        executionScope.launch {
            _navigationEvent.emit(WriteDiaryEntryNavigationEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        executionScope.launch {
            _navigationEvent.emit(WriteDiaryEntryNavigationEvent.NavigateBack)
        }
    }
}
