package com.simats.echodiary.data.local

import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.ui.activity.CategoryBreakdownItem
import com.simats.echodiary.ui.activity.DiaryEntryItem
import com.simats.echodiary.ui.activity.SupportingRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DiaryActivityStore {

    val sampleActivities = listOf(
        DiaryEntryItem(
            title = "Weekend with family",
            category = "Family",
            date = "Oct 10, 2026",
            description = "Spent time with family over the weekend.",
            icon = "🏠",
            id = 5,
            mood = "Happy"
        ),
        DiaryEntryItem(
            title = "Presentation preparation",
            category = "Work",
            date = "Oct 8, 2026",
            description = "Prepared a presentation.",
            icon = "💼",
            id = 4,
            mood = "Focused"
        ),
        DiaryEntryItem(
            title = "Evening walk",
            category = "Health",
            date = "Oct 7, 2026",
            description = "Went for an evening walk.",
            icon = "❤️",
            id = 3,
            mood = "Calm"
        ),
        DiaryEntryItem(
            title = "Lunch with friends",
            category = "Friends",
            date = "Oct 6, 2026",
            description = "Had lunch with friends after class.",
            icon = "👥",
            id = 2,
            mood = "Joyful",
            hasPhoto = true
        ),
        DiaryEntryItem(
            title = "Lectures and project",
            category = "College",
            date = "Oct 5, 2026",
            description = "Attended lectures and worked on a project.",
            icon = "🎓",
            id = 1,
            mood = "Focused"
        )
    )

    private val _activities = MutableStateFlow<List<DiaryEntryItem>>(emptyList())
    val activities: StateFlow<List<DiaryEntryItem>> = _activities.asStateFlow()

    private val _selectedActivity = MutableStateFlow<DiaryEntryItem?>(null)
    val selectedActivity: StateFlow<DiaryEntryItem?> = _selectedActivity.asStateFlow()

    private val _isLoadedFromBackend = MutableStateFlow(false)
    val isLoadedFromBackend: StateFlow<Boolean> = _isLoadedFromBackend.asStateFlow()

    fun setLoadedFromBackend(loaded: Boolean) {
        _isLoadedFromBackend.value = loaded
    }

    // ------------------------------------------------------------------------
    // User Profile & Account State
    // ------------------------------------------------------------------------
    private val _userFullName = MutableStateFlow("")
    val userFullName: StateFlow<String> = _userFullName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userInitials = MutableStateFlow("")
    val userInitials: StateFlow<String> = _userInitials.asStateFlow()

    private val _isProfileLoadedFromBackend = MutableStateFlow(false)
    val isProfileLoadedFromBackend: StateFlow<Boolean> = _isProfileLoadedFromBackend.asStateFlow()

    fun setProfileLoadedFromBackend(loaded: Boolean) {
        _isProfileLoadedFromBackend.value = loaded
    }

    // ------------------------------------------------------------------------
    // Appearance & Theme State
    // ------------------------------------------------------------------------
    private val _themeMode = MutableStateFlow("Light")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _themePalette = MutableStateFlow("Calm Blue")
    val themePalette: StateFlow<String> = _themePalette.asStateFlow()

    // ------------------------------------------------------------------------
    // Notifications State
    // ------------------------------------------------------------------------
    private val _diaryRemindersEnabled = MutableStateFlow(true)
    val diaryRemindersEnabled: StateFlow<Boolean> = _diaryRemindersEnabled.asStateFlow()

    private val _weeklyMemoryRemindersEnabled = MutableStateFlow(true)
    val weeklyMemoryRemindersEnabled: StateFlow<Boolean> = _weeklyMemoryRemindersEnabled.asStateFlow()

    private val _summaryUpdatesEnabled = MutableStateFlow(false)
    val summaryUpdatesEnabled: StateFlow<Boolean> = _summaryUpdatesEnabled.asStateFlow()

    private val _dailyReminderTime = MutableStateFlow("8:00 PM")
    val dailyReminderTime: StateFlow<String> = _dailyReminderTime.asStateFlow()

    private val _weeklyReminderTime = MutableStateFlow("Sunday · 8:00 PM")
    val weeklyReminderTime: StateFlow<String> = _weeklyReminderTime.asStateFlow()

    private val _notificationSummary = MutableStateFlow("Daily at 8:00 PM")
    val notificationSummary: StateFlow<String> = _notificationSummary.asStateFlow()

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    fun setFcmToken(token: String) {
        _fcmToken.value = token
    }

    fun getCurrentWeekLabel(entries: List<DiaryEntryItem> = emptyList()): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startFmt = SimpleDateFormat("MMM d", Locale.getDefault()).format(cal.time).uppercase(Locale.ROOT)
        cal.add(Calendar.DATE, 6)
        val endFmt = SimpleDateFormat("d, yyyy", Locale.getDefault()).format(cal.time)
        return "$startFmt–$endFmt"
    }

    fun getCurrentMonthLabel(): String {
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }

    fun getCurrentYearLabel(): String {
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
    }

    // ------------------------------------------------------------------------
    // Weekly Memory State
    // ------------------------------------------------------------------------
    private val _weeklyMemoryTitle = MutableStateFlow("Your Weekly Memory")
    val weeklyMemoryTitle: StateFlow<String> = _weeklyMemoryTitle.asStateFlow()

    private val _weeklyMemoryNarrative = MutableStateFlow(
        "No diary entries recorded for this week yet. Start writing or recording your voice diary to see your personalized weekly story."
    )
    val weeklyMemoryNarrative: StateFlow<String> = _weeklyMemoryNarrative.asStateFlow()

    private val _weeklyMemoryDateRange = MutableStateFlow("${getCurrentWeekLabel()} · WEEKLY DRAFT")
    val weeklyMemoryDateRange: StateFlow<String> = _weeklyMemoryDateRange.asStateFlow()

    private val _weeklyHighlights = MutableStateFlow<List<String>>(emptyList())
    val weeklyHighlights: StateFlow<List<String>> = _weeklyHighlights.asStateFlow()

    private val _weeklyMoodSummary = MutableStateFlow("No moods recorded for this period yet.")
    val weeklyMoodSummary: StateFlow<String> = _weeklyMoodSummary.asStateFlow()

    private val _weeklyEntriesCount = MutableStateFlow(0)
    val weeklyEntriesCount: StateFlow<Int> = _weeklyEntriesCount.asStateFlow()

    private val _weeklyMemoryPhotoUri = MutableStateFlow<String?>(null)
    val weeklyMemoryPhotoUri: StateFlow<String?> = _weeklyMemoryPhotoUri.asStateFlow()

    private val _weeklyCategoriesCount = MutableStateFlow(0)
    val weeklyCategoriesCount: StateFlow<Int> = _weeklyCategoriesCount.asStateFlow()

    private val _generatingMemoryType = MutableStateFlow("weekly")
    val generatingMemoryType: StateFlow<String> = _generatingMemoryType.asStateFlow()

    fun setGeneratingMemoryType(type: String) {
        _generatingMemoryType.value = type.lowercase(Locale.ROOT)
    }

    // ------------------------------------------------------------------------
    // Monthly Memory State
    // ------------------------------------------------------------------------
    private val _monthlyMonthLabel = MutableStateFlow(getCurrentMonthLabel())
    val monthlyMonthLabel: StateFlow<String> = _monthlyMonthLabel.asStateFlow()

    private val _monthlySummaryTitle = MutableStateFlow("${getCurrentMonthLabel()}, in your words")
    val monthlySummaryTitle: StateFlow<String> = _monthlySummaryTitle.asStateFlow()

    private val _monthlySummaryText = MutableStateFlow(
        "No diary entries recorded for this month yet. Your reflections will appear here as you log daily moments."
    )
    val monthlySummaryText: StateFlow<String> = _monthlySummaryText.asStateFlow()

    private val _monthlyCategoryBreakdown = MutableStateFlow<List<CategoryBreakdownItem>>(emptyList())
    val monthlyCategoryBreakdown: StateFlow<List<CategoryBreakdownItem>> = _monthlyCategoryBreakdown.asStateFlow()

    private val _monthlyEntriesCount = MutableStateFlow(0)
    val monthlyEntriesCount: StateFlow<Int> = _monthlyEntriesCount.asStateFlow()

    private val _monthlyCategoriesCount = MutableStateFlow(0)
    val monthlyCategoriesCount: StateFlow<Int> = _monthlyCategoriesCount.asStateFlow()

    private val _monthlyFooterText = MutableStateFlow("Log your daily moments to build your monthly reflection.")
    val monthlyFooterText: StateFlow<String> = _monthlyFooterText.asStateFlow()

    // ------------------------------------------------------------------------
    // Yearly Memory State
    // ------------------------------------------------------------------------
    private val _yearlyYearLabel = MutableStateFlow(getCurrentYearLabel())
    val yearlyYearLabel: StateFlow<String> = _yearlyYearLabel.asStateFlow()

    private val _yearlyChapterTitle = MutableStateFlow("One chapter of ${getCurrentYearLabel()}.")
    val yearlyChapterTitle: StateFlow<String> = _yearlyChapterTitle.asStateFlow()

    private val _yearlyChapterText = MutableStateFlow(
        "No diary entries recorded for this year yet. Your personal journey across the year will be summarized here."
    )
    val yearlyChapterText: StateFlow<String> = _yearlyChapterText.asStateFlow()

    private val _yearlyCategories = MutableStateFlow<List<String>>(emptyList())
    val yearlyCategories: StateFlow<List<String>> = _yearlyCategories.asStateFlow()

    private val _yearlySupportingRecords = MutableStateFlow<List<SupportingRecord>>(emptyList())
    val yearlySupportingRecords: StateFlow<List<SupportingRecord>> = _yearlySupportingRecords.asStateFlow()

    private val _yearlyFooterText = MutableStateFlow("Topics will be grounded directly from what you record.")
    val yearlyFooterText: StateFlow<String> = _yearlyFooterText.asStateFlow()

    init {
        try {
            val session = EchoDiaryApp.getSessionManager()
            val name = session.getUserName()
            val email = session.getUserEmail()
            if (!name.isNullOrBlank()) {
                _userFullName.value = name
                _userInitials.value = computeInitials(name)
            }
            if (!email.isNullOrBlank()) {
                _userEmail.value = email
            }
            _themeMode.value = session.getThemeMode()
            _themePalette.value = session.getThemePalette()

            _diaryRemindersEnabled.value = session.getDiaryRemindersEnabled()
            _weeklyMemoryRemindersEnabled.value = session.getWeeklyRemindersEnabled()
            _summaryUpdatesEnabled.value = session.getSummaryUpdatesEnabled()
            _dailyReminderTime.value = session.getDailyReminderTime()
            _weeklyReminderTime.value = session.getWeeklyReminderTime()

            val diary = _diaryRemindersEnabled.value
            val weekly = _weeklyMemoryRemindersEnabled.value
            val summary = _summaryUpdatesEnabled.value
            val dailyTime = _dailyReminderTime.value
            val weeklyTime = _weeklyReminderTime.value
            _notificationSummary.value = when {
                diary && weekly -> "Daily at $dailyTime · Sunday"
                diary -> "Daily at $dailyTime"
                weekly -> "Weekly: $weeklyTime"
                summary -> "Summary updates"
                else -> "Reminders paused"
            }
        } catch (_: Exception) {}
        refreshMemories()
    }

    fun updateNotificationSettings(
        diary: Boolean,
        weekly: Boolean,
        summary: Boolean,
        dailyTime: String,
        weeklyTime: String
    ) {
        _diaryRemindersEnabled.value = diary
        _weeklyMemoryRemindersEnabled.value = weekly
        _summaryUpdatesEnabled.value = summary
        _dailyReminderTime.value = dailyTime
        _weeklyReminderTime.value = weeklyTime

        _notificationSummary.value = when {
            diary && weekly -> "Daily at $dailyTime · Sunday"
            diary -> "Daily at $dailyTime"
            weekly -> "Weekly: $weeklyTime"
            summary -> "Summary updates"
            else -> "Reminders paused"
        }

        try {
            EchoDiaryApp.getSessionManager().saveNotificationPreferences(
                diaryReminders = diary,
                weeklyReminders = weekly,
                summaryUpdates = summary,
                dailyTime = dailyTime,
                weeklyTime = weeklyTime
            )
        } catch (_: Exception) {}
    }

    fun computeInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "AM"
            parts.size == 1 -> parts[0].take(2).uppercase(Locale.ROOT)
            else -> "${parts[0].first()}${parts[1].first()}".uppercase(Locale.ROOT)
        }
    }

    fun setProfile(name: String, email: String) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        if (cleanName.isNotBlank()) {
            _userFullName.value = cleanName
            _userInitials.value = computeInitials(cleanName)
        }
        if (cleanEmail.isNotBlank()) {
            _userEmail.value = cleanEmail
        }
        _isProfileLoadedFromBackend.value = true
    }

    fun setTheme(mode: String, palette: String) {
        _themeMode.value = mode
        _themePalette.value = palette
        try {
            EchoDiaryApp.getSessionManager().setThemeMode(mode)
            EchoDiaryApp.getSessionManager().setThemePalette(palette)
        } catch (_: Exception) {}
    }

    fun synthesizeMoodSummary(entries: List<DiaryEntryItem>): String {
        val moods = entries.map { it.mood.trim() }
            .filter { it.isNotBlank() && !it.equals("Not specified", ignoreCase = true) && !it.equals("Unspecified", ignoreCase = true) }
        return if (moods.isNotEmpty()) {
            val grouped = moods.groupingBy { it }.eachCount()
            val parts = grouped.map { "${it.value} recorded feeling ${it.key}" }.joinToString(", ")
            val unspecified = entries.size - moods.size
            if (unspecified > 0) {
                "$parts. The other $unspecified entries have no mood specified."
            } else {
                "$parts. All entries had moods recorded."
            }
        } else {
            "Recorded reflections across ${entries.size} entries with a calm, balanced tone."
        }
    }

    fun generateWeeklySummary(scope: CoroutineScope? = null) {
        val entries = _activities.value
        val count = entries.size
        val categories = entries.map { it.category }.distinct()
        val weekLabel = getCurrentWeekLabel(entries)

        if (count == 0) {
            _weeklyMemoryTitle.value = "Your Weekly Memory"
            _weeklyMemoryNarrative.value = "No diary entries recorded for this week yet. Start writing or recording your voice diary to see your personalized weekly story."
            _weeklyHighlights.value = emptyList()
            _weeklyMoodSummary.value = "No moods recorded for this period yet."
            _weeklyEntriesCount.value = 0
            _weeklyCategoriesCount.value = 0
            _weeklyMemoryDateRange.value = "$weekLabel · WEEKLY DRAFT"
            return
        }

        val ragResult = com.simats.echodiary.ai.RagMemoryEngine.synthesizeMemory(entries, "weekly", weekLabel)

        _weeklyMemoryTitle.value = ragResult.title
        _weeklyMemoryNarrative.value = ragResult.narrative
        _weeklyHighlights.value = ragResult.highlights
        _weeklyMoodSummary.value = ragResult.moodSummary
        _weeklyEntriesCount.value = count
        _weeklyCategoriesCount.value = categories.size
        _weeklyMemoryDateRange.value = "$weekLabel · WEEKLY DRAFT"

        scope?.launch {
            try {
                val response = EchoDiaryApp.getMemoryRepository().generateMemory(type = "weekly", save = true)
                response.onSuccess { data ->
                    data.title?.let { if (it.isNotBlank()) _weeklyMemoryTitle.value = it }
                    data.narrative?.let { if (it.isNotBlank()) _weeklyMemoryNarrative.value = it }
                    data.dateRange?.let { if (it.isNotBlank()) _weeklyMemoryDateRange.value = "$it · WEEKLY DRAFT" }
                    data.highlights?.let { if (it.isNotEmpty()) _weeklyHighlights.value = it }
                    data.moodSummary?.let { if (it.isNotBlank()) _weeklyMoodSummary.value = it }
                }
            } catch (_: Exception) {}
        }
    }

    fun generateMonthlySummary(scope: CoroutineScope? = null) {
        val entries = _activities.value
        val count = entries.size
        val categories = entries.map { it.category }.distinct()
        val monthLabel = getCurrentMonthLabel()

        if (count == 0) {
            _monthlyMonthLabel.value = monthLabel
            _monthlySummaryTitle.value = "$monthLabel, in your words"
            _monthlySummaryText.value = "No diary entries recorded for this month yet. Your reflections will appear here as you log daily moments."
            _monthlyCategoryBreakdown.value = emptyList()
            _monthlyEntriesCount.value = 0
            _monthlyCategoriesCount.value = 0
            _monthlyFooterText.value = "Log your daily moments to build your monthly reflection."
            return
        }

        val ragResult = com.simats.echodiary.ai.RagMemoryEngine.synthesizeMemory(entries, "monthly", monthLabel)

        val breakdown = categories.map { cat ->
            val catCount = entries.count { it.category.equals(cat, ignoreCase = true) }
            val progress = if (count > 0) catCount.toFloat() / count.toFloat() else 0f
            CategoryBreakdownItem(name = cat, count = catCount, progress = progress)
        }

        _monthlyMonthLabel.value = monthLabel
        _monthlySummaryTitle.value = ragResult.title
        _monthlySummaryText.value = ragResult.narrative
        _monthlyCategoryBreakdown.value = breakdown
        _monthlyEntriesCount.value = count
        _monthlyCategoriesCount.value = categories.size
        _monthlyFooterText.value = if (ragResult.highlights.isNotEmpty()) {
            "Highlights: ${ragResult.highlights.joinToString(", ")}."
        } else {
            "Log your daily moments to build your monthly reflection."
        }

        scope?.launch {
            try {
                val response = EchoDiaryApp.getMemoryRepository().generateMemory(type = "monthly", save = true)
                response.onSuccess { data ->
                    data.title?.let { if (it.isNotBlank()) _monthlySummaryTitle.value = it }
                    data.summaryText?.let { if (it.isNotBlank()) _monthlySummaryText.value = it }
                }
            } catch (_: Exception) {}
        }
    }

    fun generateYearlySummary(scope: CoroutineScope? = null) {
        val entries = _activities.value
        val count = entries.size
        val categories = entries.map { it.category }.distinct()
        val yearLabel = getCurrentYearLabel()

        if (count == 0) {
            _yearlyYearLabel.value = yearLabel
            _yearlyChapterTitle.value = "One chapter of $yearLabel."
            _yearlyChapterText.value = "No diary entries recorded for this year yet. Your personal journey across the year will be summarized here."
            _yearlyCategories.value = emptyList()
            _yearlySupportingRecords.value = emptyList()
            _yearlyFooterText.value = "Start journaling to see your yearly story unfold."
            return
        }

        val supporting = entries.map { SupportingRecord(it.title, it.date) }
        val ragResult = com.simats.echodiary.ai.RagMemoryEngine.synthesizeMemory(entries, "yearly", yearLabel)

        _yearlyYearLabel.value = yearLabel
        _yearlyChapterTitle.value = ragResult.title
        _yearlyChapterText.value = ragResult.narrative
        _yearlyCategories.value = categories
        _yearlySupportingRecords.value = supporting
        _yearlyFooterText.value = "Topics come from these $count records; grounded directly via RAG."

        scope?.launch {
            try {
                val response = EchoDiaryApp.getMemoryRepository().generateMemory(type = "yearly", save = true)
                response.onSuccess { data ->
                    data.title?.let { if (it.isNotBlank()) _yearlyChapterTitle.value = it }
                    data.narrative?.let { if (it.isNotBlank()) _yearlyChapterText.value = it }
                }
            } catch (_: Exception) {}
        }
    }

    fun updateWeeklyMemory(title: String, narrative: String, photoUri: String? = null) {
        if (title.isNotBlank()) _weeklyMemoryTitle.value = title
        if (narrative.isNotBlank()) _weeklyMemoryNarrative.value = narrative
        if (photoUri != null) {
            _weeklyMemoryPhotoUri.value = photoUri
        }
    }

    fun loadSampleActivities() {
        _activities.value = sampleActivities
        _selectedActivity.value = sampleActivities.firstOrNull()
        _isLoadedFromBackend.value = false
        refreshMemories()
    }

    fun clearData() {
        _activities.value = emptyList()
        _selectedActivity.value = null
        _weeklyMemoryPhotoUri.value = null
        _isLoadedFromBackend.value = false
        _isProfileLoadedFromBackend.value = false
        refreshMemories()
    }

    fun resetToDefaults(loadSampleData: Boolean = true) {
        if (loadSampleData) {
            loadSampleActivities()
        } else {
            clearData()
        }
        _isLoadedFromBackend.value = false
        _isProfileLoadedFromBackend.value = false
    }

    private fun refreshMemories() {
        generateWeeklySummary(null)
        generateMonthlySummary(null)
        generateYearlySummary(null)
    }

    fun setActivities(items: List<DiaryEntryItem>) {
        _activities.value = items
        _selectedActivity.value = items.firstOrNull()
        refreshMemories()
    }

    fun selectActivity(item: DiaryEntryItem) {
        _selectedActivity.value = item
    }

    fun addActivity(item: DiaryEntryItem) {
        val current = _activities.value.toMutableList()
        current.add(0, item)
        _activities.value = current
        _selectedActivity.value = item
        refreshMemories()
    }

    fun updateActivity(oldTitle: String, updatedItem: DiaryEntryItem) {
        val current = _activities.value.toMutableList()
        val index = current.indexOfFirst {
            (updatedItem.id > 0 && it.id == updatedItem.id) || it.title.equals(oldTitle, ignoreCase = true)
        }
        if (index != -1) {
            current[index] = updatedItem
        } else {
            current.add(0, updatedItem)
        }
        _activities.value = current
        _selectedActivity.value = updatedItem
        refreshMemories()
    }

    fun deleteActivity(id: Int, title: String) {
        val current = _activities.value.toMutableList()
        val cleanTitle = title.trim()
        current.removeAll {
            (id > 0 && it.id == id) || (cleanTitle.isNotBlank() && it.title.equals(cleanTitle, ignoreCase = true))
        }
        _activities.value = current
        if (_selectedActivity.value?.id == id || (cleanTitle.isNotBlank() && _selectedActivity.value?.title.equals(cleanTitle, ignoreCase = true))) {
            _selectedActivity.value = current.firstOrNull()
        }
        refreshMemories()
    }

    fun getCategoryIcon(category: String?): String {
        return when (category?.trim()?.lowercase(Locale.ROOT)) {
            "college" -> "🎓"
            "friends" -> "👥"
            "health" -> "❤️"
            "work" -> "💼"
            "family" -> "🏠"
            "travel" -> "✈️"
            "exercise" -> "🏃"
            "learning" -> "📚"
            "hobbies" -> "🎨"
            "voice diary" -> "🎙️"
            else -> "📝"
        }
    }

    fun getMoodEmoji(mood: String?): String {
        val clean = mood?.trim()?.lowercase(Locale.ROOT) ?: return "⚪"
        return when {
            clean.contains("sad") || clean.contains("unhappy") || clean.contains("crying") || clean.contains("grief") || clean.contains("depress") -> "😢"
            clean.contains("happy") || clean.contains("joy") || clean.contains("cheerful") || clean.contains("glad") -> "😊"
            clean.contains("calm") || clean.contains("peace") || clean.contains("relax") || clean.contains("serene") -> "😌"
            clean.contains("focus") || clean.contains("study") || clean.contains("work") || clean.contains("productiv") -> "🎯"
            clean.contains("reflect") || clean.contains("thought") || clean.contains("ponder") || clean.contains("introspect") -> "🤔"
            clean.contains("excite") || clean.contains("energetic") || clean.contains("thrill") || clean.contains("hyped") -> "🤩"
            clean.contains("anxious") || clean.contains("stress") || clean.contains("nervous") || clean.contains("worr") || clean.contains("fear") -> "😰"
            clean.contains("grate") || clean.contains("thank") || clean.contains("bless") -> "🙏"
            clean.contains("tire") || clean.contains("exhaust") || clean.contains("sleep") || clean.contains("drain") -> "🥱"
            clean.contains("angr") || clean.contains("frustrat") || clean.contains("mad") || clean.contains("annoy") -> "😠"
            clean.contains("love") || clean.contains("romant") || clean.contains("sweet") -> "🥰"
            clean.contains("motivat") || clean.contains("strong") || clean.contains("confiden") || clean.contains("empower") -> "💪"
            clean.contains("bored") || clean.contains("neutral") -> "😐"
            clean.isBlank() || clean == "unspecified" || clean == "not specified" -> "⚪"
            else -> "💭"
        }
    }
}
