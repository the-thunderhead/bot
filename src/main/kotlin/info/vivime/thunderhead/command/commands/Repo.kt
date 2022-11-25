package info.vivime.thunderhead.command.commands

import info.vivime.thunderhead.command.Command
import info.vivime.thunderhead.command.Type
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