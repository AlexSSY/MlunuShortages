package rx.dagger.mlunushortages.core

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

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