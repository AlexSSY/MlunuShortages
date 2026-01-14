package rx.dagger.mlunushortages

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GavRepository(private val context: Context) {
    private val dataStore = context.dataStore

    val isGavFlow: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[IS_GAV_KEY]
                ?.let{ deserializeIsGavSafe(it) }
                ?: false
        }

    suspend fun loadCached(): Boolean {
        val json = dataStore.data.map { prefs -> prefs[IS_GAV_KEY] }.firstOrNull() ?: return false

        val isGav = deserializeIsGavSafe(json)

        return isGav
    }

    suspend fun clearCache() {
        dataStore.edit { it.remove(IS_GAV_KEY) }
    }

    suspend fun save(newData: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_GAV_KEY] = serializeIsGav(newData)
        }
    }

    suspend fun fetchFromNetwork(): Boolean =
        getMyShortagesService().getIsGav()

    suspend fun updateAndNotify(showNotification: () -> Unit) {
        val oldData = loadCached()

        val newData = try {
            fetchFromNetwork()
        } catch (e: Exception) {
            Log.e("IS_GAV: fetchFromNetwork", "An error occurred:", e)
            null
        }

        newData?.let {
            if (oldData != newData) {
                save(newData)
                showNotification()
            }
        }
    }
}