package com.simats.echodiary.data.network

import com.simats.echodiary.data.model.AuthResponse
import com.simats.echodiary.data.model.CreateEntryRequest
import com.simats.echodiary.data.model.CreateEntryResponse
import com.simats.echodiary.data.model.DeleteEntryRequest
import com.simats.echodiary.data.model.DeleteEntryResponse
import com.simats.echodiary.data.model.GenerateMemoryRequest
import com.simats.echodiary.data.model.GenerateMemoryResponse
import com.simats.echodiary.data.model.LoginRequest
import com.simats.echodiary.data.model.ProfileResponse
import com.simats.echodiary.data.model.RegisterRequest
import com.simats.echodiary.data.model.UpdateEntryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    @POST("entries/create.php")
    suspend fun createEntry(
        @Body request: CreateEntryRequest
    ): Response<CreateEntryResponse>

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
}
