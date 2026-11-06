package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.ActivityAndMoodViewModel
import com.simats.echodiary.ui.activity.DiaryEntryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ActivityAndMoodUnitTest {

    private var testScope = CoroutineScope(Dispatchers.Default)

    @Before
    fun setup() {
        testScope = CoroutineScope(Dispatchers.Default)
        DiaryActivityStore.resetToDefaults()
    }

    @After
    fun tearDown() {
        testScope.cancel()
        DiaryActivityStore.resetToDefaults()
    }

    @Test
    fun testInitialInsightsMatchActualActivitiesAndMoods() {
        val viewModel = ActivityAndMoodViewModel(scope = testScope)
        val state = viewModel.uiState.value

        assertEquals(5, state.activitiesCount)
        assertEquals(5, state.categoriesCount)
        assertEquals(5, state.categories.size)

        val focusedMood = state.moodBreakdown.find { it.name == "Focused" }
        assertNotNull("Focused mood should exist in breakdown", focusedMood)
        assertEquals(2, focusedMood?.count)
        assertEquals(5, focusedMood?.total)
        assertEquals(2f / 5f, focusedMood?.progress ?: 0f, 0.001f)

        val happyMood = state.moodBreakdown.find { it.name == "Happy" }
        assertNotNull("Happy mood should exist", happyMood)
        assertEquals(1, happyMood?.count)
        assertEquals(1f / 5f, happyMood?.progress ?: 0f, 0.001f)

        val calmMood = state.moodBreakdown.find { it.name == "Calm" }
        assertNotNull("Calm mood should exist", calmMood)
        assertEquals(1, calmMood?.count)

        val joyfulMood = state.moodBreakdown.find { it.name == "Joyful" }
        assertNotNull("Joyful mood should exist", joyfulMood)
        assertEquals(1, joyfulMood?.count)

        assertTrue(state.moodNote.contains("All 5 entries have explicit recorded moods"))
        assertTrue(state.sourceText.contains("Weekend with family"))
    }

    @Test
    fun testInsightsUpdateWhenNewEntryWithMoodAdded() {
        val viewModel = ActivityAndMoodViewModel(scope = testScope)

        val newEntry = DiaryEntryItem(
            id = 99,
            title = "Morning Meditation",
            category = "Health",
            date = "Oct 11, 2026",
            description = "Mindfulness practice",
            icon = "🧘",
            mood = "Calm"
        )
        DiaryActivityStore.addActivity(newEntry)
        viewModel.onActivitiesChanged(DiaryActivityStore.activities.value)

        val state = viewModel.uiState.value

        assertEquals(6, state.activitiesCount)

        val healthCat = state.categories.find { it.name == "Health" }
        assertEquals(2, healthCat?.count)
        assertEquals(2f / 6f, healthCat?.progress ?: 0f, 0.001f)

        val calmMood = state.moodBreakdown.find { it.name == "Calm" }
        assertEquals(2, calmMood?.count)
        assertEquals(6, calmMood?.total)
        assertEquals(2f / 6f, calmMood?.progress ?: 0f, 0.001f)
    }

    @Test
    fun testInsightsUpdateWhenEntryAddedWithoutMood() {
        val viewModel = ActivityAndMoodViewModel(scope = testScope)

        val unrecordedMoodEntry = DiaryEntryItem(
            id = 100,
            title = "Library Book Return",
            category = "Errands",
            date = "Oct 11, 2026",
            description = "Returned library books",
            icon = "📚",
            mood = ""
        )
        DiaryActivityStore.addActivity(unrecordedMoodEntry)
        viewModel.onActivitiesChanged(DiaryActivityStore.activities.value)

        val state = viewModel.uiState.value

        assertEquals(6, state.activitiesCount)
        assertEquals(6, state.categoriesCount)

        val unspecified = state.moodBreakdown.find { it.name == "Unspecified" }
        assertNotNull("Unspecified entry should be tracked in breakdown", unspecified)
        assertEquals(1, unspecified?.count)
        assertEquals(6, unspecified?.total)

        assertTrue(state.moodNote.contains("1 entry is unspecified"))
    }

    @Test
    fun testInsightsUpdateWhenActivityDeleted() {
        val viewModel = ActivityAndMoodViewModel(scope = testScope)

        DiaryActivityStore.deleteActivity(5, "Weekend with family")
        viewModel.onActivitiesChanged(DiaryActivityStore.activities.value)

        val state = viewModel.uiState.value

        assertEquals(4, state.activitiesCount)
        assertEquals(4, state.categoriesCount)

        val happyMood = state.moodBreakdown.find { it.name == "Happy" }
        assertEquals(null, happyMood)

        val focused = state.moodBreakdown.find { it.name == "Focused" }
        assertEquals(2, focused?.count)
        assertEquals(4, focused?.total)
        assertEquals(0.5f, focused?.progress ?: 0f, 0.001f)
    }

    @Test
    fun testTabSwitchingUpdatesDateRange() {
        val viewModel = ActivityAndMoodViewModel(scope = testScope)

        viewModel.onTabSelected("Week")
        assertEquals("Week", viewModel.uiState.value.selectedTab)
        assertTrue(viewModel.uiState.value.dateRangeText.contains("Oct"))

        viewModel.onTabSelected("Month")
        assertEquals("Month", viewModel.uiState.value.selectedTab)
        assertTrue(viewModel.uiState.value.dateRangeText.contains("October 2026"))

        viewModel.onTabSelected("Year")
        assertEquals("Year", viewModel.uiState.value.selectedTab)
        assertTrue(viewModel.uiState.value.dateRangeText.contains("2026"))
    }
}
