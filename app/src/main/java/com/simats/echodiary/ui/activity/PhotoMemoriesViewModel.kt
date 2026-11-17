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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PhotoMemoryItem(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val dateHeader: String = "",
    val timeHeader: String = "",
    val category: String = "",
    val mood: String = "",
    val icon: String = "📝",
    val photoUri: String? = null,
    val photoUris: List<String> = emptyList(),
    val hasPhoto: Boolean = true,
    val imageCaption: String = "",
    val imageSubcaption: String = "",
    val rawEntry: DiaryEntryItem? = null
) {
    fun getAllPhotos(): List<String> {
        val set = linkedSetOf<String>()
        if (!photoUri.isNullOrBlank()) set.add(photoUri)
        photoUris.forEach { if (it.isNotBlank()) set.add(it) }
        return set.toList()
    }
}

data class PhotoDateGroup(
    val rawDate: String,
    val formattedDate: String,
    val totalPhotosInDate: Int,
    val memories: List<PhotoMemoryItem>
)

data class PhotoMemoriesUiState(
    val headerTitle: String = "A moment, in a picture.",
    val headerSubtitle: String = "Photos connected to your diary entries.",
    val photoMemories: List<PhotoMemoryItem> = emptyList(),
    val dateGroups: List<PhotoDateGroup> = emptyList(),
    val totalPhotosCount: Int = 0,
    val dateHeader: String = "",
    val imageCaption: String = "",
    val imageSubcaption: String = "",
    val entryTitle: String = "",
    val entrySubtitle: String = "",
    val entryDescription: String = "",
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
        val photoEntries = activities.filter { it.hasPhoto || !it.photoUri.isNullOrBlank() || it.photoUris.isNotEmpty() }

        val mappedItems = photoEntries.map { entry ->
            val allPhotos = entry.getAllPhotos()
            val dateHdr = formatPhotoDateHeader(entry.date, allPhotos.size)
            val timeText = if (entry.time.isNotBlank()) entry.time else "Recorded moment"
            val timeHdr = if (entry.time.isNotBlank()) "🕒 ${entry.time}" else "🕒 Memory moment"

            PhotoMemoryItem(
                id = entry.id,
                title = entry.title,
                description = entry.description,
                date = entry.date,
                time = timeText,
                dateHeader = dateHdr,
                timeHeader = timeHdr,
                category = entry.category,
                mood = entry.mood,
                icon = entry.icon.ifBlank { DiaryActivityStore.getCategoryIcon(entry.category) },
                photoUri = entry.photoUri ?: allPhotos.firstOrNull(),
                photoUris = allPhotos,
                hasPhoto = allPhotos.isNotEmpty() || entry.hasPhoto,
                imageCaption = "${entry.title} · ${entry.category}",
                imageSubcaption = if (allPhotos.size > 1) {
                    "${allPhotos.size} photos collage · ${entry.mood}"
                } else if (allPhotos.isNotEmpty()) {
                    "Photo associated with this memory · ${entry.mood}"
                } else {
                    "Illustrative photo associated with this entry."
                },
                rawEntry = entry
            )
        }

        // Group by Date for chronological timeline display
        val groupedByDate = mappedItems.groupBy { it.date }
        val dateGroups = groupedByDate.entries.map { (dateKey, itemsInDate) ->
            val totalInGroup = itemsInDate.sumOf { it.getAllPhotos().size.coerceAtLeast(1) }
            PhotoDateGroup(
                rawDate = dateKey,
                formattedDate = formatDateOnlyHeader(dateKey),
                totalPhotosInDate = totalInGroup,
                memories = itemsInDate
            )
        }

        val totalPhotos = mappedItems.sumOf { it.getAllPhotos().size.coerceAtLeast(1) }
        val firstItem = mappedItems.firstOrNull()
        val footer = when (totalPhotos) {
            0 -> "No photo memories recorded yet."
            1 -> "1 recorded photo memory represented."
            else -> "$totalPhotos recorded photo memories represented across ${dateGroups.size} day(s)."
        }

        _uiState.update {
            it.copy(
                photoMemories = mappedItems,
                dateGroups = dateGroups,
                totalPhotosCount = totalPhotos,
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
        fun formatDateOnlyHeader(dateStr: String): String {
            val cleanDate = dateStr.trim()
            if (cleanDate.isBlank()) return "Recorded Memory"

            val months = mapOf(
                "Jan" to "January", "Feb" to "February", "Mar" to "March",
                "Apr" to "April", "May" to "May", "Jun" to "June",
                "Jul" to "July", "Aug" to "August", "Sep" to "September",
                "Oct" to "October", "Nov" to "November", "Dec" to "December"
            )
            for ((short, full) in months) {
                if (cleanDate.startsWith(short, ignoreCase = true)) {
                    return cleanDate.replaceFirst(Regex("^$short", RegexOption.IGNORE_CASE), full)
                }
            }

            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
                val parsed = inputFormat.parse(cleanDate)
                if (parsed != null) {
                    return outputFormat.format(parsed)
                }
            } catch (_: Exception) {}

            return cleanDate
        }

        fun formatPhotoDateHeader(dateStr: String, photoCount: Int = 1): String {
            val dateLabel = formatDateOnlyHeader(dateStr)
            val countLabel = if (photoCount <= 1) "1 photo" else "$photoCount photos"
            return "$dateLabel · $countLabel"
        }
    }
}
