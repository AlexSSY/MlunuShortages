package rx.dagger.mlunushortages.data

import rx.dagger.mlunushortages.core.localDateFromEpoch
import rx.dagger.mlunushortages.core.localDateToEpoch
import rx.dagger.mlunushortages.domain.Schedule
import rx.dagger.mlunushortages.domain.Shortages
import rx.dagger.mlunushortages.domain.Slot
import rx.dagger.mlunushortages.domain.SlotState

fun ShortagesDto.toDomain(): Shortages {
    return Shortages(isGav, schedules.map { it.toDomain() })
}

fun ScheduleDto.toDomain(): Schedule {
    return Schedule(localDateFromEpoch(dateEpoch), slots.map { it.toDomain() })
}

fun SlotDto.toDomain(): Slot {
    val domainState = when (state) {
        1 -> SlotState.GREEN
        2 -> SlotState.RED
        3 -> SlotState.YELLOW
        else -> SlotState.GREEN
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
    val dtoState = when (slotState) {
        SlotState.GREEN -> 1
        SlotState.RED -> 2
        SlotState.YELLOW -> 3
    }
    return SlotDto(dtoState, i)
}