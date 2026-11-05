package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.profile.NotificationsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationsUnitTest {

    @Test
    fun testNotificationsViewModelInitialState() {
        val viewModel = NotificationsViewModel()
        val state = viewModel.uiState.value
        assertEquals(DiaryActivityStore.diaryRemindersEnabled.value, state.diaryRemindersEnabled)
        assertEquals(DiaryActivityStore.weeklyMemoryRemindersEnabled.value, state.weeklyMemoryRemindersEnabled)
        assertEquals(DiaryActivityStore.dailyReminderTime.value, state.dailyReminderTime)
    }

    @Test
    fun testTogglesAndUpdateNotificationSettings() {
        val viewModel = NotificationsViewModel()

        viewModel.onDiaryRemindersToggled(false)
        assertFalse(viewModel.uiState.value.diaryRemindersEnabled)

        viewModel.onWeeklyMemoryRemindersToggled(true)
        assertTrue(viewModel.uiState.value.weeklyMemoryRemindersEnabled)

        viewModel.onSummaryUpdatesToggled(true)
        assertTrue(viewModel.uiState.value.summaryUpdatesEnabled)

        viewModel.onDailyReminderTimeChanged("9:30 PM")
        assertEquals("9:30 PM", viewModel.uiState.value.dailyReminderTime)

        viewModel.onWeeklyReminderTimeChanged("Saturday · 7:00 PM")
        assertEquals("Saturday · 7:00 PM", viewModel.uiState.value.weeklyReminderTime)

        viewModel.savePreferences()
        assertTrue(viewModel.uiState.value.isSavedSuccess)
        assertEquals("Notification preferences saved successfully!", viewModel.uiState.value.statusMessage)

        // Verify DiaryActivityStore received updates
        assertFalse(DiaryActivityStore.diaryRemindersEnabled.value)
        assertTrue(DiaryActivityStore.weeklyMemoryRemindersEnabled.value)
        assertTrue(DiaryActivityStore.summaryUpdatesEnabled.value)
        assertEquals("9:30 PM", DiaryActivityStore.dailyReminderTime.value)
        assertEquals("Saturday · 7:00 PM", DiaryActivityStore.weeklyReminderTime.value)
        assertEquals("Weekly: Saturday · 7:00 PM", DiaryActivityStore.notificationSummary.value)
    }

    @Test
    fun testNotificationSummaryFormatting() {
        // Both enabled
        DiaryActivityStore.updateNotificationSettings(
            diary = true,
            weekly = true,
            summary = false,
            dailyTime = "8:00 PM",
            weeklyTime = "Sunday · 8:00 PM"
        )
        assertEquals("Daily at 8:00 PM · Sunday", DiaryActivityStore.notificationSummary.value)

        // Only diary enabled
        DiaryActivityStore.updateNotificationSettings(
            diary = true,
            weekly = false,
            summary = false,
            dailyTime = "8:00 PM",
            weeklyTime = "Sunday · 8:00 PM"
        )
        assertEquals("Daily at 8:00 PM", DiaryActivityStore.notificationSummary.value)

        // All disabled
        DiaryActivityStore.updateNotificationSettings(
            diary = false,
            weekly = false,
            summary = false,
            dailyTime = "8:00 PM",
            weeklyTime = "Sunday · 8:00 PM"
        )
        assertEquals("Reminders paused", DiaryActivityStore.notificationSummary.value)
    }
}
