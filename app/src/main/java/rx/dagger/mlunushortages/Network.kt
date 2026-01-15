package rx.dagger.mlunushortages

import okhttp3.OkHttpClient
import okhttp3.Request

suspend fun downloadJson(url: String): String {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    return client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            throw Exception("HTTP error ${response.code}")
        }
        response.body?.string() ?: ""
    }
}