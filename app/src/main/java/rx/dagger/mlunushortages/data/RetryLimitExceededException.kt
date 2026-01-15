package rx.dagger.mlunushortages.data

import java.io.IOException

class RetryLimitExceededException(
    message: String,
    cause: Throwable? = null
) : IOException(message, cause)