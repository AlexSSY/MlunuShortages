package rx.dagger.mlunushortages.domain

import kotlinx.coroutines.flow.Flow

interface Repository {
    val shortages: Flow<Shortages>
    suspend fun loadFromCache(): Shortages
    suspend fun loadFromInternet(): Shortages
    suspend fun save(shortages: Shortages)
}