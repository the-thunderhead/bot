package info.vivime.thunderhead.command.commands

import info.vivime.thunderhead.command.Command
import info.vivime.thunderhead.command.Type
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

class Ping : Command(
    name = "ping",
    description = "Pong! \uD83C\uDFD3",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.NORMAL,
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        event.reply("Pong! \uD83C\uDFD3").queue()
    }
}