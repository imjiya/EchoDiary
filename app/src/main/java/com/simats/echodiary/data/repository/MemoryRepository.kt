package com.simats.echodiary.data.repository

import com.simats.echodiary.ai.RagMemoryEngine
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.model.GenerateMemoryRequest
import com.simats.echodiary.data.model.GenerateMemoryResponse
import com.simats.echodiary.data.model.MemoryItemResponse
import com.simats.echodiary.data.model.SummaryEntryItem
import com.simats.echodiary.data.network.EchoDiaryApi
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

interface MemoryRepository {
    suspend fun getMemories(type: String? = null, forceRefresh: Boolean = false): Result<List<MemoryItemResponse>>
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

    private val cachedMemories = mutableMapOf<String, List<MemoryItemResponse>>()

    override suspend fun getMemories(type: String?, forceRefresh: Boolean): Result<List<MemoryItemResponse>> {
        val cacheKey = type ?: "all"
        if (!forceRefresh && cachedMemories.containsKey(cacheKey)) {
            return Result.success(cachedMemories[cacheKey] ?: emptyList())
        }

        return try {
            val response = api.listMemories(type)
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()?.memories ?: emptyList()
                cachedMemories[cacheKey] = list
                Result.success(list)
            } else {
                Result.success(cachedMemories[cacheKey] ?: emptyList())
            }
        } catch (_: Exception) {
            Result.success(cachedMemories[cacheKey] ?: emptyList())
        }
    }

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
                val body = response.body()!!
                // Invalidate cache for this memory type to reflect newly generated memory
                cachedMemories.remove(type)
                cachedMemories.remove("all")
                Result.success(body)
            } else {
                // RAG offline fallback: synthesize memory locally from stored entries
                val fallbackResult = synthesizeOfflineRagMemory(type)
                Result.success(fallbackResult)
            }
        } catch (_: Exception) {
            // RAG offline fallback
            val fallbackResult = synthesizeOfflineRagMemory(type)
            Result.success(fallbackResult)
        }
    }

    private fun synthesizeOfflineRagMemory(type: String): GenerateMemoryResponse {
        val entries = DiaryActivityStore.activities.value
        val dateRange = when (type.lowercase()) {
            "monthly" -> "OCTOBER 2026"
            "yearly" -> "2026"
            else -> "OCT 5–11, 2026"
        }
        val ragResult = RagMemoryEngine.synthesizeMemory(entries, type, dateRange)

        val summaryEntries = entries.take(5).map {
            SummaryEntryItem(
                id = it.id,
                title = it.title,
                categoryDetails = "${it.category} · ${it.date}",
                icon = it.icon
            )
        }

        return GenerateMemoryResponse(
            success = true,
            saved = false,
            memoryId = null,
            dateRange = dateRange,
            startDate = null,
            endDate = null,
            diaryEntriesCount = entries.size,
            categoriesCount = entries.map { it.category }.distinct().size,
            title = ragResult.title,
            summaryText = ragResult.narrative,
            narrative = ragResult.narrative,
            highlights = ragResult.highlights,
            moodSummary = ragResult.moodSummary,
            mindfulTakeaway = ragResult.mindfulTakeaway,
            entries = summaryEntries,
            message = "Generated via on-device SLM RAG engine."
        )
    }
}
