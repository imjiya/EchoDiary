package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.ActivityDetailsViewModel
import com.simats.echodiary.ui.activity.DiaryEntryItem
import com.simats.echodiary.ui.activity.EditActivityViewModel
import com.simats.echodiary.ui.activity.WriteDiaryEntryViewModel
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

class ActivityPhotoDisplayUnitTest {

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
    fun testActivityDetailsDisplaysInsertedPhoto() {
        val testPhotoPath = "/data/user/0/com.simats.echodiary/files/diary_photos/photo_123.jpg"
        val entryWithPhoto = DiaryEntryItem(
            id = 201,
            title = "Weekend Hike",
            category = "Health",
            date = "Oct 12, 2026",
            description = "Hiked through the hills and took a photo.",
            icon = "❤️",
            mood = "Energetic",
            hasPhoto = true,
            photoUri = testPhotoPath
        )
        DiaryActivityStore.addActivity(entryWithPhoto)
        DiaryActivityStore.selectActivity(entryWithPhoto)

        val viewModel = ActivityDetailsViewModel(scope = testScope)
        viewModel.setEntryDetails(
            title = entryWithPhoto.title,
            description = entryWithPhoto.description,
            date = entryWithPhoto.date,
            category = entryWithPhoto.category,
            mood = entryWithPhoto.mood,
            id = entryWithPhoto.id,
            photoUri = entryWithPhoto.photoUri,
            hasPhoto = entryWithPhoto.hasPhoto
        )

        val state = viewModel.uiState.value
        assertTrue("hasPhoto should be true in ActivityDetailsUiState", state.hasPhoto)
        assertEquals(testPhotoPath, state.photoUri)
        assertEquals("1 photo attached", state.photoStatus)
    }

    @Test
    fun testActivityDetailsResolvesPhotoFromSelectedStoreActivity() {
        val testPhotoPath = "/data/user/0/com.simats.echodiary/files/diary_photos/photo_999.jpg"
        val entryWithPhoto = DiaryEntryItem(
            id = 202,
            title = "Family Dinner",
            category = "Family",
            date = "Oct 13, 2026",
            description = "Had dinner together.",
            icon = "🏠",
            mood = "Happy",
            hasPhoto = true,
            photoUri = testPhotoPath
        )
        DiaryActivityStore.addActivity(entryWithPhoto)
        DiaryActivityStore.selectActivity(entryWithPhoto)

        val viewModel = ActivityDetailsViewModel(scope = testScope)
        viewModel.setEntryDetails(
            title = entryWithPhoto.title,
            description = entryWithPhoto.description,
            date = entryWithPhoto.date,
            category = entryWithPhoto.category,
            mood = entryWithPhoto.mood,
            id = entryWithPhoto.id
        )

        val state = viewModel.uiState.value
        assertTrue("hasPhoto should be resolved from selected activity", state.hasPhoto)
        assertEquals(testPhotoPath, state.photoUri)
        assertEquals("1 photo attached", state.photoStatus)
    }

    @Test
    fun testActivityDetailsShowsNoPhotoWhenEntryHasNoPhoto() {
        val entryWithoutPhoto = DiaryEntryItem(
            id = 203,
            title = "Study Session",
            category = "College",
            date = "Oct 14, 2026",
            description = "Studied algorithms.",
            icon = "🎓",
            mood = "Focused",
            hasPhoto = false,
            photoUri = null
        )
        DiaryActivityStore.addActivity(entryWithoutPhoto)
        DiaryActivityStore.selectActivity(entryWithoutPhoto)

        val viewModel = ActivityDetailsViewModel(scope = testScope)
        viewModel.setEntryDetails(
            title = entryWithoutPhoto.title,
            description = entryWithoutPhoto.description,
            date = entryWithoutPhoto.date,
            category = entryWithoutPhoto.category,
            mood = entryWithoutPhoto.mood,
            id = entryWithoutPhoto.id
        )

        val state = viewModel.uiState.value
        assertFalse(state.hasPhoto)
        assertEquals("No photo attached", state.photoStatus)
    }

    @Test
    fun testEditActivityPreservesPhotoInMemory() {
        val testPhotoPath = "/data/user/0/com.simats.echodiary/files/diary_photos/photo_555.jpg"
        val originalItem = DiaryEntryItem(
            id = 204,
            title = "Art Workshop",
            category = "Hobbies",
            date = "Oct 15, 2026",
            description = "Painted watercolors.",
            icon = "🎨",
            mood = "Creative",
            hasPhoto = true,
            photoUri = testPhotoPath
        )
        DiaryActivityStore.addActivity(originalItem)
        DiaryActivityStore.selectActivity(originalItem)

        val editViewModel = EditActivityViewModel(scope = testScope)
        editViewModel.setInitialDetails(
            title = originalItem.title,
            description = originalItem.description,
            date = originalItem.date,
            category = originalItem.category,
            mood = originalItem.mood,
            id = originalItem.id,
            photoUri = originalItem.photoUri,
            hasPhoto = originalItem.hasPhoto
        )

        editViewModel.onDescriptionChanged("Painted watercolors and oil landscapes.")
        editViewModel.onSaveClicked()

        val updated = DiaryActivityStore.activities.value.find { it.id == 204 }
        assertNotNull("Updated entry should exist in store", updated)
        assertTrue("hasPhoto should be preserved after edit", updated?.hasPhoto == true)
        assertEquals(testPhotoPath, updated?.photoUri)
    }

