package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.DiaryEntryItem
import com.simats.echodiary.ui.activity.PhotoMemoriesNavigationEvent
import com.simats.echodiary.ui.activity.PhotoMemoriesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PhotoMemoriesUnitTest {

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
    fun testInitialPhotoMemoriesLoadsDefaultWithRespectiveDateAndMemory() {
        val viewModel = PhotoMemoriesViewModel(scope = testScope)
        val state = viewModel.uiState.value

        assertTrue("Should have at least 1 photo memory initially", state.photoMemories.isNotEmpty())
        assertEquals(1, state.totalPhotosCount)

        val firstMemory = state.photoMemories.first()
        assertEquals("Lunch with friends", firstMemory.title)
        assertEquals("Oct 6, 2026", firstMemory.date)
        assertEquals("October 6, 2026 · 1 photo", firstMemory.dateHeader)
        assertEquals("Friends", firstMemory.category)
        assertEquals("Had lunch with friends after class.", firstMemory.description)
        assertTrue(firstMemory.hasPhoto)
    }

    @Test
    fun testAddEntryWithPhotoAppearsInPhotoMemoriesWithRespectiveDateAndMemory() {
        val viewModel = PhotoMemoriesViewModel(scope = testScope)
        val photoPath = "/data/user/0/com.simats.echodiary/files/diary_photos/photo_mountain.jpg"

        val newMemoryWithPhoto = DiaryEntryItem(
            id = 301,
            title = "Mountain Trek",
            category = "Travel",
            date = "Oct 18, 2026",
            description = "Climbed the peak and took scenic photos.",
            icon = "✈️",
            mood = "Adventurous",
            hasPhoto = true,
            photoUri = photoPath
        )

        DiaryActivityStore.addActivity(newMemoryWithPhoto)
        viewModel.loadPhotoMemories(DiaryActivityStore.activities.value)

        val state = viewModel.uiState.value
        assertEquals(2, state.totalPhotosCount)

        val trekMemory = state.photoMemories.find { it.id == 301 }
        assertNotNull("Newly added photo memory should be present", trekMemory)
        assertEquals("Mountain Trek", trekMemory?.title)
        assertEquals("October 18, 2026 · 1 photo", trekMemory?.dateHeader)
        assertEquals(photoPath, trekMemory?.photoUri)
        assertEquals("Travel", trekMemory?.category)
        assertEquals("Mountain Trek · Travel", trekMemory?.imageCaption)
        assertTrue(trekMemory?.hasPhoto == true)
    }

    @Test
    fun testDeletePhotoEntryRemovesFromPhotoMemories() {
        val viewModel = PhotoMemoriesViewModel(scope = testScope)
        val initialCount = viewModel.uiState.value.totalPhotosCount

        // Delete default lunch activity
        DiaryActivityStore.deleteActivity(2, "Lunch with friends")
        viewModel.loadPhotoMemories(DiaryActivityStore.activities.value)

        val state = viewModel.uiState.value
        assertEquals(initialCount - 1, state.totalPhotosCount)
        assertFalse(state.photoMemories.any { it.title == "Lunch with friends" })
    }

    @Test
    fun testDateFormattingHelperHandlesVariousDateFormats() {
        assertEquals(
            "October 6, 2026 · 1 photo",
            PhotoMemoriesViewModel.formatPhotoDateHeader("Oct 6, 2026")
        )
        assertEquals(
            "October 15, 2026 · 1 photo",
            PhotoMemoriesViewModel.formatPhotoDateHeader("2026-10-15")
        )
        assertEquals(
            "November 20, 2026 · 1 photo",
            PhotoMemoriesViewModel.formatPhotoDateHeader("Nov 20, 2026")
        )
        assertEquals(
            "December 25, 2026 · 1 photo",
            PhotoMemoriesViewModel.formatPhotoDateHeader("Dec 25, 2026")
        )
    }

    @Test
    fun testOnEntryClickedSelectsActivityInStore() {
        val viewModel = PhotoMemoriesViewModel(scope = testScope)
        val item = DiaryEntryItem(
            id = 405,
            title = "Sunset at Lake",
            category = "Health",
            date = "Oct 19, 2026",
            description = "Quiet evening by the lake.",
            icon = "❤️",
            mood = "Peaceful",
            hasPhoto = true,
            photoUri = "/files/photos/lake.jpg"
        )
        DiaryActivityStore.addActivity(item)

        viewModel.onEntryClicked(item)

        val selected = DiaryActivityStore.selectedActivity.value
        assertNotNull(selected)
        assertEquals(405, selected?.id)
        assertEquals("Sunset at Lake", selected?.title)
        assertEquals("/files/photos/lake.jpg", selected?.photoUri)
    }
}
