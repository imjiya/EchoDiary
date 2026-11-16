package com.simats.echodiary.data.network

import com.simats.echodiary.data.model.AuthResponse
import com.simats.echodiary.data.model.CreateEntryRequest
import com.simats.echodiary.data.model.CreateEntryResponse
import com.simats.echodiary.data.model.DeleteEntryRequest
import com.simats.echodiary.data.model.DeleteEntryResponse
import com.simats.echodiary.data.model.EntryListResponse
import com.simats.echodiary.data.model.GenerateMemoryRequest
import com.simats.echodiary.data.model.GenerateMemoryResponse
import com.simats.echodiary.data.model.LoginRequest
import com.simats.echodiary.data.model.MemoryListResponse
import com.simats.echodiary.data.model.ProfileResponse
import com.simats.echodiary.data.model.RegisterRequest
import com.simats.echodiary.data.model.RegisterTokenRequest
import com.simats.echodiary.data.model.SendReminderRequest
import com.simats.echodiary.data.model.StandardResponse
import com.simats.echodiary.data.model.UpdateEntryRequest
import com.simats.echodiary.data.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EchoDiaryApi {

    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/signup.php")
    suspend fun signup(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @GET("auth/profiles.php")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("auth/profiles.php")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>

    @POST("entries/create.php")
    suspend fun createEntry(
        @Body request: CreateEntryRequest
    ): Response<CreateEntryResponse>

    @GET("entries/list.php")
    suspend fun listEntries(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<EntryListResponse>

    @POST("entries/update.php")
    suspend fun updateEntry(
        @Body request: UpdateEntryRequest
    ): Response<CreateEntryResponse>

    @POST("entries/delete.php")
    suspend fun deleteEntry(
        @Body request: DeleteEntryRequest
    ): Response<DeleteEntryResponse>

    @POST("memories/generate.php")
    suspend fun generateMemory(
        @Body request: GenerateMemoryRequest
    ): Response<GenerateMemoryResponse>

    @GET("memories/list.php")
    suspend fun listMemories(
        @Query("type") type: String? = null
    ): Response<MemoryListResponse>

    @POST("notifications/register_token.php")
    suspend fun registerDeviceToken(
        @Body request: RegisterTokenRequest
    ): Response<StandardResponse>

    @POST("notifications/send_reminder.php")
    suspend fun requestReminderNotification(
        @Body request: SendReminderRequest
    ): Response<StandardResponse>
}
