package rx.dagger.mlunushortages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
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

    private val timerStateFlow = MutableStateFlow<TimerState>(TimerState(true))
    val timerStateFlowSafe = timerStateFlow.asStateFlow()

    fun update() {
        viewModelScope.launch {
            loadingStateFlow.value = true
            try {
                val shortages = shortagesService.getShortages()
                shortagesStateFlow.value = shortages
                periodsWithoutElectricityFlow.value = calculatePeriodsWithoutElectricity(shortages)
                val currentTimerState =
                    calculateCurrentTimerState(periodsWithoutElectricityFlow.value)
                timerStateFlow.value = currentTimerState
            } finally {
                loadingStateFlow.value = false
            }
        }
    }

    private fun calculateCurrentTimerState(periods: List<PeriodWithoutElectricity>): TimerState {
        val now = LocalDateTime.now()

        for (period in periods) {
            if (period.contains(now)) {
                return TimerState(
                    isElectricityAvailable = false,
                    timeRemaining = period.to
                )
            }
        }
        val actualPeriods = periods.filter { it.from > now }
        val nearestPeriod = actualPeriods.minByOrNull { Duration.between(now, it.from) }

        return TimerState(
            isElectricityAvailable = true,
            timeRemaining = nearestPeriod?.from
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
            if ((slot.state == State.YELLOW || slot.state == State.GREEN) && from != null) {
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