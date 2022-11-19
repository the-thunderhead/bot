package dev.skrub.thunderhead.bot.command.commands

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import dev.skrub.thunderhead.bot.info.Color
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Scanner

class Time : Command(
    name = "time",
    description = "Check the time in another city.",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.NORMAL,
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val location = event.getOption<String>("location")!!
        try {
            val url = URL(
                "https://geocoding-api.open-meteo.com/v1/search?name=${
                    location.replace(" ", "+").replace("&", "")
                }&format=json"
            )
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                var collector = ""
                val scanner = Scanner(url.openStream())

                while (scanner.hasNext()) collector += scanner.nextLine()
                scanner.close()

                val jsonObject = JSONObject(collector)

                if (!jsonObject.has("results")) {
                    event.reply("I could not find the time in `$location`. Are you sure it exists?").queue()
                } else {
                    val result = jsonObject.getJSONArray("results").getJSONObject(0)
                    val date = ZonedDateTime.now(ZoneId.of(result.getString("timezone"))).toLocalDateTime()
                    // TODO: debating adding a footer with the chinese calendar year like in scythe
                    event.reply(MessageCreate {
                        embeds += Embed {
                            title = "${result.getString("name")}, ${result.getString("admin1")}"
                            field {
                                name = "Day"
                                value = "${date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}"
                                inline = true
                            }
                            field {
                                name = "Time"
                                value = String.format("`%d:%02d`", date.hour, date.minute)
                                inline = true
                            }
                            color = Color.discord
                        }
                    }).queue()
                }
            }
        } catch (e: Exception) {
            event.reply("An error occurred while trying to find the time in `$location`")
        }
    }

    override fun addOptions(slashCommandData: SlashCommandData) {
        slashCommandData.option<String>("location", "Where do you want to check the time?", true)
    }
}