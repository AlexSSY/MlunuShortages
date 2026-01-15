package rx.dagger.mlunushortages.data

import rx.dagger.mlunushortages.State

data class SlotDto(
    val timeEpoch: Long,
    val state: Int,
    val i: Int
)
