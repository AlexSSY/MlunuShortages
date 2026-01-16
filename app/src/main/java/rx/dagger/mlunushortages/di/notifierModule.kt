package rx.dagger.mlunushortages.di

import org.koin.dsl.module
import rx.dagger.mlunushortages.infrastructure.Notifier

val notifierModule = module {
    single {
        Notifier(get())
    }
}