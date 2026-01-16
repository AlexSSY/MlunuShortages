package rx.dagger.mlunushortages.data

import java.io.IOException

class InvalidHtmlForParsing(
    message: String,
    cause: Throwable? = null
) : IOException(message, cause)