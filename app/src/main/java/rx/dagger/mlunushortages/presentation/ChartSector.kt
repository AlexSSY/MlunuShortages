package rx.dagger.mlunushortages.presentation

import androidx.compose.ui.graphics.Color
import rx.dagger.mlunushortages.domain.Schedule
import rx.dagger.mlunushortages.domain.SlotState

data class ChartSector(
    val startHourInMinutes: Float,
    val endHourInMinutes: Float,
    val startAngle: Float,
    val sweepAngle: Float,
    val color: Color
)


fun calculateChartSectorsFromSchedule(schedule: Schedule): List<ChartSector> {
    val hoursInDay = 24
    val degreesPerHour = 360F / hoursInDay
    val minutesPerSlot = 30L

    // hour to angle
    //hour * DEGREES_PER_HOUR - 90f

    return schedule.slots.map { slot ->
        val startHourInMinutes = slot.i * minutesPerSlot
        val endHourInMinutes = startHourInMinutes + minutesPerSlot

        val startHour = startHourInMinutes / 60F
        val endHour = endHourInMinutes / 60F

        val sweepHours =
            if (endHour >= startHour) {
                endHour - startHour
            } else {
                (24 - startHour) + endHour
            }

        val startAngle = startHour * degreesPerHour - 90F
        val sweepAngle = sweepHours * degreesPerHour
        val color = when (slot.slotState) {
            SlotState.GREEN -> Color.Green,
            SlotState.YELLOW -> Color.Yellow,
            SlotState.RED -> Color.Red
        }

        ChartSector(
            startHourInMinutes = startHourInMinutes.toFloat(),
            endHourInMinutes = endHourInMinutes.toFloat(),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            color = color
        )
    }
}