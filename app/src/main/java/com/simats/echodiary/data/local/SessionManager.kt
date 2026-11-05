package com.simats.echodiary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.simats.echodiary.data.model.UserResponse

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Log.w(TAG, "EncryptedSharedPreferences initialization failed, falling back to standard prefs", e)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuth(token: String, user: UserResponse?) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            user?.let {
                putInt(KEY_USER_ID, it.id)
                putString(KEY_USER_NAME, it.fullName)
                putString(KEY_USER_EMAIL, it.email)
            }
            apply()
        }
    }

    fun updateUserDetails(fullName: String, email: String) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, fullName)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "Light") ?: "Light"

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    fun getThemePalette(): String = prefs.getString(KEY_THEME_PALETTE, "Calm Blue") ?: "Calm Blue"

    fun setThemePalette(palette: String) {
        prefs.edit().putString(KEY_THEME_PALETTE, palette).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val TAG = "SessionManager"
        private const val PREFS_NAME = "echo_diary_secure_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_THEME_PALETTE = "theme_palette"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
