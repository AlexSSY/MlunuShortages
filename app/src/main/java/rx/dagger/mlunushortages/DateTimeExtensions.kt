package rx.dagger.mlunushortages

import java.time.LocalDateTime

fun LocalDateTime.toMinutesOfDay(): Int {
    return hour * 60 + minute
}