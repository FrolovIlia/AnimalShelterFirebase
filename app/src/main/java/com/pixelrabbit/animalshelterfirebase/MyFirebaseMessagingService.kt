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

    private val TAG = "FCM_DEBUG"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "MyFCMService: New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "MyFCMService: Message received from: ${remoteMessage.from}")

        // Проверяем, есть ли уведомление
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "MyFCMService: Message Notification Title: ${notification.title}")
            Log.d(TAG, "MyFCMService: Message Notification Body: ${notification.body}")

            // Получаем данные из payload
            val data = remoteMessage.data

            // Определяем уникальный ключ уведомления в зависимости от топика
            val uniqueKey = when (remoteMessage.from) {
                "/topics/new_animals" -> {
                    // Это уведомление о животном, получаем animalKey
                    data["animalKey"]
                }
                "/topics/new_tasks" -> {
                    // Это уведомление о задаче, получаем taskKey
                    data["taskKey"]
                }
                else -> {
                    // Неизвестный топик
                    null
                }
            }

            // Отправляем уведомление с правильным ключом
            sendNotification(
                title = notification.title,
                body = notification.body,
                data = data,
                uniqueId = uniqueKey
            )
        }
    }

    private fun sendNotification(title: String?, body: String?, data: Map<String, String>, uniqueId: String?) {
        // Создаем Intent для запуска MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            // Помещаем все данные уведомления в Intent
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        // Создаем PendingIntent, используя уникальный ID
        val pendingIntent = PendingIntent.getActivity(
            this,
            uniqueId?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "animal_shelter_notifications"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo)
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
        notificationManager.notify(uniqueId?.hashCode() ?: 0, notificationBuilder.build())
    }
}
