package dev.skrub.thunderhead.bot.command.commands

import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

class Repo : Command(
    name = "repo",
    description = "Sends a link to the bot's GitHub repository",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.DEVELOPER,
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        event.reply("https://github.com/the-thunderhead/bot").queue()
    }
}