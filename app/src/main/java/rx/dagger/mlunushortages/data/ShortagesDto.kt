package rx.dagger.mlunushortages.data

data class ShortagesDto(
    val isGav: Boolean,
    val schedules: List<ScheduleDto>
)