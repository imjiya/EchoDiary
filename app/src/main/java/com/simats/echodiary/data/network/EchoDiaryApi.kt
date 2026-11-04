package com.simats.echodiary.data.network

import com.simats.echodiary.data.model.AuthResponse
import com.simats.echodiary.data.model.CreateEntryRequest
import com.simats.echodiary.data.model.CreateEntryResponse
import com.simats.echodiary.data.model.LoginRequest
import com.simats.echodiary.data.model.ProfileResponse
import com.simats.echodiary.data.model.RegisterRequest
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
}
