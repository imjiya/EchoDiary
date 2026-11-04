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
    suspend fun createManualEntry(
        title: String,
        description: String,
        date: String? = null,
        category: String? = "College",
        mood: String? = "Not specified"
    ): Result<CreateEntryResponse>
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

        return executeCreateEntry(request)
    }

    override suspend fun createManualEntry(
        title: String,
        description: String,
        date: String?,
        category: String?,
        mood: String?
    ): Result<CreateEntryResponse> {
        val cleanTitle = title.trim()
        val cleanDesc = description.trim()
        val derivedTitle = when {
            cleanTitle.isNotBlank() -> cleanTitle
            cleanDesc.length > 40 -> cleanDesc.substring(0, 40).trim() + "..."
            cleanDesc.isNotBlank() -> cleanDesc
            else -> "Written Diary Entry"
        }

        val formattedDate = when {
            !date.isNullOrBlank() -> {
                try {
                    if (date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                        date
                    } else {
                        val parsed = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).parse(date)
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsed ?: Date())
                    }
                } catch (_: Exception) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                }
            }
            else -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }

        val request = CreateEntryRequest(
            title = derivedTitle,
            description = cleanDesc.ifBlank { derivedTitle },
            transcript = null,
            entryDate = formattedDate,
            category = if (!category.isNullOrBlank()) category else "College",
            mood = if (!mood.isNullOrBlank()) mood else "Not specified",
            sourceType = "text"
        )

        return executeCreateEntry(request)
    }

    private suspend fun executeCreateEntry(request: CreateEntryRequest): Result<CreateEntryResponse> {
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
