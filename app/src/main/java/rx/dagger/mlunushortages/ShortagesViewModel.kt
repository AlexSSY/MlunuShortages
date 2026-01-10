package rx.dagger.mlunushortages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShortagesViewModel: ViewModel() {
    private val shortagesStateFlow = MutableStateFlow<Shortages?>(null)
    val shortagesStateFlowSafe = shortagesStateFlow.asStateFlow()

    private val loadingStateFlow = MutableStateFlow<Boolean>(false)
    val loadingStateFlowSafe = loadingStateFlow.asStateFlow()

    private val shortagesService: ShortagesService = getMyShortagesService()

    fun update() {
        viewModelScope.launch {
            loadingStateFlow.value = true
            val shortages = shortagesService.getShortages()
            shortagesStateFlow.value = shortages
            loadingStateFlow.value = false
        }
    }
}