package rx.dagger.mlunushortages

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun PeriodWithoutElectricityDto.toDomain(): PeriodWithoutElectricity {
    val zone = ZoneId.systemDefault()

    return PeriodWithoutElectricity(
        from = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(fromEpoch),
            zone
        ),
        to = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(toEpoch),
            zone
        )
    )
}

fun PeriodWithoutElectricity.toDto(): PeriodWithoutElectricityDto {
    val zone = ZoneId.systemDefault()

    return PeriodWithoutElectricityDto(
        fromEpoch = from.atZone(zone).toEpochSecond(),
        toEpoch = to.atZone(zone).toEpochSecond()
    )
}