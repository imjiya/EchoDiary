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
import java.util.Locale

object DiaryActivityStore {

    private val defaultActivities = listOf(
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
            mood = "Joyful"
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

    private val _activities = MutableStateFlow<List<DiaryEntryItem>>(defaultActivities)
    val activities: StateFlow<List<DiaryEntryItem>> = _activities.asStateFlow()

    private val _selectedActivity = MutableStateFlow<DiaryEntryItem?>(defaultActivities.first())
    val selectedActivity: StateFlow<DiaryEntryItem?> = _selectedActivity.asStateFlow()

    // ------------------------------------------------------------------------
    // User Profile & Account State
    // ------------------------------------------------------------------------
    private val _userFullName = MutableStateFlow("Alex Morgan")
    val userFullName: StateFlow<String> = _userFullName.asStateFlow()

    private val _userEmail = MutableStateFlow("alex.morgan@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userInitials = MutableStateFlow("AM")
    val userInitials: StateFlow<String> = _userInitials.asStateFlow()

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

    // ------------------------------------------------------------------------
    // Weekly Memory State
    // ------------------------------------------------------------------------
    private val _weeklyMemoryTitle = MutableStateFlow("A week of progress and connection.")
    val weeklyMemoryTitle: StateFlow<String> = _weeklyMemoryTitle.asStateFlow()

    private val _weeklyMemoryNarrative = MutableStateFlow(
        "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend."
    )
    val weeklyMemoryNarrative: StateFlow<String> = _weeklyMemoryNarrative.asStateFlow()

    private val _weeklyMemoryDateRange = MutableStateFlow("OCT 5–11, 2026 · WEEKLY DRAFT")
    val weeklyMemoryDateRange: StateFlow<String> = _weeklyMemoryDateRange.asStateFlow()

    private val _weeklyHighlights = MutableStateFlow<List<String>>(
        listOf("College project", "Friends & family", "Presentation prep", "Calm walk")
    )
    val weeklyHighlights: StateFlow<List<String>> = _weeklyHighlights.asStateFlow()

    private val _weeklyMoodSummary = MutableStateFlow("1 explicitly recorded mood: Calm, on Oct 7. The other 4 entries have no mood specified.")
    val weeklyMoodSummary: StateFlow<String> = _weeklyMoodSummary.asStateFlow()

    private val _weeklyEntriesCount = MutableStateFlow(5)
    val weeklyEntriesCount: StateFlow<Int> = _weeklyEntriesCount.asStateFlow()

    private val _weeklyCategoriesCount = MutableStateFlow(5)
    val weeklyCategoriesCount: StateFlow<Int> = _weeklyCategoriesCount.asStateFlow()

    // ------------------------------------------------------------------------
    // Monthly Memory State
    // ------------------------------------------------------------------------
    private val _monthlyMonthLabel = MutableStateFlow("October 2026")
    val monthlyMonthLabel: StateFlow<String> = _monthlyMonthLabel.asStateFlow()

    private val _monthlySummaryTitle = MutableStateFlow("October, in your words")
    val monthlySummaryTitle: StateFlow<String> = _monthlySummaryTitle.asStateFlow()

    private val _monthlySummaryText = MutableStateFlow(
        "This month, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend."
    )
    val monthlySummaryText: StateFlow<String> = _monthlySummaryText.asStateFlow()

    private val _monthlyCategoryBreakdown = MutableStateFlow<List<CategoryBreakdownItem>>(
        listOf(
            CategoryBreakdownItem("College", 1, 1f),
            CategoryBreakdownItem("Friends", 1, 1f),
            CategoryBreakdownItem("Health", 1, 1f),
            CategoryBreakdownItem("Work", 1, 1f),
            CategoryBreakdownItem("Family", 1, 1f)
        )
    )
    val monthlyCategoryBreakdown: StateFlow<List<CategoryBreakdownItem>> = _monthlyCategoryBreakdown.asStateFlow()

    private val _monthlyEntriesCount = MutableStateFlow(5)
    val monthlyEntriesCount: StateFlow<Int> = _monthlyEntriesCount.asStateFlow()

    private val _monthlyCategoriesCount = MutableStateFlow(5)
    val monthlyCategoriesCount: StateFlow<Int> = _monthlyCategoriesCount.asStateFlow()

    private val _monthlyFooterText = MutableStateFlow("Highlights: project work, lunch with friends, a Calm walk, presentation preparation and family time.")
    val monthlyFooterText: StateFlow<String> = _monthlyFooterText.asStateFlow()

    // ------------------------------------------------------------------------
    // Yearly Memory State
    // ------------------------------------------------------------------------
    private val _yearlyYearLabel = MutableStateFlow("2026")
    val yearlyYearLabel: StateFlow<String> = _yearlyYearLabel.asStateFlow()

    private val _yearlyChapterTitle = MutableStateFlow("One small chapter of 2026.")
    val yearlyChapterTitle: StateFlow<String> = _yearlyChapterTitle.asStateFlow()

    private val _yearlyChapterText = MutableStateFlow(
        "Across 2026, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend."
    )
    val yearlyChapterText: StateFlow<String> = _yearlyChapterText.asStateFlow()

    private val _yearlyCategories = MutableStateFlow<List<String>>(
        listOf("College", "Friends", "Health", "Work", "Family")
    )
    val yearlyCategories: StateFlow<List<String>> = _yearlyCategories.asStateFlow()

    private val _yearlySupportingRecords = MutableStateFlow<List<SupportingRecord>>(
        listOf(
            SupportingRecord("Lectures and project", "Oct 5"),
            SupportingRecord("Lunch with friends", "Oct 6"),
            SupportingRecord("Evening walk", "Oct 7"),
            SupportingRecord("Presentation preparation", "Oct 8"),
            SupportingRecord("Weekend with family", "Oct 10")
        )
    )
    val yearlySupportingRecords: StateFlow<List<SupportingRecord>> = _yearlySupportingRecords.asStateFlow()

    private val _yearlyFooterText = MutableStateFlow("Topics come from these 5 records; grounded directly in what you recorded.")
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
        generateWeeklySummary(null)
        generateMonthlySummary(null)
        generateYearlySummary(null)
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
        val titles = entries.map { it.title }
        val moodSummary = synthesizeMoodSummary(entries)

        val title = if (entries.isNotEmpty()) {
            "A week of progress and connection."
        } else {
            "Your weekly memory"
        }

        val narrative = if (count > 0) {
            val highlightsList = titles.take(4).joinToString(", ")
            "This week, you recorded $count entries across ${categories.joinToString(", ")}. You logged $highlightsList. $moodSummary"
        } else {
            "No entries recorded for this week yet. Add daily diary entries to see your story come together."
        }

        val highlights = (categories.take(2) + titles.take(2)).distinct()

        _weeklyMemoryTitle.value = title
        _weeklyMemoryNarrative.value = narrative
        _weeklyHighlights.value = highlights
        _weeklyMoodSummary.value = moodSummary
        _weeklyEntriesCount.value = count
        _weeklyCategoriesCount.value = categories.size
        _weeklyMemoryDateRange.value = "OCT 5–11, 2026 · WEEKLY DRAFT"

        scope?.launch {
            try {
                val response = EchoDiaryApp.getMemoryRepository().generateMemory(type = "weekly", save = true)
                response.onSuccess { data ->
                    data.title?.let { if (it.isNotBlank()) _weeklyMemoryTitle.value = it }
                    data.narrative?.let { if (it.isNotBlank()) _weeklyMemoryNarrative.value = it }
                    data.dateRange?.let { if (it.isNotBlank()) _weeklyMemoryDateRange.value = "$it · WEEKLY DRAFT" }
                }
            } catch (_: Exception) {}
        }
    }

    fun generateMonthlySummary(scope: CoroutineScope? = null) {
        val entries = _activities.value
        val count = entries.size
        val categories = entries.map { it.category }.distinct()
        val moodSummary = synthesizeMoodSummary(entries)

        val title = "October, in your words"
        val narrative = if (count > 0) {
            val highlightsList = entries.take(4).joinToString(", ") { it.title }
            "This month, you logged $count entries spanning ${categories.joinToString(", ")}. Notable moments included $highlightsList. $moodSummary"
        } else {
            "No entries recorded for this month yet. Capture your days to reveal your monthly story."
        }

        val breakdown = categories.map { cat ->
            val catCount = entries.count { it.category.equals(cat, ignoreCase = true) }
            val progress = if (count > 0) catCount.toFloat() / count.toFloat() else 0f
            CategoryBreakdownItem(name = cat, count = catCount, progress = progress)
        }

        _monthlySummaryTitle.value = title
        _monthlySummaryText.value = narrative
        _monthlyCategoryBreakdown.value = breakdown
        _monthlyEntriesCount.value = count
        _monthlyCategoriesCount.value = categories.size
        _monthlyFooterText.value = "Highlights: ${entries.take(4).joinToString(", ") { it.title }}."

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
        val supporting = entries.map { SupportingRecord(it.title, it.date) }
        val moodSummary = synthesizeMoodSummary(entries)

        val title = "One small chapter of 2026."
        val narrative = if (count > 0) {
            val topTitles = entries.take(4).joinToString(", ") { it.title }
            "Across 2026, your diary holds $count recorded reflections across ${categories.joinToString(", ")}. You revisited moments such as $topTitles. $moodSummary"
        } else {
            "No entries recorded for this year yet. Write or speak your entries to build your year in reflection."
        }

        _yearlyChapterTitle.value = title
        _yearlyChapterText.value = narrative
        _yearlyCategories.value = categories
        _yearlySupportingRecords.value = supporting
        _yearlyFooterText.value = "Topics come from these $count records; grounded directly in what you recorded."

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

    fun updateWeeklyMemory(title: String, narrative: String) {
        if (title.isNotBlank()) _weeklyMemoryTitle.value = title
        if (narrative.isNotBlank()) _weeklyMemoryNarrative.value = narrative
    }

    fun selectActivity(item: DiaryEntryItem) {
        _selectedActivity.value = item
    }

    fun addActivity(item: DiaryEntryItem) {
        val current = _activities.value.toMutableList()
        current.add(0, item)
        _activities.value = current
        _selectedActivity.value = item
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
}
