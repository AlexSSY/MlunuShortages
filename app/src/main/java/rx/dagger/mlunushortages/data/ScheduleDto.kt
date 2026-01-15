package rx.dagger.mlunushortages.data

data class ScheduleDto(
    val dateEpoch: Long,
    val slots: List<SlotDto>
)
