package dev.skrub.thunderhead.bot.command.commands

import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import dev.skrub.thunderhead.bot.info.Color
import dev.skrub.thunderhead.bot.util.Economy
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class Leaderboard : Command(
    name = "leaderboard",
    description = "View the richest people!",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.ECONOMY,
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val connection: Connection = DriverManager.getConnection(Economy.DATABASE)
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 30
        val leaderboard = Economy.getLeaderboard(7, statement)
        connection.close()
        event.reply(MessageCreate {
            embeds += Embed {
                description = leaderboard.joinToString("\n") { "${it.first} - ${it.second}" }
                color = Color.discord
            }
        }).queue()
    }
}