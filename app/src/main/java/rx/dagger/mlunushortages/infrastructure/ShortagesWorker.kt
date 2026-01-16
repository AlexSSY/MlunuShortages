package rx.dagger.mlunushortages.infrastructure

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import rx.dagger.mlunushortages.domain.Repository

class ShortagesWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: Repository,
    private val notifier: Notifier
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("ShortagesWorker", "doWork executed")

        val freshShortages = runCatching {
            repository.loadFromInternet()
        }.getOrNull()

        freshShortages?.let {
            val cachedShortages = repository.loadFromCache()
            if (cachedShortages != it) {

                if (cachedShortages.schedules.size != it.schedules.size) {
                    notifier.notifyTomorrowScheduleAvailable()
                } else {
                    notifier.notifyTodayScheduleChanged()
                }

                if (cachedShortages.isGav != it.isGav) {
                    notifier.notifyGav(it.isGav)
                }

                repository.save(it)
            }
        }

        return Result.success()
    }
}