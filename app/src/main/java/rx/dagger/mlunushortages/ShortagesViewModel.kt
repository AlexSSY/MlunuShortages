package rx.dagger.mlunushortages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.concurrent.timer

class ShortagesViewModel: ViewModel() {
    private val shortagesStateFlow = MutableStateFlow<Shortages?>(null)
    val shortagesStateFlowSafe = shortagesStateFlow.asStateFlow()

    private val loadingStateFlow = MutableStateFlow<Boolean>(false)
    val loadingStateFlowSafe = loadingStateFlow.asStateFlow()

    private val shortagesService: ShortagesService = getMyShortagesService()

    private val periodsWithoutElectricityFlow = MutableStateFlow<List<PeriodWithoutElectricity>>(emptyList())
    val periodWithoutElectricityFlowSafe = periodsWithoutElectricityFlow.asStateFlow()

    private val timerValueFlow = MutableStateFlow<LocalTime>(LocalTime.of(0, 0, 0))
    val timerValueFlowSafe = timerValueFlow.asStateFlow()

    fun update() {
        viewModelScope.launch {
            loadingStateFlow.value = true
            try {
                val shortages = shortagesService.getShortages()
                shortagesStateFlow.value = shortages
                periodsWithoutElectricityFlow.value = calculatePeriodsWithoutElectricity(shortages)
                val currentTimerState =
                    calculateCurrentTimerState(periodsWithoutElectricityFlow.value)
                timerValueFlow.value = currentTimerState.timeRemaining
            } finally {
                loadingStateFlow.value = false
            }
        }
    }

    private fun calculateCurrentTimerState(periods: List<PeriodWithoutElectricity>): TimerState {
        var inShortage = false

        for (period in periods) {
            inShortage = period.contains(LocalDateTime.now())
        }

        return TimerState(
            true,
            LocalTime.of(1, 35, 40)
        )
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