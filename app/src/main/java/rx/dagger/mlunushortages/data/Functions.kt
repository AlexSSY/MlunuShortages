package rx.dagger.mlunushortages.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import rx.dagger.mlunushortages.InvalidHtmlForParsing
import rx.dagger.mlunushortages.Slot
import rx.dagger.mlunushortages.State
import rx.dagger.mlunushortages.core.localDateTimeToEpoch
import java.net.SocketTimeoutException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

private val slotsUrl = "https://www.poe.pl.ua/customs/dynamicgpv-info.php"
private val gavUrl = "https://www.poe.pl.ua/customs/dynamic-unloading-info.php"

suspend fun downloadShortages(): ShortagesDto {
    val allSlots = downloadSlots()

    val isGav = fetchGavStatus()
    val todaySlots = allSlots.slice(0..47)
    val tomorrowSlots = allSlots.slice(48..95)

    return ShortagesDto(isGav, todaySlots, tomorrowSlots)
}

private fun downloadSlots(): List<SlotDto> {
    val document = getDocumentFromUrl(slotsUrl)

    val slots = mutableListOf<SlotDto>()
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
            slots.add(
                SlotDto(localDateTimeToEpoch(time), number, counter)
            )
            counter++
        }
    }

    return slots
}

private suspend fun fetchGavStatus(): Boolean = withContext(Dispatchers.IO) {
    val json = downloadJson(gavUrl)
    val hasGAV = json.contains("\"unloadingtypename\":\"ГАВ\"")
    hasGAV
}

private fun getDocumentFromUrl(url: String): Document {
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

private fun parseUaDate(uaDateString: String): LocalDateTime {
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

private fun downloadJson(url: String): String {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    repeat(3) {
        try {
            return client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("HTTP error ${response.code}")
                }
                response.body?.string() ?: ""
            }
        } catch (e: Exception) {
            // retry
        }
    }

    throw RetryLimitExceededException(
        "Failed to connect after 3 retries",
        SocketTimeoutException()
    )
}