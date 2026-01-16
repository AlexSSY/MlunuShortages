package rx.dagger.mlunushortages.presentation

import java.time.LocalDateTime

data class TimerState(
    val isElectricityAvailable: Boolean,
    val timeRemaining: LocalDateTime? = null
)