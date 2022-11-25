package info.vivime.thunderhead.command.commands

import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import info.vivime.thunderhead.command.Command
import info.vivime.thunderhead.command.Type
import info.vivime.thunderhead.info.Color
import info.vivime.thunderhead.info.Emote
import info.vivime.thunderhead.util.Economy
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*

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
        val leaderboard = Economy.getLeaderboard(7, statement, event.jda)
        event.reply(MessageCreate {
            embeds += Embed {
                title = "Leaderboard"
                description = leaderboard.indices.joinToString("\n") {
                    "**#${it + 1}** ${leaderboard[it].first} - ${leaderboard[it].second} ${Emote.currency}"
                }.ifEmpty { "`Error: No Users in Database`" }
                color = Color.discord
            }
        }).queue()
        connection.close()
    }
}