    @Test
    fun testActivityDetailsDisplaysMultiplePhotosStatus() {
        val photos = listOf(
            "/data/user/0/com.simats.echodiary/files/diary_photos/p1.jpg",
            "/data/user/0/com.simats.echodiary/files/diary_photos/p2.jpg",
            "/data/user/0/com.simats.echodiary/files/diary_photos/p3.jpg"
        )
        val entry = DiaryEntryItem(
            id = 205,
            title = "Weekend Trip",
            category = "Travel",
            date = "Oct 20, 2026",
            description = "Travel highlights and sights.",
            icon = "✈️",
            mood = "Excited",
            hasPhoto = true,
            photoUri = photos[0],
            photoUris = photos
        )
        DiaryActivityStore.addActivity(entry)
        DiaryActivityStore.selectActivity(entry)

        val viewModel = ActivityDetailsViewModel(scope = testScope)
        viewModel.setEntryDetails(
            title = entry.title,
            description = entry.description,
            date = entry.date,
            category = entry.category,
            mood = entry.mood,
            id = entry.id,
            photoUri = entry.photoUri,
            hasPhoto = entry.hasPhoto,
            photoUris = entry.photoUris
        )

        val state = viewModel.uiState.value
        assertTrue(state.hasPhoto)
        assertEquals(3, state.photoUris.size)
        assertEquals("3 photos attached", state.photoStatus)
    }

    @Test
    fun testEditActivityPreservesMultiplePhotos() {
        val photos = listOf(
            "/data/user/0/com.simats.echodiary/files/diary_photos/a.jpg",
            "/data/user/0/com.simats.echodiary/files/diary_photos/b.jpg"
        )
        val entry = DiaryEntryItem(
            id = 206,
            title = "Museum Tour",
            category = "Hobbies",
            date = "Oct 21, 2026",
            description = "Viewed historical exhibits.",
            icon = "🎨",
            mood = "Inspired",
            hasPhoto = true,
            photoUri = photos[0],
            photoUris = photos
        )
        DiaryActivityStore.addActivity(entry)
        DiaryActivityStore.selectActivity(entry)

        val viewModel = EditActivityViewModel(scope = testScope)
        viewModel.setInitialDetails(
            title = entry.title,
            description = entry.description,
            date = entry.date,
            category = entry.category,
            mood = entry.mood,
            id = entry.id,
            photoUri = entry.photoUri,
            hasPhoto = entry.hasPhoto,
            photoUris = entry.photoUris
        )

        viewModel.onTitleChanged("Museum Tour and Workshop")
        viewModel.onSaveClicked()

        val updated = DiaryActivityStore.activities.value.find { it.id == 206 }
        assertNotNull(updated)
        assertEquals(2, updated?.photoUris?.size)
        assertEquals("Museum Tour and Workshop", updated?.title)
        assertTrue(updated?.hasPhoto == true)
    }

    @Test
    fun testDiaryActivityStoreMergingPreservesLocalPhotos() {
        val photos = listOf("/data/user/0/com.simats.echodiary/files/diary_photos/beach1.jpg", "/data/user/0/com.simats.echodiary/files/diary_photos/beach2.jpg")
        val localEntryWithPhotos = DiaryEntryItem(
            id = 10,
            title = "Beach Sunset",
            category = "Travel",
            date = "Oct 22, 2026",
            description = "Watched sunset with friends.",
            icon = "✈️",
            mood = "Calm",
            hasPhoto = true,
            photoUri = photos[0],
            photoUris = photos
        )
        DiaryActivityStore.addActivity(localEntryWithPhotos)

        // Simulate backend sending refreshed list without local photo paths
        val backendEntries = listOf(
            DiaryEntryItem(
                id = 10,
                title = "Beach Sunset",
                category = "Travel",
                date = "Oct 22, 2026",
                description = "Watched sunset with friends.",
                icon = "✈️",
                mood = "Calm",
                hasPhoto = false,
                photoUri = null,
                photoUris = emptyList()
            )
        )
        DiaryActivityStore.setActivities(backendEntries)

        val merged = DiaryActivityStore.activities.value.find { it.id == 10 }
        assertNotNull(merged)
        assertTrue("Local photos must be preserved during backend merge", merged?.hasPhoto == true)
        assertEquals(photos[0], merged?.photoUri)
        assertEquals(2, merged?.photoUris?.size)
    }
}
