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
import rx.dagger.mlunushortages.domain.Schedule
import rx.dagger.mlunushortages.domain.Shortages
import rx.dagger.mlunushortages.domain.SlotState
import java.time.Duration
import java.time.LocalDate
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

    val nowFlow =
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

    val todaySchedule: StateFlow<Schedule> =
        combine(nowFlow, shortagesStateFlow) { now, shortages ->
            shortages.schedules.forEach { schedule ->
                if (schedule.date.year == now.year &&
                        schedule.date.monthValue == now.monthValue &&
                        schedule.date.dayOfMonth == now.dayOfMonth) {
                    return@combine schedule
                }
            }

            Schedule(LocalDate.of(
                now.year, now.monthValue, now.dayOfMonth
            ), emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Schedule(LocalDate.now(), emptyList())
        )

    val tomorrowSchedule: StateFlow<Schedule?> =
        combine(nowFlow, shortagesStateFlow) { now, shortages ->
            val tomorrow = now.plusDays(1)
            shortages.schedules.find { schedule ->
                schedule.date.year == tomorrow.year &&
                schedule.date.monthValue == tomorrow.monthValue &&
                schedule.date.dayOfMonth == tomorrow.dayOfMonth
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val todayChartSectors: StateFlow<List<ChartSector>> =
        todaySchedule.map {
            calculateChartSectors(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val tomorrowChartSectors: StateFlow<List<ChartSector>?> =
        tomorrowSchedule.map { schedule ->
            schedule?.let {
                calculateChartSectors(it)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private fun calculateChartSectors(schedule: Schedule): List<ChartSector> {
        return schedule.slots
            .filter { slot ->
                slot.slotState == SlotState.RED
            }
            .map { slot ->
                val startHour = 30F * slot.i
                val endHour = startHour + 30F
                ChartSector(startHour, endHour)
            }
    }

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
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val todayShortagesTotal: StateFlow<Float> =
        combine(nowFlow, periodsWithoutElectricityStateFlow) { now, periods ->
            calculateTodayShortages(now, periods)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0f
        )

    val tomorrowShortagesTotal: StateFlow<Float> =
        combine(nowFlow, periodsWithoutElectricityStateFlow) { now, periods ->
            val tomorrow = now.plusDays(1)
            calculateTodayShortages(tomorrow, periods)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0f
        )

    val todayPeriods: StateFlow<List<PeriodWithoutElectricity>> =
        combine(nowFlow, periodsWithoutElectricityStateFlow) { now, periods ->
            periods.filter { it.from.dayOfMonth == now.dayOfMonth }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
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