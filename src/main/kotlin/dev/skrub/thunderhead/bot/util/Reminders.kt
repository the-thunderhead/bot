package dev.skrub.thunderhead.bot.util

import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import dev.skrub.thunderhead.bot.info.Color
import dev.skrub.thunderhead.bot.info.Emote
import net.dv8tion.jda.api.JDA
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.time.Instant
import java.util.*
import kotlin.concurrent.timerTask

object Reminders {
    const val DATABASE = "jdbc:sqlite:reminders.db"
    const val CYCLE_LENGTH = 10000L // ten seconds

    fun setup() {
        val connection: Connection = DriverManager.getConnection(DATABASE)
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 30
        statement.execute("CREATE TABLE IF NOT EXISTS Reminders(created DATETIME, scheduled DATETIME, id VARCHAR(32), reminder VARCHAR(5000))")
        connection.close()
    }

    fun startScheduler(jda: JDA) {
        Timer("ReminderScheduler").scheduleAtFixedRate(timerTask {
            val connection: Connection = DriverManager.getConnection(DATABASE)
            val statement: Statement = connection.createStatement()
            statement.queryTimeout =
                (CYCLE_LENGTH / 2000).toInt() // make sure we are not waiting longer than half a cycle
            val now = Date.from(Instant.now()).time
            val result =
                statement.executeQuery("SELECT created, scheduled, id, reminder FROM Reminders WHERE scheduled < ${now + CYCLE_LENGTH}")
            while (result.next()) {
                val created = result.getDate("created").time
                val scheduled = result.getDate("scheduled").time
                val id = result.getString("id")
                val reminder = result.getString("reminder")
                Timer("ScheduledReminder").schedule(timerTask {
                    val connection: Connection = DriverManager.getConnection(DATABASE)
                    val statement: Statement = connection.createStatement()
                    statement.queryTimeout = (CYCLE_LENGTH / 2000).toInt()
                    statement.executeUpdate("DELETE FROM Reminders WHERE id = '$id' AND created = $created")
                    jda.retrieveUserById(id).queue { user ->
                        user.openPrivateChannel().flatMap {
                            it.sendMessage(MessageCreate {
                                embeds += Embed {
                                    title = "Reminder ${Emote.ping}"
                                    description = String(Base64.getDecoder().decode(reminder))
                                    color = Color.discord
                                }
                            })
                        }.queue()
                    }
                }, maxOf(0, scheduled - now)) // ensure that we do not have a negative delay
            }
            connection.close()
        }, 0, CYCLE_LENGTH)
    }

    fun addReminder(id: String, scheduled: Long, reminder: String, statement: Statement) {
        val now = Date.from(Instant.now()).time
        val encodedReminder =
            Base64.getEncoder().encodeToString(reminder.toByteArray()) // protect against sql injection
        statement.executeUpdate("INSERT INTO Reminders VALUES($now, ${now + scheduled}, '$id', '$encodedReminder')")
    }
}