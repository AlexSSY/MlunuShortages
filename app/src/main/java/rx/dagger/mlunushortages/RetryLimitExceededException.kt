package rx.dagger.mlunushortages

import java.io.IOException

class RetryLimitExceededException(
    message: String,
    cause: Throwable? = null
) : IOException(message, cause)