package rx.dagger.mlunushortages

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.concurrent.timer

class ShortagesViewModel(
    private val repository: ShortagesRepository
): ViewModel() {
    init {
        viewModelScope.launch {
            repository.updateAndNotify {
                // Можно вызвать тот же showNotification(), если нужно
            }
        }
    }
    val periodsFlow = repository.periodsFlow

    private val loadingStateFlow = MutableStateFlow(false)
    val loadingStateFlowSafe = loadingStateFlow.asStateFlow()

    private val nowFlow = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(1000)
        }
    }

    val timerStateFlowSafe: StateFlow<TimerState> =
        combine(nowFlow, periodsFlow) { now, periods ->
            calculateCurrentTimerState(now, periods)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TimerState(true, null)
        )

    val periodsActualFlow: StateFlow<List<PeriodWithoutElectricity>> =
        combine(nowFlow, periodsFlow) { now, periods ->
            periods
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun update() {
        viewModelScope.launch {
            loadingStateFlow.value = true
            try {
//                val shortages = shortagesService.getShortages()
//                shortagesStateFlow.value = shortages
//                periodsWithoutElectricityFlow.value = calculatePeriodsWithoutElectricity(shortages)
//                val currentTimerState =
//                    calculateCurrentTimerState(periodsWithoutElectricityFlow.value)
//                timerStateFlow.value = currentTimerState
            } finally {
                loadingStateFlow.value = false
            }
        }
    }

    private fun calculateCurrentTimerState(
        now: LocalDateTime,
        periods: List<PeriodWithoutElectricity>
    ): TimerState {
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
}