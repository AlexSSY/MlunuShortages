package rx.dagger.mlunushortages

import java.time.LocalDateTime

data class Shortages(
    val slotMinutes: Int,
    val now: LocalDateTime,
    val timezone: String,
    val slots: List<Slot>
)
