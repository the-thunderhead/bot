package dev.skrub.thunderhead.bot.util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.time.Instant
import java.util.*

object Economy {
    const val DATABASE = "jdbc:sqlite:economy.db"
    const val DEFAULT_BALANCE = 100
    const val DAILY_LOW = 10
    const val DAILY_HIGH = 22

    fun verify() {
        val connection: Connection = DriverManager.getConnection(DATABASE)
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 30
        statement.execute("CREATE TABLE IF NOT EXISTS Economy(id VARCHAR(32) UNIQUE, balance INTEGER, daily DATETIME, work DATETIME, created DATETIME, updated DATETIME)")
        connection.close()
    }

    fun updateBalance(id: String, amount: Int, statement: Statement) {
        setBalance(id, getBalance(id, statement) + amount, statement)
    }

    fun setBalance(id: String, amount: Int, statement: Statement) : Int{
        val now = Date.from(Instant.now()).time
        if (!exists(id, statement)) {
            statement.executeUpdate("INSERT INTO Economy VALUES('$id', $amount, 0, 0, $now, $now)")
        } else {
            statement.executeUpdate("UPDATE Economy SET balance=${amount}, updated=$now WHERE id='$id'")
        }
        return amount
    }

    fun getBalance(id: String, statement: Statement): Int {
        return if (exists(id, statement))  {
            statement.executeQuery("SELECT balance FROM Economy WHERE id='$id'").getInt(1)
        } else {
            setBalance(id, DEFAULT_BALANCE, statement)
        }
    }

    fun daily(id: String, statement: Statement): Int? {
        if (Date.from(Instant.now()).time - getDate(id, "daily", statement) >= (1000 * 60 * 60 * 24)) {
            val amount = (DAILY_LOW..DAILY_HIGH).random()
            updateBalance(id, amount, statement)
            statement.executeUpdate("UPDATE Economy SET daily=${Date.from(Instant.now()).time} WHERE id='$id'")
            return amount
        }
        return null
    }

    fun getDate(id: String, date: String, statement: Statement): Long {
        getBalance(id, statement) // ensure we exist
        return statement.executeQuery("SELECT $date FROM Economy WHERE id='$id'").getDate(1).time
    }


    private fun exists(id: String, statement: Statement): Boolean {
        return statement.executeQuery("SELECT EXISTS (SELECT 1 FROM Economy WHERE id='$id')").getBoolean(1)
    }
}
