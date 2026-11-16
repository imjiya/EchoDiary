package com.simats.echodiary

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.model.CreateEntryRequest
import com.simats.echodiary.data.model.CreateEntryResponse
import com.simats.echodiary.data.model.DeleteEntryRequest
import com.simats.echodiary.data.model.DeleteEntryResponse
import com.simats.echodiary.data.model.EntryItemResponse
import com.simats.echodiary.data.model.EntryListResponse
import com.simats.echodiary.data.model.GenerateMemoryRequest
import com.simats.echodiary.data.model.GenerateMemoryResponse
import com.simats.echodiary.data.model.LoginRequest
import com.simats.echodiary.data.model.MemoryItemResponse
import com.simats.echodiary.data.model.MemoryListResponse
import com.simats.echodiary.data.model.ProfileResponse
import com.simats.echodiary.data.model.RegisterRequest
import com.simats.echodiary.data.model.RegisterTokenRequest
import com.simats.echodiary.data.model.SendReminderRequest
import com.simats.echodiary.data.model.StandardResponse
import com.simats.echodiary.data.model.UpdateEntryRequest
import com.simats.echodiary.data.model.UpdateProfileRequest
import com.simats.echodiary.data.network.EchoDiaryApi
import com.simats.echodiary.data.repository.EntryRepositoryImpl
import com.simats.echodiary.data.repository.MemoryRepositoryImpl
import com.simats.echodiary.ui.activity.AllActivitiesViewModel
import com.simats.echodiary.ui.activity.DiaryEntryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RepositoryCachingUnitTest {

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

    class FakeEchoDiaryApi : EchoDiaryApi {
        var listEntriesCallCount = 0
        var listMemoriesCallCount = 0

        override suspend fun listEntries(limit: Int, offset: Int): Response<EntryListResponse> {
            listEntriesCallCount++
            val items = listOf(
                EntryItemResponse(
                    id = 101,
                    title = "Backend Grounded Entry",
                    description = "Fetched from EchoDiary backend.",
                    transcript = null,
                    category = "Work",
                    categoryIcon = "💼",
                    icon = "💼",
                    date = "Oct 15, 2026",
                    dayOfWeek = "THURSDAY",
                    fullDate = "October 15, 2026",
                    entryDate = "2026-10-15",
                    mood = "Focused",
                    hasPhoto = false,
                    photoUrl = null,
                    audioUrl = null,
                    sourceType = "text"
                )
            )
            return Response.success(EntryListResponse(success = true, count = 1, entries = items))
        }

        override suspend fun listMemories(type: String?): Response<MemoryListResponse> {
            listMemoriesCallCount++
            val memories = listOf(
                MemoryItemResponse(
                    id = 1,
                    type = type ?: "weekly",
                    title = "Cached Weekly Memory",
                    narrative = "Narrative from memory repository.",
                    summaryText = "Summary from memory repository."
                )
            )
            return Response.success(MemoryListResponse(success = true, count = 1, memories = memories))
        }

        override suspend fun login(request: LoginRequest) = throw NotImplementedError()
        override suspend fun signup(request: RegisterRequest) = throw NotImplementedError()
        override suspend fun getProfile(): Response<ProfileResponse> = throw NotImplementedError()
        override suspend fun updateProfile(request: UpdateProfileRequest) = throw NotImplementedError()
        override suspend fun createEntry(request: CreateEntryRequest) = throw NotImplementedError()
        override suspend fun updateEntry(request: UpdateEntryRequest) = throw NotImplementedError()
        override suspend fun deleteEntry(request: DeleteEntryRequest) = throw NotImplementedError()
        override suspend fun generateMemory(request: GenerateMemoryRequest): Response<GenerateMemoryResponse> {
            return Response.success(
                GenerateMemoryResponse(
                    success = true,
                    title = "Synthesized Memory",
                    narrative = "Synthesized Narrative"
                )
            )
        }
        override suspend fun registerDeviceToken(request: RegisterTokenRequest) = throw NotImplementedError()
        override suspend fun requestReminderNotification(request: SendReminderRequest) = throw NotImplementedError()
    }

    @Test
    fun testEntryRepositoryCachesAndDoesNotHitApiOnEveryVisit() = runBlocking {
        val fakeApi = FakeEchoDiaryApi()
        val repository = EntryRepositoryImpl(fakeApi)

        // First call: not cached, should call API once
        val result1 = repository.getEntries(forceRefresh = false)
        assertTrue(result1.isSuccess)
        assertEquals(1, fakeApi.listEntriesCallCount)
        assertEquals("Backend Grounded Entry", result1.getOrNull()?.first()?.title)

        // Second call (simulating revisiting screen): should use cache, NOT hit API again
        val result2 = repository.getEntries(forceRefresh = false)
        assertTrue(result2.isSuccess)
        assertEquals(1, fakeApi.listEntriesCallCount) // Call count remains 1!

        // Third call with forceRefresh = true: should hit API again
        val result3 = repository.getEntries(forceRefresh = true)
        assertTrue(result3.isSuccess)
        assertEquals(2, fakeApi.listEntriesCallCount)
    }

    @Test
    fun testMemoryRepositoryCachesMemories() = runBlocking {
        val fakeApi = FakeEchoDiaryApi()
        val memoryRepo = MemoryRepositoryImpl(fakeApi)

        // First call
        val result1 = memoryRepo.getMemories(type = "weekly", forceRefresh = false)
        assertTrue(result1.isSuccess)
        assertEquals(1, fakeApi.listMemoriesCallCount)

        // Second call: should use cache, call count remains 1
        val result2 = memoryRepo.getMemories(type = "weekly", forceRefresh = false)
        assertTrue(result2.isSuccess)
        assertEquals(1, fakeApi.listMemoriesCallCount)
    }

    @Test
    fun testAllActivitiesViewModelUsesCachedStateWithoutNetworkSpam() {
        val fakeApi = FakeEchoDiaryApi()
        val repository = EntryRepositoryImpl(fakeApi)

        // Preload cache
        runBlocking {
            repository.getEntries(forceRefresh = false)
        }
        assertEquals(1, fakeApi.listEntriesCallCount)

        // Instantiate ViewModel
        val viewModel = AllActivitiesViewModel(entryRepository = repository, scope = testScope)
        val state = viewModel.uiState.value
        assertNotNull(state)
        assertTrue(state.entries.isNotEmpty())

        // Calling loadActivities without forceRefresh does NOT hit API
        viewModel.loadActivities(forceRefresh = false)
        assertEquals(1, fakeApi.listEntriesCallCount)
    }
}
