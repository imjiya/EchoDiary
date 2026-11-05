package com.simats.echodiary.data.model

import com.google.gson.annotations.SerializedName

data class GenerateMemoryRequest(
    @SerializedName("type")
    val type: String, // "weekly", "monthly", "yearly"
    @SerializedName("save")
    val save: Boolean = true,
    @SerializedName("start_date")
    val startDate: String? = null,
    @SerializedName("end_date")
    val endDate: String? = null
)

data class SummaryEntryItem(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("category_details")
    val categoryDetails: String? = null,
    @SerializedName("icon")
    val icon: String? = null
)

data class GenerateMemoryResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("saved")
    val saved: Boolean? = false,
    @SerializedName("memory_id")
    val memoryId: Int? = null,
    @SerializedName("date_range")
    val dateRange: String? = null,
    @SerializedName("start_date")
    val startDate: String? = null,
    @SerializedName("end_date")
    val endDate: String? = null,
    @SerializedName("diary_entries_count")
    val diaryEntriesCount: Int? = null,
    @SerializedName("categories_count")
    val categoriesCount: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("summary_text")
    val summaryText: String? = null,
    @SerializedName("narrative")
    val narrative: String? = null,
    @SerializedName("highlights")
    val highlights: List<String>? = null,
    @SerializedName("mood_summary")
    val moodSummary: String? = null,
    @SerializedName("entries")
    val entries: List<SummaryEntryItem>? = null,
    @SerializedName("message")
    val message: String? = null
)

