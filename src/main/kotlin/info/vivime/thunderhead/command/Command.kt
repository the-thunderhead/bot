package info.vivime.thunderhead.command

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class Command(
    val name: String,
    val description: String,
    val restrictions: Pair<Boolean, DefaultMemberPermissions>,
    val type: Type,
) {
    abstract fun execute(event: GenericCommandInteractionEvent)
    open fun addOptions(slashCommandData: SlashCommandData) {
        /* Left blank because, by default, commands shouldn't have any options. */
    }
}

enum class Type {
    DEVELOPER, NORMAL, ECONOMY, MUSIC, UTILITY
}