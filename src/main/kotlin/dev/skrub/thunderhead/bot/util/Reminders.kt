package dev.skrub.thunderhead.bot.util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

object Reminders {
    val database = "jdbc:sqlite:reminders.db"
    val CYCLE_LENGTH = 5000

    fun verify() {
        val connection: Connection = DriverManager.getConnection(Economy.database)
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 30
        statement.execute("CREATE TABLE IF NOT EXISTS Reminders(scheduled DATETIME, id VARCHAR(32), reminder VARCHAR(5000))")
        connection.close()
    }
}