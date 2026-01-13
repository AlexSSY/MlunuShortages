package rx.dagger.mlunushortages

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ShortagesRepository(private val context: Context) {

    private val dataStore = context.dataStore

    val periodsFlow: Flow<List<PeriodWithoutElectricity>> =
        dataStore.data.map { prefs ->
            prefs[PERIODS_KEY]
                ?.let { deserializePeriods(it) }
                ?: emptyList()
        }

    suspend fun loadCached(): List<PeriodWithoutElectricity> {
        val json = dataStore.data.map { prefs -> prefs[PERIODS_KEY] }.firstOrNull() ?: return emptyList()

        val periods = deserializePeriodsSafe(json)

        return if (periods.isValid()) {
            periods
        } else {
            clearCache()
            emptyList()
        }
    }

    suspend fun clearCache() {
        dataStore.edit { it.remove(PERIODS_KEY) }
    }

    suspend fun save(newData: List<PeriodWithoutElectricity>) {
        dataStore.edit { prefs ->
            prefs[PERIODS_KEY] = serializePeriods(newData)
        }
    }

    suspend fun fetchFromNetwork(): List<PeriodWithoutElectricity> =
        getMyShortagesService().getPeriodsWithoutElectricity()

    suspend fun updateAndNotify(showNotification: () -> Unit) {
        val oldData = loadCached()
        val newData = fetchFromNetwork()

        if (oldData != newData) {
            save(newData)
            showNotification()
        }
    }
}
