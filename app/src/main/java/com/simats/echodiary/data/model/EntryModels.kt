package com.simats.echodiary.data.model

import com.google.gson.annotations.SerializedName

data class CreateEntryRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("transcript")
    val transcript: String? = null,
    @SerializedName("entry_date")
    val entryDate: String? = null,
    @SerializedName("category")
    val category: String = "Voice Diary",
    @SerializedName("mood")
    val mood: String = "Reflective",
    @SerializedName("source_type")
    val sourceType: String = "voice"
)

data class UpdateEntryRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("entry_date")
    val entryDate: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("mood")
    val mood: String? = null
)

data class DeleteEntryRequest(
    @SerializedName("id")
    val id: Int
)

data class DeleteEntryResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("deleted_id")
    val deletedId: Int? = null
)

data class CreateEntryResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("entry")
    val entry: EntryItem? = null
)

data class EntryItem(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("transcript")
    val transcript: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("mood")
    val mood: String? = null,
    @SerializedName("entry_date")
    val entryDate: String? = null,
    @SerializedName("source_type")
    val sourceType: String? = null
)

data class EntryListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("total")
    val total: Int? = 0,
    @SerializedName("count")
    val count: Int? = 0,
    @SerializedName("entries")
    val entries: List<EntryItemResponse>? = null,
    @SerializedName("message")
    val message: String? = null
)

data class EntryItemResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("transcript")
    val transcript: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("category_icon")
    val categoryIcon: String? = null,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("day_of_week")
    val dayOfWeek: String? = null,
    @SerializedName("full_date")
    val fullDate: String? = null,
    @SerializedName("entry_date")
    val entryDate: String? = null,
    @SerializedName("mood")
    val mood: String? = null,
    @SerializedName("has_photo")
    val hasPhoto: Boolean? = false,
    @SerializedName("photo_url")
    val photoUrl: String? = null,
    @SerializedName("audio_url")
    val audioUrl: String? = null,
    @SerializedName("source_type")
    val sourceType: String? = null
)
