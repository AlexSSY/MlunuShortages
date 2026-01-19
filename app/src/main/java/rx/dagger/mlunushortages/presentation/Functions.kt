package rx.dagger.mlunushortages.presentation

import rx.dagger.mlunushortages.domain.Schedule
import rx.dagger.mlunushortages.domain.Slot
import rx.dagger.mlunushortages.domain.SlotState
import java.time.LocalDateTime
import java.time.LocalTime

fun calculatePeriodsWithElectricity(
    schedule: Schedule
): List<PeriodWithElectricity> {
    val result = mutableListOf<PeriodWithElectricity>()

    val minutesPerSlot = 30L
    val dayStart = LocalDateTime.of(schedule.date, LocalTime.MIDNIGHT)
    var startSlot: Slot? = null
    schedule.slots.forEach { slot ->
        if (slot.slotState == SlotState.RED) {
            if (startSlot != null) {
                result.add(
                    PeriodWithElectricity(
                        start = dayStart.plusMinutes(minutesPerSlot * startSlot.i),
                        end = dayStart.plusMinutes(minutesPerSlot * slot.i)
                    )
                )
            }

            startSlot = null
        } else if (startSlot == null) {
            startSlot = slot
        }
    }

    startSlot?.let {
        val lastSlot = schedule.slots.last()

        result.add(
            PeriodWithElectricity(
                start = dayStart.plusMinutes(minutesPerSlot * it.i),
                end = dayStart.plusMinutes(minutesPerSlot * lastSlot.i + 30L)
            )
        )
    }

    return result
}