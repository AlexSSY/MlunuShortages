package rx.dagger.mlunushortages.data

import com.google.gson.reflect.TypeToken
import rx.dagger.mlunushortages.domain.Shortages
import rx.dagger.mlunushortages.gson
import rx.dagger.mlunushortages.toDto

fun deserializeShortages(json: String): ShortagesDto =
    runCatching {
        gson.fromJson<ShortagesDto>(
            json,
            object : TypeToken<ShortagesDto>() {}.type
        )
    }.getOrElse {
        ShortagesDto(false, emptyList())
    }

fun serializeShortages(shortages: ShortagesDto): String =
    gson.toJson(shortages)
