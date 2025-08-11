package com.pixelrabbit.animalshelterfirebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCM_DEBUG" // Используем тот же TAG для консистентности

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "MyFCMService: New token: $token")
        // Отправьте токен на сервер, если нужно, или обновите в Firestore
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "MyFCMService: Message received from: ${remoteMessage.from}")

        // Log the data payload, which is always available
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "MyFCMService: Message data payload: ${remoteMessage.data}")
            // Если вы хотите обрабатывать уведомления из data-payload,
            // вы можете сделать это здесь.
        }

        // --- ВАЖНО: Раскомментированный блок для обработки уведомлений в активном приложении ---
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "MyFCMService: Message Notification Title: ${notification.title}")
            Log.d(TAG, "MyFCMService: Message Notification Body: ${notification.body}")

            // Теперь вызываем метод для отображения уведомления
            sendNotification(notification.title, notification.body, remoteMessage.data)
        }
        // --------------------------------------------------------------------------------------
    }

    private fun sendNotification(title: String?, body: String?, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            data.forEach { (key, value) -> putExtra(key, value) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "animal_shelter_notifications"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Уведомления о приюте для животных",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID уведомления */, notificationBuilder.build())
    }
}
