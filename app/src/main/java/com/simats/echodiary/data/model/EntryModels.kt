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

