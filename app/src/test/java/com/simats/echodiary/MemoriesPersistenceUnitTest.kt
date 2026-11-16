package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.DiaryEntryItem
import com.simats.echodiary.ui.activity.MemoriesViewModel
import com.simats.echodiary.ui.activity.MonthlyMemoryViewModel
import com.simats.echodiary.ui.activity.YearlyMemoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MemoriesPersistenceUnitTest {

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
    fun testMemoriesViewModelSavesEntriesForWeeklyMonthlyYearlyTabs() {
        val vm = MemoriesViewModel(scope = testScope)
        Thread.sleep(100)

        // 1. Weekly tab by default
        assertEquals("Weekly", vm.uiState.value.activeFilterTab)
        assertEquals("SAVED WEEKLY MEMORY", vm.uiState.value.memoryCardHeader)
        assertTrue(vm.uiState.value.entries.isNotEmpty())
        assertEquals(DiaryActivityStore.activities.value.size, vm.uiState.value.entries.size)

        // 2. Switch to Monthly tab
        vm.onFilterTabSelected("Monthly")
        assertEquals("Monthly", vm.uiState.value.activeFilterTab)
        assertEquals("SAVED MONTHLY MEMORY", vm.uiState.value.memoryCardHeader)
        assertTrue(vm.uiState.value.entries.isNotEmpty())
        assertEquals(DiaryActivityStore.activities.value.size, vm.uiState.value.entries.size)

        // 3. Switch to Yearly tab
        vm.onFilterTabSelected("Yearly")
        assertEquals("Yearly", vm.uiState.value.activeFilterTab)
        assertEquals("SAVED YEARLY MEMORY", vm.uiState.value.memoryCardHeader)
        assertTrue(vm.uiState.value.entries.isNotEmpty())
        assertEquals(DiaryActivityStore.activities.value.size, vm.uiState.value.entries.size)
    }

    @Test
    fun testAddingNewSadEntryUpdatesAllWeeklyMonthlyYearlyMemories() {
        val initialCount = DiaryActivityStore.activities.value.size
        val newSadEntry = DiaryEntryItem(
            id = 999,
            title = "Rough exam day",
            category = "College",
            date = "Oct 18, 2026",
            description = "Felt overwhelmed after the engineering physics paper.",
            icon = "🎓",
            mood = "Sad"
        )

        DiaryActivityStore.addActivity(newSadEntry)
        Thread.sleep(100)

        assertEquals(initialCount + 1, DiaryActivityStore.activities.value.size)
        assertEquals("Sad", DiaryActivityStore.activities.value.first().mood)
        assertEquals("😢", DiaryActivityStore.getMoodEmoji(DiaryActivityStore.activities.value.first().mood))

        // Check MemoriesViewModel
        val memoriesVm = MemoriesViewModel(scope = testScope)
        Thread.sleep(100)

        // Weekly tab
        memoriesVm.onFilterTabSelected("Weekly")
        assertTrue(memoriesVm.uiState.value.entries.any { it.title == "Rough exam day" && it.mood == "Sad" })

        // Monthly tab
        memoriesVm.onFilterTabSelected("Monthly")
        assertTrue(memoriesVm.uiState.value.entries.any { it.title == "Rough exam day" && it.mood == "Sad" })

        // Yearly tab
        memoriesVm.onFilterTabSelected("Yearly")
        assertTrue(memoriesVm.uiState.value.entries.any { it.title == "Rough exam day" && it.mood == "Sad" })

        // MonthlyMemoryViewModel
        val monthlyVm = MonthlyMemoryViewModel(testScope)
        Thread.sleep(100)
        assertTrue(monthlyVm.uiState.value.entries.any { it.title == "Rough exam day" && it.mood == "Sad" })

        // YearlyMemoryViewModel
        val yearlyVm = YearlyMemoryViewModel(testScope)
        Thread.sleep(100)
        assertTrue(yearlyVm.uiState.value.entries.any { it.title == "Rough exam day" && it.mood == "Sad" })
    }

    @Test
    fun testMonthlyAndYearlyViewModelsExposeSavedEntriesWithMoodEmoji() {
        val monthlyVm = MonthlyMemoryViewModel(testScope)
        val yearlyVm = YearlyMemoryViewModel(testScope)
        Thread.sleep(100)

        assertFalse(monthlyVm.uiState.value.entries.isEmpty())
        assertFalse(yearlyVm.uiState.value.entries.isEmpty())

        val firstEntry = monthlyVm.uiState.value.entries.first()
        val emoji = DiaryActivityStore.getMoodEmoji(firstEntry.mood)
        assertNotNull(emoji)
        assertTrue(emoji.isNotBlank())
    }
}
