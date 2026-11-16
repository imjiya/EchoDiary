package com.simats.echodiary.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.model.RegisterTokenRequest
import com.simats.echodiary.data.network.RetrofitClient
import com.simats.echodiary.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EchoFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Firebase FCM Token received: $token")
        DiaryActivityStore.setFcmToken(token)

        // Try registering device token with backend
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.getApi(applicationContext)
                api.registerDeviceToken(RegisterTokenRequest(fcmToken = token))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token with backend", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Firebase push message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val title = notification?.title
            ?: data["title"]
            ?: "EchoDiary · Time to add memory for this day ✨"

        val message = notification?.body
            ?: data["message"]
            ?: data["body"]
            ?: "How was your day? Take a moment to capture your thoughts, highlights, or feelings."

        NotificationHelper.showInteractiveAddMemoryNotification(
            context = applicationContext,
            title = title,
            message = message
        )
    }

    companion object {
        private const val TAG = "EchoFirebaseMessaging"
    }
}

