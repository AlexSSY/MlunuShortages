package rx.dagger.mlunushortages.domain

data class Shortages(
    val isGav: Boolean,
    val schedules: List<Schedule>,
    val periods: List<PeriodWithoutElectricity>
)
