package rx.dagger.mlunushortages.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rx.dagger.mlunushortages.presentation.PeriodWithoutElectricity
import rx.dagger.mlunushortages.domain.Repository
import rx.dagger.mlunushortages.domain.Shortages
import java.time.Duration
import java.time.LocalDateTime

class ShortagesViewModel(
    private val repository: Repository
): ViewModel() {
    init {
        update()
    }

    val shortagesStateFlow = repository.shortages
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            Shortages(false, emptyList())
        )

    val periodsWithoutElectricityStateFlow =
        shortagesStateFlow
            .map { calculatePeriodsWithoutElectricity(it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private val nowFlow =
        flow {
            while (true) {
                emit(LocalDateTime.now())
                delay(1000)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LocalDateTime.now()
        )

    val timerStateFlow =
        combine(nowFlow, periodsWithoutElectricityStateFlow) { now, periods ->
            calculateCurrentTimerState(now, periods)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TimerState(isElectricityAvailable = true, timeRemaining = null)
        )


    val periodsActualFlow: StateFlow<List<PeriodWithoutElectricity>> =
        combine(nowFlow, periodsWithoutElectricityStateFlow) { now, periods ->
            periods
        }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000),
            emptyList()
        )

    val todayShortagesTotal: StateFlow<Float> =
        combine(nowFlow, periodsWithoutElectricityStateFlow) { now, periods ->
            calculateTodayShortages(now, periods)
        }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000),
            0f
        )

    val todayPeriods: StateFlow<List<PeriodWithoutElectricity>> =
        combine(nowFlow, periodsWithoutElectricityStateFlow) { now, periods ->
            periods.filter { it.from.dayOfMonth == now.dayOfMonth }
        }.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000),
            emptyList()
        )

    fun update() {
        viewModelScope.launch {
            val shortages = runCatching {
                repository.loadFromInternet()
            }.getOrNull()

            shortages?.let {
                val cachedShortages = repository.loadFromCache()

                if (cachedShortages != it) {
                    repository.save(it)
                }
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