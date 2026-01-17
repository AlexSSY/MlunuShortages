package rx.dagger.mlunushortages.infrastructure

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import rx.dagger.mlunushortages.R

class Notifier(
    private val context: Context
) {

    companion object {
        private const val CHANNEL_ID = "gav_status_channel"
        private const val CHANNEL_NAME = "GAV notifications"
    }

    init {
        createNotificationChannel()
    }

    fun notifyGav(isGavEnabled: Boolean) {
        val title = "Млини свет: ГАВ"
        val message =
            if (isGavEnabled) "В Млинах ввели графiк аварiйних вiдключень"
            else "GAV is cancelled"

        showNotification(
            id = 1,
            title = title,
            message = message
        )
    }

    fun notifyTodayScheduleChanged() {
        showNotification(
            id = 2,
            title = "Млини свет: графiк",
            message = "Сьогодняшнiй графiк змiнився"
        )
    }

    fun notifyTomorrowScheduleAvailable() {
        showNotification(
            id = 3,
            title = "Млини свет: графiк на завтра",
            message = "Появився графiк на завтра"
        )
    }

    private fun showNotification(
        id: Int,
        title: String,
        message: String
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(id, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}