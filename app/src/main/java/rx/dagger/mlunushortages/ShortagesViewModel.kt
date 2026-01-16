package rx.dagger.mlunushortages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rx.dagger.mlunushortages.domain.Repository
import rx.dagger.mlunushortages.presentation.TimerState
import java.time.Duration
import java.time.LocalDateTime

class ShortagesViewModel(
    private val repository: Repository
): ViewModel() {
    init {
        update()
    }

    val periodsFlow = shortagesRepository.periodsFlow
    val isGav = isGavRepository.isGavFlow

    private val loadingStateFlow = MutableStateFlow(false)
    val loadingStateFlowSafe = loadingStateFlow.asStateFlow()

    val nowFlow = flow {
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

    val todayShortagesTotal: StateFlow<Float> =
        combine(nowFlow, periodsFlow) { now, periods ->
            calculateTodayShortages(now, periods)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0f
        )

    val todayPeriods: StateFlow<List<PeriodWithoutElectricity>> =
        combine(nowFlow, periodsFlow) { now, periods ->
            calculateTodayPeriods(now, periods)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    private fun calculateTodayPeriods(
        now: LocalDateTime,
        periods: List<PeriodWithoutElectricity>
    ): List<PeriodWithoutElectricity> = periods.filter { it.from.dayOfMonth == now.dayOfMonth }

    fun update() {
        viewModelScope.launch {
            shortagesRepository.updateAndNotify {
                // Можно вызвать тот же showNotification(), если нужно
            }
            isGavRepository.updateAndNotify {
                // Можно вызвать тот же showNotification(), если нужно
            }
        }
    }

    private fun calculateTodayShortages(
        now: LocalDateTime,
        periods: List<PeriodWithoutElectricity>
    ): Float {
        var totalSeconds = 0L

        periods.filter { it.from.dayOfMonth == now.dayOfMonth }.forEach {
            val duration = Duration.between(it.from, it.to)
            totalSeconds += duration.seconds
        }

        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60f

        return totalHours
    }

    private fun calculateCurrentTimerState(now: LocalDateTime,
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