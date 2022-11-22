package dev.skrub.thunderhead.bot.command.commands


import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import dev.skrub.thunderhead.bot.info.Color
import dev.skrub.thunderhead.bot.info.Emote
import dev.skrub.thunderhead.bot.util.Economy
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

class Daily : Command(
    name = "daily",
    description = "Your daily BIG (Basic Income Guarantee",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.ECONOMY
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val connection: Connection = DriverManager.getConnection(Economy.DATABASE)
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 30
        val daily = Economy.daily(event.user.id, statement)
        if (daily != null) {
            val balance = Economy.getBalance(event.user.id, statement)
            event.reply(MessageCreate {
                embeds += Embed {
                    description = "You have received $daily ${Emote.currency} as part of your BIG (Basic Income Guarantee). Your balance is now $balance ${Emote.currency}"
                    color = Color.discord
                }
            }).queue()
        } else {
            val ms =  (1000 * 60 * 60 * 24) - (Date.from(Instant.now()).time - Economy.getDate(event.user.id, "daily", statement))
            val remaining = String.format("%d:%02d", TimeUnit.MILLISECONDS.toHours(ms), TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1))
            event.reply(MessageCreate {
                embeds += Embed {
                    description = "I’m sorry, you have already collected your BIG (Basic Income Guarantee) for today. You may collect it again in `$remaining` ${Emote.time}"
                    color = Color.discord
                }
            }).queue()
        }
        connection.close()
    }
}