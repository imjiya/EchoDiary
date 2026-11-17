package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditYourMemoryUiState(
    val dateRangeText: String = DiaryActivityStore.weeklyMemoryDateRange.value,
    val memoryTitle: String = DiaryActivityStore.weeklyMemoryTitle.value,
    val weeklyNarrative: String = DiaryActivityStore.weeklyMemoryNarrative.value,
    val sourceMaterialText: String = "Source material: ${DiaryActivityStore.activities.value.size} recorded entries.",
    val photoUri: String? = DiaryActivityStore.weeklyMemoryPhotoUri.value,
    val photoUris: List<String> = DiaryActivityStore.weeklyMemoryPhotoUris.value.ifEmpty {
        listOfNotNull(DiaryActivityStore.weeklyMemoryPhotoUri.value)
    },
    val hasPhoto: Boolean = !DiaryActivityStore.weeklyMemoryPhotoUri.value.isNullOrBlank() || DiaryActivityStore.weeklyMemoryPhotoUris.value.isNotEmpty()
)

class EditYourMemoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        EditYourMemoryUiState(
            dateRangeText = DiaryActivityStore.weeklyMemoryDateRange.value,
            memoryTitle = DiaryActivityStore.weeklyMemoryTitle.value,
            weeklyNarrative = DiaryActivityStore.weeklyMemoryNarrative.value,
            sourceMaterialText = "Source material: ${DiaryActivityStore.activities.value.size} recorded entries.",
            photoUri = DiaryActivityStore.weeklyMemoryPhotoUri.value,
            photoUris = DiaryActivityStore.weeklyMemoryPhotoUris.value.ifEmpty {
                listOfNotNull(DiaryActivityStore.weeklyMemoryPhotoUri.value)
            },
            hasPhoto = !DiaryActivityStore.weeklyMemoryPhotoUri.value.isNullOrBlank() || DiaryActivityStore.weeklyMemoryPhotoUris.value.isNotEmpty()
        )
    )
    val uiState: StateFlow<EditYourMemoryUiState> = _uiState.asStateFlow()

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(memoryTitle = title) }
    }

    fun onNarrativeChanged(narrative: String) {
        _uiState.update { it.copy(weeklyNarrative = narrative) }
    }

    fun onPhotoSelected(uri: String) {
        _uiState.update { current ->
            val updated = if (current.photoUris.contains(uri)) current.photoUris else current.photoUris + uri
            current.copy(
                photoUri = updated.firstOrNull(),
                photoUris = updated,
                hasPhoto = true
            )
        }
    }

    fun onPhotosSelected(uris: List<String>) {
        if (uris.isEmpty()) return
        _uiState.update { current ->
            val updated = (current.photoUris + uris).distinct()
            current.copy(
                photoUri = updated.firstOrNull(),
                photoUris = updated,
                hasPhoto = true
            )
        }
    }

    fun onRemovePhotoAt(index: Int) {
        _uiState.update { current ->
            val updated = current.photoUris.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
            }
            current.copy(
                photoUri = updated.firstOrNull(),
                photoUris = updated,
                hasPhoto = updated.isNotEmpty()
            )
        }
    }

    fun onRemovePhoto() {
        _uiState.update { it.copy(photoUri = null, photoUris = emptyList(), hasPhoto = false) }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        val allPhotos = if (state.hasPhoto) state.photoUris.ifEmpty { listOfNotNull(state.photoUri) } else emptyList()
        DiaryActivityStore.updateWeeklyMemory(
            title = state.memoryTitle,
            narrative = state.weeklyNarrative,
            photoUri = allPhotos.firstOrNull(),
            photoUris = allPhotos
        )
    }
}
