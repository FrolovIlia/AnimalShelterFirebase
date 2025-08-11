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
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreen
import com.pixelrabbit.animalshelterfirebase.ui.details_animal_screen.AnimalDetailsScreen
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AnimalDetailsNavObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCM_DEBUG"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "MyFCMService: New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "MyFCMService: Message received from: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "MyFCMService: Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "MyFCMService: Message Notification Title: ${notification.title}")
            Log.d(TAG, "MyFCMService: Message Notification Body: ${notification.body}")

            // Получаем ключ животного из данных payload
            val animalKey = remoteMessage.data["animalKey"]

            sendNotification(notification.title, notification.body, remoteMessage.data)
        }
    }

    private fun sendNotification(title: String?, body: String?, data: Map<String, String>) {
        // Создаем Intent для запуска MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            // Помещаем все данные уведомления в Intent
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        // Создаем PendingIntent, который будет запущен при нажатии на уведомление
        // Используем animalKey для уникального request code
        val pendingIntent = PendingIntent.getActivity(
            this,
            data["animalKey"]?.hashCode() ?: 0,
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

        // Используем уникальный ID для каждого уведомления, чтобы они не заменяли друг друга
        notificationManager.notify(data["animalKey"]?.hashCode() ?: 0, notificationBuilder.build())
    }
}
