package rx.dagger.mlunushortages.domain

import java.time.LocalDateTime

data class PeriodWithoutElectricity(
    val from: LocalDateTime,
    val to: LocalDateTime,
) {
    fun contains(localDateTime: LocalDateTime): Boolean {
        return localDateTime < to && from < localDateTime
    }
}