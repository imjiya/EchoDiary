package com.simats.echodiary.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("user")
    val user: UserResponse? = null
)

data class UserResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("email")
    val email: String
)

data class ProfileResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("profile")
    val profile: UserProfile? = null
)

data class UserProfile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("profile_image")
    val profileImage: String? = null,
    @SerializedName("initials")
    val initials: String? = null,
    @SerializedName("diary_entries_count")
    val diaryEntriesCount: Int = 0,
    @SerializedName("saved_memories_count")
    val savedMemoriesCount: Int = 0
)

