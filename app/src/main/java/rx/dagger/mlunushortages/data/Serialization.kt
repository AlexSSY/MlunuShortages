package rx.dagger.mlunushortages.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

fun deserializeShortages(json: String): ShortagesDto =
    runCatching {
        gson.fromJson<ShortagesDto>(
            json,
            ShortagesDto::class.java
//            object : TypeToken<ShortagesDto>() {}.type
        )
    }.getOrElse {
        ShortagesDto(false, emptyList())
    }

fun serializeShortages(shortages: ShortagesDto): String =
    gson.toJson(shortages)
