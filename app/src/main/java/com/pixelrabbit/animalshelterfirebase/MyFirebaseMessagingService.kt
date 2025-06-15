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
import com.pixelrabbit.animalshelterfirebase.MainActivity // Убедитесь, что это ваш MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCM_DEBUG" // Используем тот же TAG для консистентности

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "MyFCMService: New token: $token")
        // Отправьте токен на сервер, если нужно, или обновите в Firestore
        // Например:
        // val sharedPrefs = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        // sharedPrefs.edit().putString("fcm_token", token).apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "MyFCMService: Message received from: ${remoteMessage.from}")

        // Log the data payload, which is always available
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "MyFCMService: Message data payload: ${remoteMessage.data}")
            // Если вы ожидаете, что Cloud Function отправляет данные в "data" payload
            // и не всегда в "notification", то здесь можно извлечь заголовок и текст из data
            // val title = remoteMessage.data["title"] ?: "Новое уведомление"
            // val body = remoteMessage.data["body"] ?: "Посмотрите в приложении!"
            // sendNotification(title, body, remoteMessage.data)
        }

        // Log and handle the notification payload (if present)
//        remoteMessage.notification?.let { notification ->
//            Log.d(TAG, "MyFCMService: Message Notification Title: ${notification.title}")
//            Log.d(TAG, "MyFCMService: Message Notification Body: ${notification.body}")
//
//            // Теперь вызываем метод для отображения уведомления
//            sendNotification(notification.title, notification.body, remoteMessage.data)
//        } ?: run {
//            // Если notification payload отсутствует, но data payload есть
//            Log.d(TAG, "MyFCMService: No notification payload, but data payload exists. Check if notification should be shown from data.")
//            // В этом блоке вы можете решить, нужно ли показывать уведомление,
//            // используя только данные из remoteMessage.data.
//            // Например, если ваш Cloud Function отправляет только `data` payload:
//            // val title = remoteMessage.data["title"] ?: "Новое событие"
//            // val body = remoteMessage.data["body"] ?: "Пожалуйста, проверьте приложение."
//            // sendNotification(title, body, remoteMessage.data)
//        }
    }

    private fun sendNotification(title: String?, body: String?, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Добавляем все кастомные данные из уведомления в Intent
            data.forEach { (key, value) -> putExtra(key, value) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE для Android 6.0+
        )

        // Используйте уникальный ID канала.
        // Это имя будет видно пользователю в настройках приложения.
        val channelId = "animal_shelter_notifications" // Уникальный ID канала
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Убедитесь, что у вас есть этот ресурс
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true) // Уведомление исчезнет при нажатии
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Для "Heads-up" уведомлений

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создание канала уведомлений для Android 8.0 (Oreo) и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Уведомления о приюте для животных", // Имя канала, видимое пользователю
                NotificationManager.IMPORTANCE_HIGH // Важность: HIGH для всплывающих уведомлений
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID уведомления */, notificationBuilder.build())
    }
}