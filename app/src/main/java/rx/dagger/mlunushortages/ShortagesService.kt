package rx.dagger.mlunushortages

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ShortagesService {
    private val url = ""

    suspend fun getShortages(): Shortages =
        withContext(Dispatchers.IO) {
            val document = getDocumentFromUrl()
            val slots = parseSlotsFromDocument(document)

            Shortages(
                30,
                LocalDateTime.now(),
                "Europe/Kiev",
                slots
            )
        }

    private fun getDocumentFromUrl(): Document {
        repeat(3) {
            try {
                val document = Jsoup.connect(url).get()
                return document
            } catch (e: SocketTimeoutException) {
                // retry
            }
        }

        throw RetryLimitExceededException(
            "Failed to connect after 3 retries",
            SocketTimeoutException()
        )
    }

    private fun parseSlotsFromDocument(document: Document): List<Slot> {
        val months = hashMapOf(
            "січня" to 1,
            "лютого" to 2,
            "березня" to 3,
            "квітня" to 4,
            "травня" to 5,
            "червня" to 6,
            "липня" to 7,
            "серпня" to 8,
            "вересня" to 9,
            "жовтня" to 10,
            "листопада" to 11,
            "грудня" to 12,
        )

//        val headings = document.select("h1")
//        val data = headings.map { it.text() }

        return emptyList()
    }
}