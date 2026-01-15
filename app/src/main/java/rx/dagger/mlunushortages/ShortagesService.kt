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
    private val url = "https://www.poe.pl.ua/customs/dynamicgpv-info.php"

    suspend fun getPeriodsWithoutElectricity(): List<PeriodWithoutElectricity> =
        withContext(Dispatchers.IO) {
            val document = getDocumentFromUrl()
            val slots = parseSlotsFromDocument(document)
            val periods = calculatePeriodsWithoutElectricity(slots)

            periods
        }

    suspend fun getIsGav(): Boolean =
        withContext(Dispatchers.IO) {
            val document = getDocumentFromUrl()
            val isGav = parseIsGavFromUrl(
                "https://www.poe.pl.ua/customs/dynamic-unloading-info.php"
            )

            isGav
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
        val slots = mutableListOf<Slot>()
        val dateNumbers = mutableMapOf<LocalDateTime, List<Int>>()
        val gpvDivs = document.select(".gpvinfodetail")

        for (gpvDiv in gpvDivs) {
            val dateElement = gpvDiv.selectFirst("b")
            if (dateElement == null) {
                throw InvalidHtmlForParsing("Failed to parse dateString")
            }
            val dateString = dateElement.text()
            val date = parseUaDate(dateString)
            val tr =
                gpvDiv.selectFirst(".turnoff-scheduleui-table > tbody:nth-child(2) > tr:nth-child(2)")
            if (tr == null) {
                throw InvalidHtmlForParsing("Failed to parse tr")
            }
            val numbers = try {
                tr.select("""td[class^="light_"]""").map { td ->
                    val firstClass = td.className().split(" ").first()
                    firstClass.split("light_").last().toInt()
                }
            } catch (e: NoSuchElementException) {
                throw InvalidHtmlForParsing("Failed to parse numbers")
            }
            dateNumbers[date] = numbers
        }

        var counter = 0
        for ((date, numbers) in dateNumbers) {
            numbers.forEachIndexed { idx, number ->
                val time = date.plusSeconds(idx.toLong() * 30 * 60)
                val state = when (number) {
                    1 -> State.GREEN
                    2 -> State.RED
                    3 -> State.YELLOW
                    else -> throw InvalidHtmlForParsing("Failed to parse shortage status")
                }
                slots.add(
                    Slot(time, state, counter)
                )
                counter++
            }
        }

        return slots
    }

    fun parseUaDate(uaDateString: String): LocalDateTime {
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

        val (dayString, monthName, yearString, _) = uaDateString.split(" ")
        val day = dayString.toInt()
        val month = months[monthName] ?: 1
        val year = yearString.toInt()
        return LocalDateTime.of(
            year,
            month,
            day,
            0, 0, 0
        )
    }

    private fun calculatePeriodsWithoutElectricity(slots: List<Slot>): List<PeriodWithoutElectricity> {
        val result = mutableListOf<PeriodWithoutElectricity>()

        var from: Slot? = null
        for (slot in slots) {
            if (slot.state == State.RED && from == null) {
                from = slot
                continue
            }
            if ((slot.state == State.YELLOW || slot.state == State.GREEN) && from != null) {
                val period = PeriodWithoutElectricity(from.time, slot.time)
                result.add(period)
                from = null
            }
        }

        if (from != null) {
            val period = PeriodWithoutElectricity(from.time, slots.last().time.plusMinutes(30))
            result.add(period)
        }

        return result
    }

    private suspend fun parseIsGavFromDocument(document: Document): Boolean {
        val documentText = document.text()
        val result = documentText.contains("ГАВ", ignoreCase = false)
        return result
    }

    private suspend fun parseIsGavFromUrl(url: String): Boolean {
        val json = try {
            downloadJson(url)
        } catch (e: Exception) {
            ""
        }
        val hasGAV = json.contains("\"unloadingtypename\":\"ГАВ\"")
        return hasGAV
    }
}