package com.simats.echodiary.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.DiaryEntryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StorageCategoryItem(
    val name: String,
    val count: Int,
    val percentage: Float,
    val colorHex: Long
)

data class StorageBreakdownItem(
    val title: String,
    val description: String,
    val sizeText: String,
    val sizeBytes: Long,
    val countText: String,
    val percentage: Float,
    val colorHex: Long,
    val iconEmoji: String
)

data class StorageUiState(
    val totalAppStorageText: String = "Calculating...",
    val totalAppBytes: Long = 0L,
    val deviceStorageSummary: String = "Calculating...",
    val deviceFreeStorageSummary: String = "Calculating...",
    val deviceTotalStorageSummary: String = "Calculating...",
    val deviceUsedPercentage: Float = 0.5f,
    val entriesBreakdown: StorageBreakdownItem = StorageBreakdownItem(
        title = "Diary Entries",
        description = "Encrypted local entries & activity logs",
        sizeText = "0 KB",
        sizeBytes = 0L,
        countText = "0 entries",
        percentage = 0.25f,
        colorHex = 0xFF3B82F6,
        iconEmoji = "📝"
    ),
    val memoriesBreakdown: StorageBreakdownItem = StorageBreakdownItem(
        title = "AI Memories & Summaries",
        description = "On-device SLM weekly, monthly & yearly narratives",
        sizeText = "0 KB",
        sizeBytes = 0L,
        countText = "3 memories",
        percentage = 0.25f,
        colorHex = 0xFF6366F1,
        iconEmoji = "🧠"
    ),
    val mediaBreakdown: StorageBreakdownItem = StorageBreakdownItem(
        title = "Attached Photos & Media",
        description = "Memory photo attachments & card banners",
        sizeText = "0 KB",
        sizeBytes = 0L,
        countText = "0 photos",
        percentage = 0.25f,
        colorHex = 0xFF10B981,
        iconEmoji = "🖼️"
    ),
    val cacheBreakdown: StorageBreakdownItem = StorageBreakdownItem(
        title = "Temporary Cache",
        description = "Shared image renders & temporary buffers",
        sizeText = "0 KB",
        sizeBytes = 0L,
        countText = "Safe to clean",
        percentage = 0.25f,
        colorHex = 0xFFF59E0B,
        iconEmoji = "⚡"
    ),
    val categoryBreakdown: List<StorageCategoryItem> = emptyList(),
    val totalEntriesCount: Int = 0,
    val totalMemoriesCount: Int = 3,
    val totalPhotosCount: Int = 0,
    val weeklyMemoryTitle: String = "",
    val monthlyMemoryTitle: String = "",
    val yearlyMemoryTitle: String = "",
    val isClearingCache: Boolean = false,
    val isExporting: Boolean = false,
    val feedbackMessage: String? = null
)

class StorageViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { entries ->
                _uiState.update { state ->
                    state.copy(
                        totalEntriesCount = entries.size,
                        weeklyMemoryTitle = DiaryActivityStore.weeklyMemoryTitle.value,
                        monthlyMemoryTitle = DiaryActivityStore.monthlySummaryTitle.value,
                        yearlyMemoryTitle = DiaryActivityStore.yearlyChapterTitle.value
                    )
                }
            }
        }
    }

    fun refreshStorageStats(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val entries = DiaryActivityStore.activities.value
            val entriesCount = entries.size

            // 1. Calculate Entries Storage Size (data text + SQLite overhead estimate)
            val entriesTextBytes = entries.sumOf {
                (it.title.toByteArray(Charsets.UTF_8).size +
                        it.description.toByteArray(Charsets.UTF_8).size +
                        it.category.toByteArray(Charsets.UTF_8).size +
                        it.mood.toByteArray(Charsets.UTF_8).size + 128).toLong()
            }.coerceAtLeast(16 * 1024L) // Base 16KB DB overhead

            // 2. Calculate Memories Storage Size (Weekly, Monthly, Yearly summaries & vector embeddings)
            val weeklyText = DiaryActivityStore.weeklyMemoryNarrative.value + DiaryActivityStore.weeklyMemoryTitle.value
            val monthlyText = DiaryActivityStore.monthlySummaryText.value + DiaryActivityStore.monthlySummaryTitle.value
            val yearlyText = DiaryActivityStore.yearlyChapterText.value + DiaryActivityStore.yearlyChapterTitle.value
            val memoriesCount = 3
            val memoriesTextBytes = (weeklyText.toByteArray(Charsets.UTF_8).size +
                    monthlyText.toByteArray(Charsets.UTF_8).size +
                    yearlyText.toByteArray(Charsets.UTF_8).size +
                    (memoriesCount * 384 * 4)).toLong().coerceAtLeast(24 * 1024L)

            // 3. Scan Photos & Media directory
            val diaryPhotosDir = File(context.filesDir, "diary_photos")
            val mediaBytes = if (diaryPhotosDir.exists()) getFolderSize(diaryPhotosDir) else 0L
            val photoFilesCount = diaryPhotosDir.listFiles()?.count { !it.isDirectory } ?: 0

            // Also check attached weekly memory photo
            val weeklyPhotoUri = DiaryActivityStore.weeklyMemoryPhotoUri.value
            val totalPhotosCount = if (!weeklyPhotoUri.isNullOrBlank() && photoFilesCount == 0) 1 else photoFilesCount

            // 4. Scan Cache directory (shared_memories, response caches)
            val cacheDir = context.cacheDir
            val cacheBytes = if (cacheDir.exists()) getFolderSize(cacheDir) else 0L

            // Total App storage
            val totalAppBytes = entriesTextBytes + memoriesTextBytes + mediaBytes + cacheBytes
            val safeTotalBytes = if (totalAppBytes <= 0L) 1L else totalAppBytes

            val entriesPct = (entriesTextBytes.toFloat() / safeTotalBytes).coerceIn(0.05f, 0.90f)
            val memoriesPct = (memoriesTextBytes.toFloat() / safeTotalBytes).coerceIn(0.05f, 0.90f)
            val mediaPct = (mediaBytes.toFloat() / safeTotalBytes).coerceIn(0.05f, 0.90f)
            val cachePct = (cacheBytes.toFloat() / safeTotalBytes).coerceIn(0.05f, 0.90f)

            // Normalize percentages to sum to 1.0f
            val sumPct = entriesPct + memoriesPct + mediaPct + cachePct
            val normEntriesPct = entriesPct / sumPct
            val normMemoriesPct = memoriesPct / sumPct
            val normMediaPct = mediaPct / sumPct
            val normCachePct = cachePct / sumPct

            // 5. Category breakdown
            val categoryColors = listOf(
                0xFF3B82F6, // Blue
                0xFF10B981, // Emerald
                0xFF8B5CF6, // Purple
                0xFFF59E0B, // Amber
                0xFFEC4899, // Pink
                0xFF06B6D4  // Cyan
            )
            val categoryGroups = entries.groupBy { it.category }
            val catItems = categoryGroups.entries.mapIndexed { index, entry ->
                val count = entry.value.size
                val pct = if (entriesCount > 0) count.toFloat() / entriesCount else 0f
                val color = categoryColors[index % categoryColors.size]
                StorageCategoryItem(
                    name = entry.key.ifBlank { "General" },
                    count = count,
                    percentage = pct,
                    colorHex = color
                )
            }.sortedByDescending { it.count }

            // 6. Device storage metrics
            var deviceTotalSummary = "128 GB"
            var deviceFreeSummary = "45 GB"
            var deviceUsedSummary = "83 GB"
            var deviceUsedPct = 0.65f

            try {
                val stat = StatFs(Environment.getDataDirectory().path)
                val blockSize = stat.blockSizeLong
                val totalBlocks = stat.blockCountLong
                val availableBlocks = stat.availableBlocksLong
                val totalBytes = totalBlocks * blockSize
                val freeBytes = availableBlocks * blockSize
                val usedBytes = (totalBytes - freeBytes).coerceAtLeast(0L)

                deviceTotalSummary = formatBytes(totalBytes)
                deviceFreeSummary = formatBytes(freeBytes)
                deviceUsedSummary = formatBytes(usedBytes)
                if (totalBytes > 0) {
                    deviceUsedPct = (usedBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
                }
            } catch (_: Exception) {}

            withContext(Dispatchers.Main) {
                _uiState.update { current ->
                    current.copy(
                        totalAppStorageText = formatBytes(totalAppBytes),
                        totalAppBytes = totalAppBytes,
                        deviceStorageSummary = deviceUsedSummary,
                        deviceFreeStorageSummary = deviceFreeSummary,
                        deviceTotalStorageSummary = deviceTotalSummary,
                        deviceUsedPercentage = deviceUsedPct,
                        totalEntriesCount = entriesCount,
                        totalMemoriesCount = memoriesCount,
                        totalPhotosCount = totalPhotosCount,
                        weeklyMemoryTitle = DiaryActivityStore.weeklyMemoryTitle.value,
                        monthlyMemoryTitle = DiaryActivityStore.monthlySummaryTitle.value,
                        yearlyMemoryTitle = DiaryActivityStore.yearlyChapterTitle.value,
                        entriesBreakdown = StorageBreakdownItem(
                            title = "Diary Entries",
                            description = "Encrypted local entries & activity logs",
                            sizeText = formatBytes(entriesTextBytes),
                            sizeBytes = entriesTextBytes,
                            countText = "$entriesCount recorded entries",
                            percentage = normEntriesPct,
                            colorHex = 0xFF3B82F6,
                            iconEmoji = "📝"
                        ),
                        memoriesBreakdown = StorageBreakdownItem(
                            title = "AI Memories & Summaries",
                            description = "On-device SLM weekly, monthly & yearly narratives",
                            sizeText = formatBytes(memoriesTextBytes),
                            sizeBytes = memoriesTextBytes,
                            countText = "3 synthesized memories (Weekly, Monthly, Yearly)",
                            percentage = normMemoriesPct,
                            colorHex = 0xFF6366F1,
                            iconEmoji = "🧠"
                        ),
                        mediaBreakdown = StorageBreakdownItem(
                            title = "Attached Photos & Media",
                            description = "Memory photo attachments & card banners",
                            sizeText = formatBytes(mediaBytes),
                            sizeBytes = mediaBytes,
                            countText = "$totalPhotosCount attached photo${if (totalPhotosCount == 1) "" else "s"}",
                            percentage = normMediaPct,
                            colorHex = 0xFF10B981,
                            iconEmoji = "🖼️"
                        ),
                        cacheBreakdown = StorageBreakdownItem(
                            title = "Temporary Cache",
                            description = "Shared image renders & temporary buffers",
                            sizeText = formatBytes(cacheBytes),
                            sizeBytes = cacheBytes,
                            countText = if (cacheBytes > 0) "Safe to clean" else "Clean",
                            percentage = normCachePct,
                            colorHex = 0xFFF59E0B,
                            iconEmoji = "⚡"
                        ),
                        categoryBreakdown = catItems
                    )
                }
            }
        }
    }

    fun clearTemporaryCache(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isClearingCache = true) }

            val cacheDir = context.cacheDir
            var freedBytes = 0L

            if (cacheDir.exists()) {
                freedBytes = getFolderSize(cacheDir)
                deleteDirContents(cacheDir)
            }

            // Also check shared_memories subfolder
            val sharedMemDir = File(cacheDir, "shared_memories")
            if (sharedMemDir.exists()) {
                deleteDirContents(sharedMemDir)
            }

            withContext(Dispatchers.Main) {
                val freedText = if (freedBytes > 0) formatBytes(freedBytes) else "Cache"
                _uiState.update {
                    it.copy(
                        isClearingCache = false,
                        feedbackMessage = "Temporary cache cleared! Freed $freedText."
                    )
                }
                // Refresh metrics after clearing
                refreshStorageStats(context)
            }
        }
    }

    fun exportDiaryData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isExporting = true) }
            val entries = DiaryActivityStore.activities.value

            val exportJson = JSONObject()
            exportJson.put("app", "EchoDiary")
            exportJson.put("version", "1.0")
            exportJson.put("export_date", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
            exportJson.put("user_name", DiaryActivityStore.userFullName.value)
            exportJson.put("user_email", DiaryActivityStore.userEmail.value)
            exportJson.put("total_entries", entries.size)

            val entriesArray = JSONArray()
            for (entry in entries) {
                val obj = JSONObject()
                obj.put("id", entry.id)
                obj.put("title", entry.title)
                obj.put("description", entry.description)
                obj.put("category", entry.category)
                obj.put("mood", entry.mood)
                obj.put("date", entry.date)
                entriesArray.put(obj)
            }
            exportJson.put("entries", entriesArray)

            val memoriesObj = JSONObject()
            memoriesObj.put("weekly_memory_title", DiaryActivityStore.weeklyMemoryTitle.value)
            memoriesObj.put("weekly_memory_narrative", DiaryActivityStore.weeklyMemoryNarrative.value)
            memoriesObj.put("monthly_memory_title", DiaryActivityStore.monthlySummaryTitle.value)
            memoriesObj.put("monthly_memory_narrative", DiaryActivityStore.monthlySummaryText.value)
            memoriesObj.put("yearly_memory_title", DiaryActivityStore.yearlyChapterTitle.value)
            memoriesObj.put("yearly_memory_narrative", DiaryActivityStore.yearlyChapterText.value)
            exportJson.put("synthesized_memories", memoriesObj)

            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isExporting = false) }
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "EchoDiary Backup - ${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())}")
                        putExtra(Intent.EXTRA_TEXT, exportJson.toString(2))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val chooser = Intent.createChooser(shareIntent, "Export Diary Backup")
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                } catch (_: Exception) {}
            }
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    private fun getFolderSize(dir: File): Long {
        var size: Long = 0
        val files = dir.listFiles() ?: return 0L
        for (file in files) {
            size += if (file.isDirectory) getFolderSize(file) else file.length()
        }
        return size
    }

    private fun deleteDirContents(dir: File) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                deleteDirContents(file)
            }
            try {
                file.delete()
            } catch (_: Exception) {}
        }
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
        val mb = kb / 1024.0
        if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb)
        val gb = mb / 1024.0
        return String.format(Locale.US, "%.2f GB", gb)
    }
}
