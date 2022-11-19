package dev.skrub.thunderhead.bot.util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.time.Instant
import java.util.*

object Reminders {
    const val DATABASE = "jdbc:sqlite:reminders.db"
    const val CYCLE_LENGTH = 5000

    fun verify() {
        val connection: Connection = DriverManager.getConnection(DATABASE)
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 30
        statement.execute("CREATE TABLE IF NOT EXISTS Reminders(created DATETIME, scheduled DATETIME, id VARCHAR(32), reminder VARCHAR(5000))")
        connection.close()
    }

    fun addReminder(id: String, scheduled: Long, reminder: String, statement: Statement) {
        val now = Date.from(Instant.now()).time
        statement.executeUpdate("INSERT INTO Reminders VALUES('$now', ${now + scheduled}, $id, $reminder)")
    }
}