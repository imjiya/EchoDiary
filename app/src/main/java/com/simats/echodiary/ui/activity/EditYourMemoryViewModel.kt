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
    val hasPhoto: Boolean = !DiaryActivityStore.weeklyMemoryPhotoUri.value.isNullOrBlank()
)

class EditYourMemoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        EditYourMemoryUiState(
            dateRangeText = DiaryActivityStore.weeklyMemoryDateRange.value,
            memoryTitle = DiaryActivityStore.weeklyMemoryTitle.value,
            weeklyNarrative = DiaryActivityStore.weeklyMemoryNarrative.value,
            sourceMaterialText = "Source material: ${DiaryActivityStore.activities.value.size} recorded entries.",
            photoUri = DiaryActivityStore.weeklyMemoryPhotoUri.value,
            hasPhoto = !DiaryActivityStore.weeklyMemoryPhotoUri.value.isNullOrBlank()
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
        _uiState.update { it.copy(photoUri = uri, hasPhoto = true) }
    }

    fun onRemovePhoto() {
        _uiState.update { it.copy(photoUri = null, hasPhoto = false) }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        DiaryActivityStore.updateWeeklyMemory(
            title = state.memoryTitle,
            narrative = state.weeklyNarrative,
            photoUri = if (state.hasPhoto) state.photoUri else null
        )
    }
}
