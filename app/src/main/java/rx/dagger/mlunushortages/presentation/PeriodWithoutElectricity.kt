package rx.dagger.mlunushortages.presentation

import java.time.LocalDateTime

data class PeriodWithoutElectricity(
    val from: LocalDateTime,
    val to: LocalDateTime,
) {
    override fun toString(): String {
        // f(14:30) t(17:00) (today|tomorrow|after tomorrow)
        val now = LocalDateTime.now()
        val suffix = when(to.dayOfMonth - now.dayOfMonth) {
            0 -> "today"
            1 -> "tomorrow"
            2 -> "after tomorrow"
            else -> "after tomorrow"
        }
        val prefix = if (contains(now)) "-> " else ""
        return "${prefix}f(${from.hour}:${from.minute}) \t\t t(${to.hour}:${to.minute}) $suffix"
    }

    fun contains(localDateTime: LocalDateTime): Boolean {
        return localDateTime < to && from < localDateTime
    }
}