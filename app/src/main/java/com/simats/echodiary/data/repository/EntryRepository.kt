package com.simats.echodiary.data.repository

import com.simats.echodiary.data.model.CreateEntryRequest
import com.simats.echodiary.data.model.CreateEntryResponse
import com.simats.echodiary.data.network.EchoDiaryApi
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface EntryRepository {
    suspend fun createVoiceEntry(transcript: String, title: String? = null): Result<CreateEntryResponse>
}

class EntryRepositoryImpl(
    private val api: EchoDiaryApi
) : EntryRepository {

    override suspend fun createVoiceEntry(transcript: String, title: String?): Result<CreateEntryResponse> {
        val cleanTranscript = transcript.trim()
        val derivedTitle = when {
            !title.isNullOrBlank() -> title.trim()
            cleanTranscript.length > 40 -> cleanTranscript.substring(0, 40).trim() + "..."
            cleanTranscript.isNotBlank() -> cleanTranscript
            else -> "Voice Diary Entry"
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val request = CreateEntryRequest(
            title = derivedTitle,
            description = cleanTranscript,
            transcript = cleanTranscript,
            entryDate = currentDate,
            category = "Voice Diary",
            mood = "Reflective",
            sourceType = "voice"
        )

        return try {
            val response = api.createEntry(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = try {
                    val rawError = response.errorBody()?.string()
                    if (!rawError.isNullOrBlank()) {
                        JSONObject(rawError).optString("message", "Failed to save entry.")
                    } else {
                        response.body()?.message ?: "Failed to save entry."
                    }
                } catch (_: Exception) {
                    "Failed to save entry."
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out while saving entry."))
        } catch (e: IOException) {
            Result.failure(Exception("Network error while saving entry."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "An unexpected error occurred."))
        }
    }
}
