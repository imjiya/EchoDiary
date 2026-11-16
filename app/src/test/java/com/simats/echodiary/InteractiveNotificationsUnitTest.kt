package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.model.RegisterTokenRequest
import com.simats.echodiary.data.model.SendReminderRequest
import com.simats.echodiary.ui.activity.DiaryEntryItem
import com.simats.echodiary.util.NotificationHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InteractiveNotificationsUnitTest {

    @Before
    fun setup() {
        DiaryActivityStore.resetToDefaults()
    }

    @After
    fun tearDown() {
        DiaryActivityStore.resetToDefaults()
    }

    @Test
    fun testNotificationHelperConstants() {
        assertEquals("echodiary_reminders_channel", NotificationHelper.CHANNEL_ID)
        assertEquals("extra_navigate_to", NotificationHelper.EXTRA_NAVIGATE_TO)
        assertEquals("write_entry", NotificationHelper.DESTINATION_WRITE_ENTRY)
        assertEquals("voice_recording", NotificationHelper.DESTINATION_VOICE_RECORD)
        assertEquals("photo_memories", NotificationHelper.DESTINATION_PHOTO_MEMORIES)
        assertEquals("home", NotificationHelper.DESTINATION_HOME)
        assertEquals("com.simats.echodiary.ACTION_QUICK_REPLY", NotificationHelper.ACTION_QUICK_REPLY)
        assertEquals("key_quick_memory_text", NotificationHelper.KEY_QUICK_MEMORY_TEXT)
    }

    @Test
    fun testFcmTokenStorageInDiaryActivityStore() {
        val testToken = "fcm_device_token_xyz_98765"
        DiaryActivityStore.setFcmToken(testToken)
        assertEquals(testToken, DiaryActivityStore.fcmToken.value)
    }

    @Test
    fun testQuickThoughtMemoryAddedToActivities() {
        val initialCount = DiaryActivityStore.activities.value.size
        val quickText = "Had a wonderful conversation with my mentor this afternoon."

        val quickMemory = DiaryEntryItem(
            id = 501,
            title = "Quick Reflection",
            category = "Learning",
            date = "Oct 20, 2026",
            description = quickText,
            icon = "💭",
            mood = "Inspired"
        )
        DiaryActivityStore.addActivity(quickMemory)

        val updatedActivities = DiaryActivityStore.activities.value
        assertEquals(initialCount + 1, updatedActivities.size)

        val retrieved = updatedActivities.find { it.id == 501 }
        assertNotNull(retrieved)
        assertEquals("Quick Reflection", retrieved?.title)
        assertEquals(quickText, retrieved?.description)
        assertEquals("Learning", retrieved?.category)
        assertEquals("💭", retrieved?.icon)
    }

    @Test
    fun testNotificationModels() {
        val tokenReq = RegisterTokenRequest(fcmToken = "test_fcm_token_123")
        assertEquals("test_fcm_token_123", tokenReq.fcmToken)
        assertEquals("android", tokenReq.deviceType)

        val reminderReq = SendReminderRequest(
            type = "daily_memory_reminder",
            title = "EchoDiary · Time to add memory for this day ✨",
            message = "Capture your thoughts today."
        )
        assertEquals("daily_memory_reminder", reminderReq.type)
        assertEquals("EchoDiary · Time to add memory for this day ✨", reminderReq.title)
    }
}

