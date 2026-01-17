package rx.dagger.mlunushortages.di

import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import rx.dagger.mlunushortages.infrastructure.ShortagesWorker

val workerModule = module {

    worker {
        ShortagesWorker(
            context = get(),
            params = get(),
            repository = get(),
            notifier = get()
        )
    }
}