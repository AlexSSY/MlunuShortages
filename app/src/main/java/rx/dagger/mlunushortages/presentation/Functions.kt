package rx.dagger.mlunushortages.presentation

import rx.dagger.mlunushortages.domain.Shortages
import rx.dagger.mlunushortages.domain.Slot
import rx.dagger.mlunushortages.domain.SlotState
import java.time.LocalDateTime
import java.time.LocalTime

fun calculatePeriodsWithoutElectricity(
    shortages: Shortages
): List<PeriodWithoutElectricity> {
    val result = mutableListOf<PeriodWithoutElectricity>()
    val minutesPerSlot = 30L

    shortages.schedules.forEach { schedule ->
        var fromTime: LocalDateTime? = null
        var lastSlotTime: LocalDateTime? = null
        val dayStart = LocalDateTime.of(schedule.date, LocalTime.MIDNIGHT)

        schedule.slots.forEach { slot ->
            val slotTime = dayStart.plusMinutes(slot.i * minutesPerSlot)
            lastSlotTime = slotTime

            when {
                slot.slotState == SlotState.RED && fromTime == null -> {
                    // Начало нового периода
                    fromTime = slotTime
                }

                slot.slotState != SlotState.RED && fromTime != null -> {
                    // Конец периода
                    result.add(PeriodWithoutElectricity(fromTime, slotTime))
                    fromTime = null
                }
            }
        }

        // Если день закончился, а период ещё открыт — закрываем его последним слотом
        fromTime?.let { start ->
            result.add(PeriodWithoutElectricity(start, lastSlotTime ?: start))
        }
    }

    return result
}