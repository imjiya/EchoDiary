package com.simats.echodiary

import com.google.gson.Gson
import com.simats.echodiary.data.model.AuthResponse
import com.simats.echodiary.data.model.LoginRequest
import com.simats.echodiary.data.model.ProfileResponse
import com.simats.echodiary.data.model.RegisterRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUnitTest {

    private val gson = Gson()

    @Test
    fun testLoginRequestSerialization() {
        val request = LoginRequest("alex.morgan@example.com", "password123")
        val json = gson.toJson(request)
        assertTrue(json.contains("\"email\":\"alex.morgan@example.com\""))
        assertTrue(json.contains("\"password\":\"password123\""))
    }

    @Test
    fun testRegisterRequestSerialization() {
        val request = RegisterRequest("Alex Morgan", "alex.morgan@example.com", "password123")
        val json = gson.toJson(request)
        assertTrue(json.contains("\"full_name\":\"Alex Morgan\""))
        assertTrue(json.contains("\"email\":\"alex.morgan@example.com\""))
        assertTrue(json.contains("\"password\":\"password123\""))
    }

    @Test
    fun testAuthResponseDeserialization() {
        val json = """
            {
                "success": true,
                "message": "Login successful.",
                "token": "4ea298a2c4815864ee45e88b15f674be611927e5a3cab363bc4a0477279db29b",
                "user": {
                    "id": 3,
                    "full_name": "Alex Morgan",
                    "email": "alex.morgan@example.com"
                }
            }
        """.trimIndent()

        val response = gson.fromJson(json, AuthResponse::class.java)
        assertTrue(response.success)
        assertEquals("Login successful.", response.message)
        assertNotNull(response.token)
        assertNotNull(response.user)
        assertEquals(3, response.user?.id)
        assertEquals("Alex Morgan", response.user?.fullName)
        assertEquals("alex.morgan@example.com", response.user?.email)
    }

    @Test
    fun testProfileResponseDeserialization() {
        val json = """
            {
                "success": true,
                "message": "Profile retrieved successfully.",
                "profile": {
                    "id": 3,
                    "full_name": "Alex Morgan",
                    "email": "alex.morgan@example.com",
                    "profile_image": null,
                    "initials": "AM",
                    "diary_entries_count": 5,
                    "saved_memories_count": 1
                }
            }
        """.trimIndent()

        val response = gson.fromJson(json, ProfileResponse::class.java)
        assertTrue(response.success)
        assertNotNull(response.profile)
        assertEquals("AM", response.profile?.initials)
        assertEquals(5, response.profile?.diaryEntriesCount)
        assertEquals(1, response.profile?.savedMemoriesCount)
    }
}

