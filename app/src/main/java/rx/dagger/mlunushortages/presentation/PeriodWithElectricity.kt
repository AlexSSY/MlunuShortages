package rx.dagger.mlunushortages.presentation

import java.time.LocalDateTime

data class PeriodWithElectricity(
    val start: LocalDateTime,
    val end: LocalDateTime
)
