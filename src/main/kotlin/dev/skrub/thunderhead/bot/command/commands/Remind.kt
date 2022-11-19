package dev.skrub.thunderhead.bot.command.commands

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.components.getOption
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import java.util.concurrent.TimeUnit

class Remind : Command(
    name = "remind",
    description = "Have me remind you of something",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.NORMAL,
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val time = event.getOption<String>("time")!!
        val reminder = event.getOption<String>("reminder")!!

        if (!time.matches(Regex("/[s,m,h,d,w,y]\$/g"))) {
            val unit = when(time.last()) {
                's' -> 1
                'm' -> 60
                'h' -> 3600
                'd' -> 86400
                'w' -> 604800
                'y' -> 31557600
                else -> 0
            }
            val scheduledFor = unit * ("0${time.filter { it.isDigit() }}").toInt()

            // DEBUG
            // val ms = 1000L*scheduledFor
            // val remaining = String.format("%d:%02d", TimeUnit.MILLISECONDS.toHours(ms), TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1))


        } else {
            event.reply("Please specify a valid time, e.g., 3m, 5h.").queue()
        }
    }
    override fun addOptions(slashCommandData: SlashCommandData) {
        slashCommandData.option<String>("time", "When am I to remind you? (e.g. 30s, 2m, 24h)", true)
        slashCommandData.option<String>("reminder", "What I am to remind you of.", true)
    }
}