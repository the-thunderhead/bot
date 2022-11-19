package dev.skrub.thunderhead.bot.command.commands

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import dev.skrub.thunderhead.bot.info.Color
import dev.skrub.thunderhead.bot.info.Emote
import dev.skrub.thunderhead.bot.util.Economy
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class Balance : Command(
    name = "balance",
    description = "Check your, or another users, balance.",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.ECONOMY
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val user = event.getOption<User>("user") ?: event.user
        val connection: Connection = DriverManager.getConnection(Economy.DATABASE)
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 30
        val balance = Economy.getBalance(user.id, statement)
        connection.close()
        event.reply(MessageCreate {
            embeds += Embed {
                description = "${user.name} has $balance ${Emote.currency}"
                color = Color.discord
            }
        }).queue()
    }

    override fun addOptions(slashCommandData: SlashCommandData) {
        slashCommandData.option<User>("user", "The user who's balance you wish to view", false)
    }
}