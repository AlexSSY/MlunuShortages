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

    private val periodsWithoutElectricityFlow = MutableStateFlow<List<PeriodWithoutElectricity>>(emptyList())
    val periodWithoutElectricityFlowSafe = periodsWithoutElectricityFlow.asStateFlow()

    fun update() {
        viewModelScope.launch {
            loadingStateFlow.value = true
            val shortages = shortagesService.getShortages()
            shortagesStateFlow.value = shortages
            periodsWithoutElectricityFlow.value = calculatePeriodsWithoutElectricity(shortages)
            loadingStateFlow.value = false
        }
    }

    private fun calculatePeriodsWithoutElectricity(shortages: Shortages): List<PeriodWithoutElectricity> {
        val result = mutableListOf<PeriodWithoutElectricity>()

        var from: Slot? = null
        for (slot in shortages.slots) {
            if (slot.state == State.RED && from == null) {
                from = slot
                continue
            }
            if (slot.state == State.YELLOW && from != null) {
                val period = PeriodWithoutElectricity(from.time, slot.time)
                result.add(period)
                from = null
            }
        }

        if (from != null) {
            val period = PeriodWithoutElectricity(from.time, shortages.slots.last().time.plusMinutes(30))
            result.add(period)
        }

        return result
    }
}