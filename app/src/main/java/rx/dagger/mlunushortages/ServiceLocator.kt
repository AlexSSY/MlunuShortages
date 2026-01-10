package rx.dagger.mlunushortages

// Service locator function
val shortagesService: ShortagesService = ShortagesService()

fun getMyShortagesService() : ShortagesService {
    return shortagesService
}