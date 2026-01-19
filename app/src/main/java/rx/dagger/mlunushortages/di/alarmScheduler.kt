package rx.dagger.mlunushortages.di

import org.koin.dsl.module
import rx.dagger.mlunushortages.infrastructure.AlarmScheduler

val alarmScheduler = module {
    single<AlarmScheduler> {
        AlarmScheduler(get())
    }
}