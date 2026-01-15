package rx.dagger.mlunushortages.domain

import kotlinx.coroutines.flow.Flow

interface Repository {
    val shortages: Flow<Shortages>
    suspend fun loadCached()
    suspend fun refresh()
}