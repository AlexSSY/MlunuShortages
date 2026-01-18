package rx.dagger.mlunushortages.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import rx.dagger.mlunushortages.domain.Repository
import rx.dagger.mlunushortages.domain.Shortages
import rx.dagger.mlunushortages.infrastructure.AlarmScheduler

class PoeRepository(
    context: Context,
) : Repository {
    private val datastoreKey = stringPreferencesKey("shortages")
    private val dataStore = context.dataStore

    private val alarmScheduler = AlarmScheduler(context)

    override val shortages: Flow<Shortages> =
        dataStore.data.map { prefs ->
            prefs[datastoreKey]
                ?.let { deserializeShortages(it).toDomain() }
                ?: Shortages(false, emptyList(), emptyList())
        }

    override suspend fun loadFromCache(): Shortages =
        dataStore.data.map { prefs ->
            prefs[datastoreKey]?.let { deserializeShortages(it).toDomain() }
        }.firstOrNull() ?: Shortages(false, emptyList(), emptyList())

    override suspend fun loadFromInternet(): Shortages {
        val shortages =downloadShortages().toDomain()
        val periodsWithoutElectricity = shortages.periods
        alarmScheduler.schedule(periodsWithoutElectricity)
        return shortages
    }

    override suspend fun save(shortages: Shortages) {
        dataStore.edit { prefs ->
            prefs[datastoreKey] = serializeShortages(shortages.toDto())
        }
    }
}