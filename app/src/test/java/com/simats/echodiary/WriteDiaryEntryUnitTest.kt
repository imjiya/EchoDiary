package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.WriteDiaryEntryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WriteDiaryEntryUnitTest {

    private var testScope = CoroutineScope(Dispatchers.Default)
    private lateinit var viewModel: WriteDiaryEntryViewModel

    @Before
    fun setup() {
        testScope = CoroutineScope(Dispatchers.Default)
        DiaryActivityStore.resetToDefaults()
        viewModel = WriteDiaryEntryViewModel(scope = testScope)
    }

    @After
    fun tearDown() {
        testScope.cancel()
        DiaryActivityStore.resetToDefaults()
    }

    @Test
    fun testInitialPhotoState() {
        val state = viewModel.uiState.value
        assertFalse(state.hasPhoto)
        assertNull(state.photoUri)
    }

    @Test
    fun testOnPhotoSelectedUpdatesState() {
        val testUri = "content://media/external/images/media/12345"
        viewModel.onPhotoSelected(testUri)

        val state = viewModel.uiState.value
        assertTrue("hasPhoto should be true after photo selection", state.hasPhoto)
        assertEquals(testUri, state.photoUri)
        assertEquals("Photo imported from gallery ✓", state.statusMessage)
        assertNull(state.errorMessage)
    }

    @Test
    fun testOnRemovePhotoClearsPhotoState() {
        viewModel.onPhotoSelected("content://media/external/images/media/999")
        assertTrue(viewModel.uiState.value.hasPhoto)

        viewModel.onRemovePhoto()
        val state = viewModel.uiState.value
        assertFalse("hasPhoto should be false after removal", state.hasPhoto)
        assertNull("photoUri should be null after removal", state.photoUri)
    }

    @Test
    fun testOnPermissionDeniedShowsErrorMessage() {
        val deniedMsg = "Gallery permission is required to import photos."
        viewModel.onPermissionDenied(deniedMsg)

        val state = viewModel.uiState.value
        assertEquals(deniedMsg, state.errorMessage)
    }

    @Test
    fun testSaveEntryWithAttachedPhoto() {
        val photoUri = "content://media/external/images/media/456"
        viewModel.onTitleChanged("Park visit with photo")
        viewModel.onDescriptionChanged("Enjoyed walking around the lake.")
        viewModel.onPhotoSelected(photoUri)

        viewModel.onSaveEntryClicked()
        Thread.sleep(150)

        val savedActivity = DiaryActivityStore.activities.value.find { it.title == "Park visit with photo" }
        assertNotNull("Saved activity should exist in DiaryActivityStore", savedActivity)
        assertTrue("Saved activity should have hasPhoto true", savedActivity?.hasPhoto == true)
        assertEquals(photoUri, savedActivity?.photoUri)
    }
}

