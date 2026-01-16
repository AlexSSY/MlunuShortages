package rx.dagger.mlunushortages

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.context.GlobalContext.startKoin
import rx.dagger.mlunushortages.di.dataModule
import rx.dagger.mlunushortages.di.viewModelModule
import rx.dagger.mlunushortages.di.workerModule
import java.util.concurrent.TimeUnit

class App : Application(), Configuration.Provider {

    init {
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                viewModelModule,
                workerModule
            )
        }
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(this)
        scheduleShortagesWorker()
    }

    private fun scheduleShortagesWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest =
            PeriodicWorkRequestBuilder<ShortagesWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "shortages_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    override val workManagerConfiguration: Configuration =
        Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()
}
