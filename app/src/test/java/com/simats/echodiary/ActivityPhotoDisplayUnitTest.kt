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
}
