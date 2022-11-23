package dev.skrub.thunderhead.bot.command.commands

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import dev.skrub.thunderhead.bot.info.Color
import dev.skrub.thunderhead.bot.util.Utils
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

class Weather : Command(
    name = "weather",
    description = "Check the weather.",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.NORMAL,
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val location = event.getOption<String>("location")!!
        try {
            val response = JSONObject(
                Utils.request(
                    "https://geocoding-api.open-meteo.com/v1/search?name=${
                        location.replace(" ", "+").replace("&", "")
                    }"
                )
            )

            if (!response.has("results")) {
                event.reply("I could not find `$location`. Are you sure it exists?").queue()
            } else {
                val result = response.getJSONArray("results").getJSONObject(0)
                val response = JSONObject(
                    Utils.request(
                        "https://api.open-meteo.com/v1/forecast" +
                                "?latitude=${result.getNumber("latitude")}&longitude=${result.getNumber("longitude")}" +
                                "&hourly=weathercode,temperature_2m,apparent_temperature,precipitation,visibility,windspeed_10m" +
                                "&daily=sunrise,sunset" +
                                "&temperature_unit=fahrenheit&precipitation_unit=inch&windspeed_unit=mph" + // USA USA USA
                                "&timeformat=unixtime&timezone=auto&past_days=1"
                    )
                )
                val getTimeFromDate = SimpleDateFormat("HH:mm")
                getTimeFromDate.timeZone = TimeZone.getTimeZone(ZoneId.of(result.getString("timezone")))
                val hourly = response.getJSONObject("hourly")
                val daily = response.getJSONObject("daily")
                val sunrise = daily.getJSONArray("sunrise").getInt(0)
                val sunset = daily.getJSONArray("sunset").getInt(0)
                event.reply(MessageCreate {
                    embeds += Embed {
                        title = "${result.getString("name")} — Weather"
                        field {
                            name = "Time"
                            value = getTimeFromDate.format(Instant.now().epochSecond * 1000L)
                            inline = true
                        }
                        field {
                            name = "Temperature"
                            value = "${hourly.getJSONArray("apparent_temperature").getNumber(0)}°F"
                            inline = true
                        }
                        field {
                            name = "Feels Like"
                            value = "${hourly.getJSONArray("temperature_2m").getNumber(0)}°F"
                            inline = true
                        }
                        field {
                            name = "Sunrise"
                            value = getTimeFromDate.format(sunrise * 1000L)
                            inline = true
                        }
                        field {
                            name = "Sunset"
                            value = getTimeFromDate.format(sunset * 1000L)
                            inline = true
                        }
                        field {
                            name = "Windspeed"
                            value = "${hourly.getJSONArray("windspeed_10m").getNumber(0)} mph"
                            inline = true
                        }
                        thumbnail = String.format(
                            "https://openweathermap.org/img/wn/%02d${
                                if (Date.from(Instant.now()).time / 1000 in (sunrise..sunset)) {
                                    "d"
                                } else {
                                    "n"
                                }
                            }@4x.png", when (hourly.getJSONArray("weathercode").getInt(0)) {
                                0 -> 1
                                1 -> 2
                                2 -> 2
                                in (3..4) -> 3
                                in (45..48) -> 50
                                in (51..55) -> 9
                                in (56..57) -> 13
                                in (61..65) -> 10
                                in (66..77) -> 13
                                in (80..82) -> 9
                                in (85..86) -> 13
                                in (95..99) -> 11
                                else -> 3
                            }
                        )
                        color = Color.discord
                    }
                }).queue()

            }
        } catch (e: Exception) {
            event.reply("An error occurred while trying to find the weather in `$location`")
        }
    }

    override fun addOptions(slashCommandData: SlashCommandData) {
        slashCommandData.option<String>("location", "Where do you want to check the time?", true)
    }
}