package com.simats.echodiary.data.local

import com.simats.echodiary.ui.activity.DiaryEntryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // Weekly memory summary
    private val _weeklyMemoryTitle = MutableStateFlow("A week of progress and connection.")
    val weeklyMemoryTitle: StateFlow<String> = _weeklyMemoryTitle.asStateFlow()

    private val _weeklyMemoryNarrative = MutableStateFlow(
        "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend."
    )
    val weeklyMemoryNarrative: StateFlow<String> = _weeklyMemoryNarrative.asStateFlow()

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
