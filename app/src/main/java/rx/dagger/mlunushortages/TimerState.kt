package rx.dagger.mlunushortages

import kotlinx.coroutines.CoroutineScope
import java.time.Duration
import java.time.LocalDateTime

data class TimerState(
    val isElectricityAvailable: Boolean,
    val timeRemaining: LocalDateTime? = null
)
