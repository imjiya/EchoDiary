package com.simats.echodiary.data.repository

import com.google.gson.Gson
import com.simats.echodiary.data.local.SessionManager
import com.simats.echodiary.data.model.AuthResponse
import com.simats.echodiary.data.model.LoginRequest
import com.simats.echodiary.data.model.ProfileResponse
import com.simats.echodiary.data.model.RegisterRequest
import com.simats.echodiary.data.network.EchoDiaryApi
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResponse>
    suspend fun register(fullName: String, email: String, password: String): Result<AuthResponse>
    suspend fun getProfile(): Result<ProfileResponse>
}

class AuthRepositoryImpl(
    private val api: EchoDiaryApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    private val gson = Gson()

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email.trim(), password))
            handleAuthResponse(response)
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out. Please verify the backend server is running."))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Unable to connect to server."))
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred. Please try again."))
        }
    }

    override suspend fun register(fullName: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.signup(RegisterRequest(fullName.trim(), email.trim(), password))
            handleAuthResponse(response)
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out. Please verify the backend server is running."))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Unable to connect to server."))
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred. Please try again."))
        }
    }

    override suspend fun getProfile(): Result<ProfileResponse> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    ?: response.body()?.message
                    ?: "Failed to retrieve profile."
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error while loading profile."))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to load profile."))
        }
    }

    private fun handleAuthResponse(response: Response<AuthResponse>): Result<AuthResponse> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.success) {
                // Save session if token is provided
                val token = body.token
                if (!token.isNullOrBlank()) {
                    sessionManager.saveAuth(token, body.user)
                }
                Result.success(body)
            } else {
                Result.failure(Exception(body?.message ?: "Authentication failed."))
            }
        } else {
            val rawError = response.errorBody()?.string()
            val parsedMessage = parseErrorMessage(rawError)
            val finalMessage = when {
                !parsedMessage.isNullOrBlank() -> parsedMessage
                response.code() == 401 -> "Invalid email or password."
                response.code() == 409 -> "An account with this email already exists."
                response.code() >= 500 -> "Server error occurred. Please try again later."
                else -> "Request failed (Error ${response.code()})."
            }
            Result.failure(Exception(finalMessage))
        }
    }

    private fun parseErrorMessage(errorBodyString: String?): String? {
        if (errorBodyString.isNullOrBlank()) return null
        return try {
            val parsed = gson.fromJson(errorBodyString, AuthResponse::class.java)
            parsed?.message
        } catch (e: Exception) {
            null
        }
    }
}

