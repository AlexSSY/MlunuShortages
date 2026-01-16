package rx.dagger.mlunushortages.domain

import java.time.LocalDateTime

data class Slot(
    val state: State,
    val i: Int
)
