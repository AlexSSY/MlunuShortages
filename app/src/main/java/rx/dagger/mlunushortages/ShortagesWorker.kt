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

        val shortagesRepository = ShortagesRepository(applicationContext)

        shortagesRepository.updateAndNotify {
            showNotification(
                context = applicationContext,
                title = "Изменения в граффике",
                text = "Изменился текущий граффик, или появился граффик на завтра"
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