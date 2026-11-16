package com.simats.echodiary

import com.simats.echodiary.ai.RagMemoryEngine
import com.simats.echodiary.ui.activity.DiaryEntryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RagMemoryEngineUnitTest {

    @Test
    fun testEmptyEntriesFallback() {
        val result = RagMemoryEngine.synthesizeMemory(emptyList(), "weekly")
        assertEquals("Your weekly memory", result.title)
        assertEquals(0, result.retrievedCount)
        assertTrue(result.narrative.contains("No entries recorded"))
        assertTrue(result.highlights.isEmpty())
    }

    @Test
    fun testWeeklyRagSynthesisGroundedInRetrievedEntries() {
        val entries = listOf(
            DiaryEntryItem(
                id = 1,
                title = "Compose Navigation Architecture",
                category = "Learning",
                date = "Oct 12, 2026",
                description = "Implemented nested navigation and deep-linking in Compose.",
                icon = "📚",
                mood = "Focused"
            ),
            DiaryEntryItem(
                id = 2,
                title = "Lunch with Team",
                category = "Friends",
                date = "Oct 13, 2026",
                description = "Celebrated successful sprint release with good friends and food.",
                icon = "👥",
                mood = "Joyful",
                hasPhoto = true,
                photoUri = "/photos/team.jpg"
            ),
            DiaryEntryItem(
                id = 3,
                title = "Sunset Run",
                category = "Health",
                date = "Oct 14, 2026",
                description = "Jogged 5k in the park as the sun was setting.",
                icon = "❤️",
                mood = "Calm"
            )
        )

        val result = RagMemoryEngine.synthesizeMemory(entries, "weekly", "OCT 12–18, 2026")

        assertNotNull(result)
        assertEquals(3, result.retrievedCount)
        assertEquals(3, result.groundedSourceIds.size)

        // Title should be synthesized and reflect themes
        assertTrue(result.title.isNotBlank())

        // Narrative should cite grounded anchor titles
        assertTrue(result.narrative.contains("This week"))
        assertTrue(
            result.narrative.contains("Compose Navigation") ||
            result.narrative.contains("Lunch with Team") ||
            result.narrative.contains("Sunset Run")
        )

        // Highlights should contain anchors
        assertTrue(result.highlights.isNotEmpty())
        assertTrue(result.highlights.any { it.contains("Compose") || it.contains("Lunch") || it.contains("Sunset") })

        // Emotional trajectory
        assertTrue(result.moodSummary.contains("Focused") || result.moodSummary.contains("Joyful") || result.moodSummary.contains("Calm"))

        // Mindful takeaway
        assertTrue(result.mindfulTakeaway.isNotBlank())
    }

    @Test
    fun testMonthlyRagSynthesis() {
        val entries = listOf(
            DiaryEntryItem(
                id = 10,
                title = "Project Pitch",
                category = "Work",
                date = "Oct 1, 2026",
                description = "Delivered the quarterly project pitch to stakeholders.",
                icon = "💼",
                mood = "Energetic"
            ),
            DiaryEntryItem(
                id = 11,
                title = "Art Museum Visit",
                category = "Hobbies",
                date = "Oct 15, 2026",
                description = "Visited modern art gallery.",
                icon = "🎨",
                mood = "Inspired"
            )
        )

        val result = RagMemoryEngine.synthesizeMemory(entries, "monthly", "October 2026")
        assertTrue(result.title.contains("October"))
        assertTrue(result.narrative.contains("Across this month"))
        assertEquals(2, result.retrievedCount)
    }

    @Test
    fun testYearlyRagSynthesis() {
        val entries = listOf(
            DiaryEntryItem(
                id = 20,
                title = "Graduation Day",
                category = "College",
                date = "May 20, 2026",
                description = "Graduated with honors in Computer Science.",
                icon = "🎓",
                mood = "Proud"
            )
        )

        val result = RagMemoryEngine.synthesizeMemory(entries, "yearly", "2026")
        assertTrue(result.title.contains("2026"))
        assertTrue(result.narrative.contains("Throughout this year"))
        assertEquals(1, result.retrievedCount)
    }
}

