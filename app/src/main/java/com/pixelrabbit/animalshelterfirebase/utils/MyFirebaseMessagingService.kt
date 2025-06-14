package com.pixelrabbit.animalshelterfirebase.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token: $token")
        // Отправьте токен на сервер, если нужно
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Обработка сообщения
        message.notification?.let {
            Log.d("FCM", "Notification title: ${it.title}")
            Log.d("FCM", "Notification body: ${it.body}")
        }
    }
}
