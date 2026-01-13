package rx.dagger.mlunushortages

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        scheduleShortagesWorker()
    }

    private fun scheduleShortagesWorker() {
        val workRequest =
            PeriodicWorkRequestBuilder<ShortagesWorker>(
                15, TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "shortages_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}
