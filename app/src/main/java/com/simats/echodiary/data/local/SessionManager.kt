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

    // ------------------------------------------------------------------------
    // Notifications Settings
    // ------------------------------------------------------------------------
    fun getDiaryRemindersEnabled(): Boolean = prefs.getBoolean(KEY_DIARY_REMINDERS, true)

    fun setDiaryRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DIARY_REMINDERS, enabled).apply()
    }

    fun getWeeklyRemindersEnabled(): Boolean = prefs.getBoolean(KEY_WEEKLY_REMINDERS, true)

    fun setWeeklyRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WEEKLY_REMINDERS, enabled).apply()
    }

    fun getSummaryUpdatesEnabled(): Boolean = prefs.getBoolean(KEY_SUMMARY_UPDATES, false)

    fun setSummaryUpdatesEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SUMMARY_UPDATES, enabled).apply()
    }

    fun getDailyReminderTime(): String = prefs.getString(KEY_DAILY_REMINDER_TIME, "8:00 PM") ?: "8:00 PM"

    fun setDailyReminderTime(time: String) {
        prefs.edit().putString(KEY_DAILY_REMINDER_TIME, time).apply()
    }

    fun getWeeklyReminderTime(): String = prefs.getString(KEY_WEEKLY_REMINDER_TIME, "Sunday · 8:00 PM") ?: "Sunday · 8:00 PM"

    fun setWeeklyReminderTime(time: String) {
        prefs.edit().putString(KEY_WEEKLY_REMINDER_TIME, time).apply()
    }

    fun saveNotificationPreferences(
        diaryReminders: Boolean,
        weeklyReminders: Boolean,
        summaryUpdates: Boolean,
        dailyTime: String,
        weeklyTime: String
    ) {
        prefs.edit().apply {
            putBoolean(KEY_DIARY_REMINDERS, diaryReminders)
            putBoolean(KEY_WEEKLY_REMINDERS, weeklyReminders)
            putBoolean(KEY_SUMMARY_UPDATES, summaryUpdates)
            putString(KEY_DAILY_REMINDER_TIME, dailyTime)
            putString(KEY_WEEKLY_REMINDER_TIME, weeklyTime)
            apply()
        }
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
        private const val KEY_DIARY_REMINDERS = "diary_reminders_enabled"
        private const val KEY_WEEKLY_REMINDERS = "weekly_reminders_enabled"
        private const val KEY_SUMMARY_UPDATES = "summary_updates_enabled"
        private const val KEY_DAILY_REMINDER_TIME = "daily_reminder_time"
        private const val KEY_WEEKLY_REMINDER_TIME = "weekly_reminder_time"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
