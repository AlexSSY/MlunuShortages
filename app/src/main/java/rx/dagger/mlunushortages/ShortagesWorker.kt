package rx.dagger.mlunushortages

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ShortagesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("ShortagesWorker", "doWork STARTED")

        showNotification(
            context = applicationContext,
            title = "Test",
            text = "Test Notification"
        )

        val shortagesRepository = ShortagesRepository(applicationContext)

        shortagesRepository.updateAndNotify {
            showNotification(
                context = applicationContext,
                title = "Новый график отключений",
                text = "Появился график на завтра"
            )
        }

        val gpvRepository = GavRepository(applicationContext)

        gpvRepository.updateAndNotify {
            showNotification(
                context = applicationContext,
                title = "ГАВ",
                text = "Изменился ГАВ"
            )
        }

        return Result.success()
    }
}