package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotoMemoryItem(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val dateHeader: String = "",
    val category: String = "",
    val mood: String = "",
    val icon: String = "📝",
    val photoUri: String? = null,
    val hasPhoto: Boolean = true,
    val imageCaption: String = "",
    val imageSubcaption: String = "",
    val rawEntry: DiaryEntryItem? = null
)

data class PhotoMemoriesUiState(
    val headerTitle: String = "A moment, in a picture.",
    val headerSubtitle: String = "Photos connected to your diary entries.",
    val photoMemories: List<PhotoMemoryItem> = emptyList(),
    val totalPhotosCount: Int = 0,
    val dateHeader: String = "October 6, 2026 · 1 photo",
    val imageCaption: String = "Lunch with friends · A2",
    val imageSubcaption: String = "Illustrative lunch photo associated with this entry.",
    val entryTitle: String = "Lunch with friends",
    val entrySubtitle: String = "Friends · Oct 6, 2026",
    val entryDescription: String = "Had lunch with friends after class.",
    val footerText: String = "Only recorded photo memories are represented."
)

sealed class PhotoMemoriesNavigationEvent {
    object NavigateBack : PhotoMemoriesNavigationEvent()
    data class NavigateToActivityDetails(val item: DiaryEntryItem) : PhotoMemoriesNavigationEvent()
}

class PhotoMemoriesViewModel(
    scope: CoroutineScope? = null
) : ViewModel() {

    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private val _uiState = MutableStateFlow(PhotoMemoriesUiState())
    val uiState: StateFlow<PhotoMemoriesUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<PhotoMemoriesNavigationEvent>()
    val navigationEvent: SharedFlow<PhotoMemoriesNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        loadPhotoMemories(DiaryActivityStore.activities.value)
        executionScope.launch {
            DiaryActivityStore.activities.collectLatest { activitiesList ->
                loadPhotoMemories(activitiesList)
            }
        }
    }

    fun loadPhotoMemories(activities: List<DiaryEntryItem>) {
        val photoEntries = activities.filter { it.hasPhoto || !it.photoUri.isNullOrBlank() }
        val mappedItems = photoEntries.map { entry ->
            val dateHdr = formatPhotoDateHeader(entry.date)
            PhotoMemoryItem(
                id = entry.id,
                title = entry.title,
                description = entry.description,
                date = entry.date,
                dateHeader = dateHdr,
                category = entry.category,
                mood = entry.mood,
                icon = entry.icon.ifBlank { DiaryActivityStore.getCategoryIcon(entry.category) },
                photoUri = entry.photoUri,
                hasPhoto = entry.hasPhoto || !entry.photoUri.isNullOrBlank(),
                imageCaption = "${entry.title} · ${entry.category}",
                imageSubcaption = if (!entry.photoUri.isNullOrBlank()) {
                    "Photo associated with this memory · ${entry.mood}"
                } else {
                    "Illustrative photo associated with this entry."
                },
                rawEntry = entry
            )
        }

        val firstItem = mappedItems.firstOrNull()
        val count = mappedItems.size
        val footer = when (count) {
            0 -> "No photo memories recorded yet."
            1 -> "1 recorded photo memory represented."
            else -> "$count recorded photo memories represented."
        }

        _uiState.update {
            it.copy(
                photoMemories = mappedItems,
                totalPhotosCount = count,
                dateHeader = firstItem?.dateHeader ?: it.dateHeader,
                imageCaption = firstItem?.imageCaption ?: it.imageCaption,
                imageSubcaption = firstItem?.imageSubcaption ?: it.imageSubcaption,
                entryTitle = firstItem?.title ?: it.entryTitle,
                entrySubtitle = firstItem?.let { f -> "${f.category} · ${f.date}" } ?: it.entrySubtitle,
                entryDescription = firstItem?.description ?: it.entryDescription,
                footerText = footer
            )
        }
    }

    fun onBackClicked() {
        executionScope.launch {
            _navigationEvent.emit(PhotoMemoriesNavigationEvent.NavigateBack)
        }
    }

    fun onEntryClicked(item: DiaryEntryItem? = null) {
        val targetItem = item ?: _uiState.value.photoMemories.firstOrNull()?.rawEntry ?: DiaryActivityStore.selectedActivity.value
        if (targetItem != null) {
            DiaryActivityStore.selectActivity(targetItem)
            executionScope.launch {
                _navigationEvent.emit(PhotoMemoriesNavigationEvent.NavigateToActivityDetails(targetItem))
            }
        }
    }

    companion object {
        fun formatPhotoDateHeader(dateStr: String): String {
            val cleanDate = dateStr.trim()
            if (cleanDate.isBlank()) return "Recorded Memory · 1 photo"

            val months = mapOf(
                "Jan" to "January", "Feb" to "February", "Mar" to "March",
                "Apr" to "April", "May" to "May", "Jun" to "June",
                "Jul" to "July", "Aug" to "August", "Sep" to "September",
                "Oct" to "October", "Nov" to "November", "Dec" to "December"
            )
            for ((short, full) in months) {
                if (cleanDate.startsWith(short, ignoreCase = true)) {
                    val replaced = cleanDate.replaceFirst(Regex("^$short", RegexOption.IGNORE_CASE), full)
                    return "$replaced · 1 photo"
                }
            }

            try {
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH)
                val outputFormat = java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.ENGLISH)
                val parsed = inputFormat.parse(cleanDate)
                if (parsed != null) {
                    return "${outputFormat.format(parsed)} · 1 photo"
                }
            } catch (_: Exception) {}

            return "$cleanDate · 1 photo"
        }
    }
}
