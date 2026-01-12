package rx.dagger.mlunushortages

import kotlinx.coroutines.CoroutineScope
import java.time.LocalTime

data class TimerState(
    val isElectricityAvailable: Boolean,
    val timeRemaining: LocalTime
)
