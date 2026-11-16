package com.simats.echodiary.data.model

import com.google.gson.annotations.SerializedName

data class RegisterTokenRequest(
    @SerializedName("fcm_token")
    val fcmToken: String,
    @SerializedName("device_type")
    val deviceType: String = "android"
)

data class SendReminderRequest(
    @SerializedName("type")
    val type: String = "daily_memory_reminder",
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("message")
    val message: String? = null
)

data class StandardResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null
)

