package rx.dagger.mlunushortages.domain

import java.time.LocalDate

data class Schedule(
    val date: LocalDate,
    val slots: List<Slot>
)
