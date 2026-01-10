package rx.dagger.mlunushortages

import java.time.LocalDateTime

data class Slot(
    val time: LocalDateTime,
    val state: State,
    val i: Int
)
