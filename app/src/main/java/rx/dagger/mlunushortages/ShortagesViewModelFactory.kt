package rx.dagger.mlunushortages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShortagesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShortagesViewModel::class.java)) {
            val shortagesRepository = ShortagesRepository(context.applicationContext)
            val isGavRepository = GavRepository(context.applicationContext)
            return ShortagesViewModel(shortagesRepository, isGavRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}