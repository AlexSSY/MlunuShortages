package rx.dagger.mlunushortages.data

import rx.dagger.mlunushortages.domain.Schedule
import rx.dagger.mlunushortages.domain.Shortages
import rx.dagger.mlunushortages.domain.Slot
import rx.dagger.mlunushortages.domain.SlotState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

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

fun localDateTimeToEpoch(localDateTime: LocalDateTime): Long {
    val zone = ZoneId.systemDefault()
    return localDateTime.atZone(zone).toEpochSecond()
}

fun localDateTimeFromEpoch(ePoch: Long): LocalDateTime {
    val zone = ZoneId.systemDefault()
    return LocalDateTime.ofInstant(
        Instant.ofEpochSecond(ePoch),
        zone
    )
}

fun localDateToEpoch(localDate: LocalDate): Long {
    val zone = ZoneId.systemDefault()
    return localDate
        .atStartOfDay(zone)
        .toEpochSecond()
}

fun localDateFromEpoch(epoch: Long): LocalDate {
    val zone = ZoneId.systemDefault()
    return Instant
        .ofEpochSecond(epoch)
        .atZone(zone)
        .toLocalDate()
}

fun LocalDateTime.toMinutesOfDay(): Int {
    return hour * 60 + minute
}