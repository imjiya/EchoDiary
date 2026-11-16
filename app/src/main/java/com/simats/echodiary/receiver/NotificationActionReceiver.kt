package com.simats.echodiary.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.DiaryEntryItem
import com.simats.echodiary.util.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NotificationHelper.ACTION_QUICK_REPLY) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            val quickText = remoteInput?.getCharSequence(NotificationHelper.KEY_QUICK_MEMORY_TEXT)?.toString()?.trim()

            if (!quickText.isNullOrBlank()) {
                val todayDate = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(Date())
                val newId = (System.currentTimeMillis() % 100000).toInt()

                val newEntry = DiaryEntryItem(
                    id = newId,
                    title = if (quickText.length > 30) quickText.take(30).trim() + "..." else quickText,
                    category = "Other",
                    date = todayDate,
                    description = quickText,
                    icon = "💭",
                    mood = "Reflective"
                )

                // Add to memory of activities
                DiaryActivityStore.addActivity(newEntry)

                // Show confirmation notification
                NotificationHelper.showMemorySavedConfirmation(context, quickText)
            }
        }
    }
}

