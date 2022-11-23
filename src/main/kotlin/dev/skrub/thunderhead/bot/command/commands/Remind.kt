package dev.skrub.thunderhead.bot.command.commands

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.components.getOption
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import dev.skrub.thunderhead.bot.util.Reminders
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class Remind : Command(
    name = "remind",
    description = "Have me remind you of something",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.NORMAL,
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val time = event.getOption<String>("time")!!
        val reminder = event.getOption<String>("reminder")!!

        if (Regex("^(\\d+)[smhdwy]\$").matches(time)) {
            val unit = when (time.last()) {
                's' -> 1
                'm' -> 60
                'h' -> 3600
                'd' -> 86400
                'w' -> 604800
                'y' -> 31557600
                else -> 0
            }
            val connection: Connection = DriverManager.getConnection(Reminders.DATABASE)
            val statement: Statement = connection.createStatement()
            statement.queryTimeout = 30
            Reminders.addReminder(
                event.user.id,
                unit * ("0${time.filter { it.isDigit() }}").toInt() * 1000L,
                reminder,
                statement
            )
            event.reply("I will be sure to remind you of that.").queue()
            connection.close()
        } else {
            event.reply("Please specify a valid time, e.g., 3m, 5h.").queue()
        }
    }

    override fun addOptions(slashCommandData: SlashCommandData) {
        slashCommandData.option<String>("time", "When am I to remind you? (e.g. 30s, 2m, 24h)", true)
        slashCommandData.option<String>("reminder", "What I am to remind you of.", true)
    }
}