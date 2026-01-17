package rx.dagger.mlunushortages.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import rx.dagger.mlunushortages.presentation.ShortagesViewModel

val viewModelModule = module {

    viewModel {
        ShortagesViewModel(
            repository = get(),
            notifier = get()
        )
    }
}