package rx.dagger.mlunushortages.data

import rx.dagger.mlunushortages.core.localDateFromEpoch
import rx.dagger.mlunushortages.core.localDateToEpoch
import rx.dagger.mlunushortages.domain.Schedule
import rx.dagger.mlunushortages.domain.Shortages
import rx.dagger.mlunushortages.domain.Slot
import rx.dagger.mlunushortages.domain.State

fun ShortagesDto.toDomain(): Shortages {
    return Shortages(isGav, schedules.map { it.toDomain() })
}

fun ScheduleDto.toDomain(): Schedule {
    return Schedule(localDateFromEpoch(dateEpoch), slots.map { it.toDomain() })
}

fun SlotDto.toDomain(): Slot {
    val domainState = when (state) {
        1 -> State.RED
        2 -> State.YELLOW
        3 -> State.GREEN
        else -> State.GREEN
    }
    return Slot(domainState, i)
}

fun Shortages.toDto(): ShortagesDto {
    return ShortagesDto(isGav, schedules.map { it.toDto() })
}

fun Schedule.toDto(): ScheduleDto {
    return ScheduleDto(localDateToEpoch(date), slots.map { it.toDto() })
}

fun Slot.toDto(): SlotDto {
    val dtoState = when (state) {
        State.RED -> 1
        State.YELLOW -> 2
        State.GREEN -> 3
    }
    return SlotDto(dtoState, i)
}