package rx.dagger.mlunushortages.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import rx.dagger.mlunushortages.data.PoeRepository
import rx.dagger.mlunushortages.domain.Repository


val dataModule = module {

    single<Repository> {
        PoeRepository(
            context = androidContext()
        )
    }
}