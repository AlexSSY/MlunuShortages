package rx.dagger.mlunushortages

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

fun serializePeriods(list: List<PeriodWithoutElectricity>): String =
    gson.toJson(list.map { it.toDto() })

fun deserializePeriods(json: String): List<PeriodWithoutElectricity> =
    gson.fromJson<List<PeriodWithoutElectricityDto>>(
        json,
        object : TypeToken<List<PeriodWithoutElectricityDto>>() {}.type
    ).map { dto ->
        dto.toDomain()
    }

fun deserializePeriodsSafe(json: String): List<PeriodWithoutElectricity> =
    runCatching {
        gson.fromJson<List<PeriodWithoutElectricityDto>>(
            json,
            object : TypeToken<List<PeriodWithoutElectricityDto>>() {}.type
        )
            .filter { it.fromEpoch > 0 && it.toEpoch > 0 }
            .map { it.toDomain() }
    }.getOrElse {
        emptyList()
    }

fun List<PeriodWithoutElectricity>.isValid(): Boolean =
    all { it.from.isBefore(it.to) }