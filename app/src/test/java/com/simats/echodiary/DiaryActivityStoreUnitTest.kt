package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.DiaryEntryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiaryActivityStoreUnitTest {

    @Test
    fun testInitialActivitiesPresent() {
        val activities = DiaryActivityStore.activities.value
        assertTrue("Initial activities should not be empty", activities.isNotEmpty())
        assertTrue(
            "Default activities should contain 'Weekend with family'",
            activities.any { it.title == "Weekend with family" }
        )
    }

    @Test
    fun testAddActivityPushesToFront() {
        val newEntry = DiaryEntryItem(
            id = 99,
            title = "Morning Coffee & Code",
            category = "Learning",
            date = "Oct 12, 2026",
            description = "Started learning Jetpack Compose animations.",
            icon = DiaryActivityStore.getCategoryIcon("Learning"),
            mood = "Focused"
        )

        DiaryActivityStore.addActivity(newEntry)

        val updatedActivities = DiaryActivityStore.activities.value
        assertEquals("Morning Coffee & Code", updatedActivities.first().title)
        assertEquals("Learning", updatedActivities.first().category)
        assertEquals("📚", updatedActivities.first().icon)
        assertEquals("Oct 12, 2026", updatedActivities.first().date)
        assertEquals(newEntry, DiaryActivityStore.selectedActivity.value)
    }

    @Test
    fun testUpdateActivityPreservesChangedDetailsInMemory() {
        val testEntry = DiaryEntryItem(
            id = 100,
            title = "Original Title",
            category = "Work",
            date = "Oct 13, 2026",
            description = "Original Description",
            icon = "💼",
            mood = "Neutral"
        )
        DiaryActivityStore.addActivity(testEntry)

        val updatedEntry = testEntry.copy(
            title = "Updated Title for Memory",
            description = "Updated Description with more reflections.",
            category = "Health",
            icon = DiaryActivityStore.getCategoryIcon("Health"),
            mood = "Reflective"
        )

        DiaryActivityStore.updateActivity(testEntry.title, updatedEntry)

        val found = DiaryActivityStore.activities.value.find { it.id == 100 }
        assertNotNull("Updated entry should exist in activities", found)
        assertEquals("Updated Title for Memory", found?.title)
        assertEquals("Updated Description with more reflections.", found?.description)
        assertEquals("Health", found?.category)
        assertEquals("❤️", found?.icon)
        assertEquals("Reflective", found?.mood)
    }

    @Test
    fun testDeleteActivityRemovesEntryFromMemory() {
        val deleteCandidate = DiaryEntryItem(
            id = 101,
            title = "Entry to Delete",
            category = "Other",
            date = "Oct 14, 2026",
            description = "This entry will be deleted.",
            icon = "📝",
            mood = "Sad"
        )
        DiaryActivityStore.addActivity(deleteCandidate)
        assertTrue(DiaryActivityStore.activities.value.any { it.id == 101 })

        DiaryActivityStore.deleteActivity(101, "Entry to Delete")
        assertFalse(
            "Entry should be removed from memory after deletion",
            DiaryActivityStore.activities.value.any { it.id == 101 || it.title == "Entry to Delete" }
        )
    }

    @Test
    fun testCategoryIconHelper() {
        assertEquals("🎓", DiaryActivityStore.getCategoryIcon("College"))
        assertEquals("👥", DiaryActivityStore.getCategoryIcon("Friends"))
        assertEquals("❤️", DiaryActivityStore.getCategoryIcon("Health"))
        assertEquals("💼", DiaryActivityStore.getCategoryIcon("Work"))
        assertEquals("🏠", DiaryActivityStore.getCategoryIcon("Family"))
        assertEquals("✈️", DiaryActivityStore.getCategoryIcon("Travel"))
        assertEquals("🏃", DiaryActivityStore.getCategoryIcon("Exercise"))
        assertEquals("📚", DiaryActivityStore.getCategoryIcon("Learning"))
        assertEquals("🎨", DiaryActivityStore.getCategoryIcon("Hobbies"))
        assertEquals("🎙️", DiaryActivityStore.getCategoryIcon("Voice Diary"))
        assertEquals("📝", DiaryActivityStore.getCategoryIcon("Unknown Category"))
    }

    @Test
    fun testWeeklyMemoryUpdate() {
        val newTitle = "A productive and insightful week."
        val newNarrative = "You focused on development, spent quality time outdoors, and met up with friends."

        DiaryActivityStore.updateWeeklyMemory(newTitle, newNarrative)

        assertEquals(newTitle, DiaryActivityStore.weeklyMemoryTitle.value)
        assertEquals(newNarrative, DiaryActivityStore.weeklyMemoryNarrative.value)
    }
}
