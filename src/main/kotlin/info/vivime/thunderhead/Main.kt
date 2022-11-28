package info.vivime.thunderhead

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.commands.restrict
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.updateCommands
import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import info.vivime.thunderhead.command.Command
import info.vivime.thunderhead.info.Activities
import info.vivime.thunderhead.util.Economy
import info.vivime.thunderhead.util.Reminders
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import org.reflections.Reflections
import java.util.*
import kotlin.concurrent.timerTask

fun main() {
    val jda = light(System.getenv("TOKEN")!!, enableCoroutines = true) {
        intents += listOf(GatewayIntent.GUILD_MEMBERS)
    }

    Economy.setup() // makes sure the economy database exists
    Reminders.setup() // makes sure the reminders database exists
    registerCommands(jda) // register all the commands in the Commands folder

    // we are ready to do actual things at this point !
    jda.awaitReady()

    // ...like sending out reminders
    Reminders.startScheduler(jda)

    println("The Thunderhead has attained consciousness in ${jda.guildCache.size()} guilds.") // old ready message

    // change what the bot is listening to/watching every so often
    Timer("ReminderScheduler").scheduleAtFixedRate(
        timerTask {
            val activity = Activities.LIST.random();
            jda.presence.activity = Activity.of(activity.first, activity.second);
        }, 0, Activities.CYCLE_LENGTH) // ten seconds
}

private fun registerCommands(jda: JDA) {
    val reflections = Reflections("info.vivime.thunderhead.command.commands")
    jda.updateCommands {
        for (command in reflections.getSubTypesOf(Command::class.java)) {
            val instance = command.getConstructor().newInstance()
            slash(instance.name, instance.description) {
                this.restrict(instance.restrictions.first, instance.restrictions.second)
                instance.addOptions(this)
            }
            jda.onCommand(instance.name) { event -> instance.execute(event) }
        }
    }.queue()
}
