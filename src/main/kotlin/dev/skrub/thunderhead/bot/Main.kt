package dev.skrub.thunderhead.bot

import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.commands.restrict
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.updateCommands
import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.util.Economy
import dev.skrub.thunderhead.bot.util.Reminders
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.requests.GatewayIntent
import org.reflections.Reflections
import java.util.Timer
import kotlin.concurrent.timerTask

fun main() {
    val jda = light(System.getenv("TOKEN")!!, enableCoroutines = true) {
        intents += listOf(GatewayIntent.GUILD_MEMBERS)
    }
    Economy.setup()
    Reminders.setup()
    registerCommands(jda)
    jda.awaitReady()
    Reminders.startScheduler(jda)
    println("Ready!")
}

private fun registerCommands(jda: JDA) {
    val reflections = Reflections("dev.skrub.thunderhead.bot.command.commands")
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
