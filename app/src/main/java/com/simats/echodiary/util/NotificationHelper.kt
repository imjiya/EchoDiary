package com.simats.echodiary.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.simats.echodiary.MainActivity
import com.simats.echodiary.R
import com.simats.echodiary.receiver.NotificationActionReceiver

object NotificationHelper {

    const val CHANNEL_ID = "echodiary_reminders_channel"
    const val CHANNEL_NAME = "EchoDiary Reminders & Memories"
    const val CHANNEL_DESCRIPTION = "Daily reminders to add memories and interactive prompts"

    const val NOTIFICATION_ID_DAILY_REMINDER = 1001
    const val NOTIFICATION_ID_CONFIRMATION = 1002

    const val EXTRA_NAVIGATE_TO = "extra_navigate_to"
    const val DESTINATION_WRITE_ENTRY = "write_entry"
    const val DESTINATION_VOICE_RECORD = "voice_recording"
    const val DESTINATION_PHOTO_MEMORIES = "photo_memories"
    const val DESTINATION_HOME = "home"

    const val ACTION_QUICK_REPLY = "com.simats.echodiary.ACTION_QUICK_REPLY"
    const val KEY_QUICK_MEMORY_TEXT = "key_quick_memory_text"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds and displays an interactive notification asking the user to add today's memory.
     * Features:
     * 1. Direct Tap opens Write Diary screen
     * 2. Action Button: "✍️ Write Diary"
     * 3. Action Button: "🎙️ Voice Record"
     * 4. Interactive Direct Reply: "💭 Quick Thought" to type a note directly from notification shade
     */
    fun showInteractiveAddMemoryNotification(
        context: Context,
        title: String = "EchoDiary · Time to add memory for this day ✨",
        message: String = "How was your day? Take a moment to capture your thoughts, highlights, or feelings."
    ): Boolean {
        createNotificationChannel(context)

        // Main Tap Intent -> Opens Write Diary Entry
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, DESTINATION_WRITE_ENTRY)
        }
        val tapPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            101,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: Write Diary Entry
        val writeIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, DESTINATION_WRITE_ENTRY)
        }
        val writePendingIntent = PendingIntent.getActivity(
            context,
            102,
            writeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: Voice Recording
        val voiceIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, DESTINATION_VOICE_RECORD)
        }
        val voicePendingIntent = PendingIntent.getActivity(
            context,
            103,
            voiceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 3: Interactive Quick Thought (RemoteInput)
        val remoteInput = RemoteInput.Builder(KEY_QUICK_MEMORY_TEXT)
            .setLabel("Type a quick reflection...")
            .build()

        val replyIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_QUICK_REPLY
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            104,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
        )

        val quickReplyAction = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            "💭 Quick Thought",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\nChoose an action below to preserve today's memory:")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .addAction(R.mipmap.ic_launcher, "✍️ Write Diary", writePendingIntent)
            .addAction(R.mipmap.ic_launcher, "🎙️ Voice Record", voicePendingIntent)
            .addAction(quickReplyAction)

        return dispatchNotification(context, NOTIFICATION_ID_DAILY_REMINDER, builder)
    }

    /**
     * Backward compatible wrapper for reminder notification.
     */
    fun showReminderNotification(
        context: Context,
        title: String = "EchoDiary · Time to add memory for this day ✨",
        message: String = "Take a moment to record your thoughts and memories for today."
    ): Boolean {
        return showInteractiveAddMemoryNotification(context, title, message)
    }

    /**
     * Confirmation notification shown after direct quick reply from notification shade.
     */
    fun showMemorySavedConfirmation(
        context: Context,
        savedText: String
    ): Boolean {
        createNotificationChannel(context)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, DESTINATION_HOME)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            105,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("EchoDiary · Memory Captured! ✨")
            .setContentText("Saved to today's memory: \"$savedText\"")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Saved to today's memory:\n\"$savedText\""))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)

        return dispatchNotification(context, NOTIFICATION_ID_CONFIRMATION, builder)
    }

    private fun dispatchNotification(
        context: Context,
        notificationId: Int,
        builder: NotificationCompat.Builder
    ): Boolean {
        return try {
            val manager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    manager.notify(notificationId, builder.build())
                    true
                } else {
                    false
                }
            } else {
                manager.notify(notificationId, builder.build())
                true
            }
        } catch (_: SecurityException) {
            false
        } catch (_: Exception) {
            false
        }
    }
}
