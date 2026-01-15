package rx.dagger.mlunushortages.data

data class ShortagesDto(
    val isGav: Boolean = false,
    val todaySlots: List<SlotDto> = emptyList(),
    val tomorrowSlots: List<SlotDto> = emptyList(),
)