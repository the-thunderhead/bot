package info.vivime.thunderhead.util

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object Utils {
    fun request(url: String): String {
        val url = URL(url)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        val responseCode = connection.responseCode
        if (responseCode == 200) {
            var collector = ""
            val scanner = Scanner(url.openStream())

            while (scanner.hasNext()) collector += scanner.nextLine()
            scanner.close()

            return collector
        }

        throw Exception("URL responded with StatusCode $responseCode instead of 200")
    }

    fun getValueFromDotenv(variable: String) : String {
        // might there be a better way to do this?
        val envFile = File(".env").readText(Charsets.UTF_8)
        val line = envFile.lines().filter { it.startsWith(variable)}[0]
        return line.split("=")[1]
    }
}