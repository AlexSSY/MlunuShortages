package rx.dagger.mlunushortages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "MlunuShortages",
            "Отключения света",
            NotificationManager.IMPORTANCE_HIGH // 🔥 именно HIGH
        ).apply {
            description = "Уведомления о новых графиках отключений"
            enableVibration(true)
            enableLights(true)
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

fun showNotification(context: Context, title: String, text: String, id: Int = 1) {
    val CHANNEL_ID = "MlunuShortages"

    // Создаём канал (Android 8+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Отключения света",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Уведомления о новых графиках отключений"
            enableLights(true)
            enableVibration(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // Строим уведомление
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_HIGH) // для Android < 8
        .setDefaults(NotificationCompat.DEFAULT_ALL)   // звук + вибрация
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(id, notification)
}