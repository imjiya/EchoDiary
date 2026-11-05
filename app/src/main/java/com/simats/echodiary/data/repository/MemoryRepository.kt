package com.simats.echodiary.data.repository

import com.simats.echodiary.data.model.GenerateMemoryRequest
import com.simats.echodiary.data.model.GenerateMemoryResponse
import com.simats.echodiary.data.network.EchoDiaryApi
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

interface MemoryRepository {
    suspend fun generateMemory(
        type: String,
        save: Boolean = true,
        startDate: String? = null,
        endDate: String? = null
    ): Result<GenerateMemoryResponse>
}

class MemoryRepositoryImpl(
    private val api: EchoDiaryApi
) : MemoryRepository {

    override suspend fun generateMemory(
        type: String,
        save: Boolean,
        startDate: String?,
        endDate: String?
    ): Result<GenerateMemoryResponse> {
        val request = GenerateMemoryRequest(
            type = type,
            save = save,
            startDate = startDate,
            endDate = endDate
        )

        return try {
            val response = api.generateMemory(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = try {
                    val rawError = response.errorBody()?.string()
                    if (!rawError.isNullOrBlank()) {
                        JSONObject(rawError).optString("message", "Failed to generate $type memory.")
                    } else {
                        response.body()?.message ?: "Failed to generate $type memory."
                    }
                } catch (_: Exception) {
                    "Failed to generate $type memory."
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out while generating $type memory."))
        } catch (e: IOException) {
            Result.failure(Exception("Network error while generating $type memory."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "An unexpected error occurred."))
        }
    }
}

