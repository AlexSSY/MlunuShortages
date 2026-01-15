package rx.dagger.mlunushortages.core

import java.time.Instant
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