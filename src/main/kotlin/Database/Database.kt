package com.beauver.discord.bots.Database

import com.beauver.discord.bots.Classes.DiscordUser
import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Enums.QuoteType
import com.beauver.discord.bots.Instance
import net.dv8tion.jda.api.entities.User
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.time.Instant

class Database {


    companion object{
        private val jdbc = "jdbc:mysql://${Instance.env!!.get("DB_IP")}:${Instance.env!!.get("DB_PORT")}/${Instance.env!!.get("DB_NAME")}"

        val currentQotd: Quote = getAutomaticQuoteDatabase(QuoteType.DAY, Date(Instant.now().toEpochMilli()))
        val currentQotw: Quote = getAutomaticQuoteDatabase(QuoteType.WEEK, Date(Instant.now().toEpochMilli()))
        val currentQotm: Quote = getAutomaticQuoteDatabase(QuoteType.MONTH, Date(Instant.now().toEpochMilli()))
        val currentQoty: Quote = getAutomaticQuoteDatabase(QuoteType.YEAR, Date(Instant.now().toEpochMilli()))

        private fun getConnection(): Connection {
            return DriverManager.getConnection(jdbc, Instance.env!!.get("DB_USER"), Instance.env!!.get("DB_PWD"));
        }

        /**
         * @author Beau
         * @param user Discord user who you want to get their quotes of
         * @return List of quotes
         */
        fun getUserQuotes(user: DiscordUser): List<Quote>{
            val quotes = mutableListOf<Quote>()
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                "SELECT * FROM quotes " +
                    "WHERE automated = false " +
                    "AND sender_id = ?"
            )

            stmt.setString(1, user.userId.toString())
            val rs = stmt.executeQuery()

            while(rs.next()){
                quotes.add(Quote(
                    rs.getLong("id"),
                    rs.getString("quote"),
                    DiscordUser(getUserFromId(rs.getLong("sender_id"))),
                    rs.getString("guild_id"),
                    rs.getDate("date"),
                ))
            }
            if(quotes.size <= 0){
                throw RuntimeException("Quote not found")
            }
            return quotes
        }

        fun getQuoteById(id: Int): Quote{
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                    "SELECT * FROM quotes " +
                        "WHERE id = ?"
            )
            stmt.setInt(1, id)

            val rs = stmt.executeQuery()

            if(rs.next()){
                return Quote(
                    rs.getLong("id"),
                    rs.getString("quote"),
                    DiscordUser(getUserFromId(rs.getLong("sender_id"))),
                    rs.getString("guild_id"),
                    rs.getDate("date"),
                )
            }else{
                throw RuntimeException("Quote not found")
            }
        }

        fun getQuoteByString(value: String): Quote{
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                    "SELECT * FROM quotes " +
                        "WHERE quote LIKE ?"
            )
            stmt.setString(1, "%$value%")
            val rs = stmt.executeQuery()

            if(rs.next()){
                return Quote(
                    rs.getLong("id"),
                    rs.getString("quote"),
                    DiscordUser(getUserFromId(rs.getLong("sender_id"))),
                    rs.getString("guild_id"),
                    rs.getDate("date"),
                )
            }else{
                throw RuntimeException("Quote not found")
            }
        }

        private fun getAutomaticQuoteDatabase(quoteType: QuoteType, date: Date): Quote {
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                "SELECT * FROM quotes " +
                        "WHERE automated = true " +
                        "AND quote_type = ? " +
                        "AND date = ?;"
            )
            stmt.setString(1, quoteType.toString());
            stmt.setString(2, SimpleDateFormat("yyyy/MM/dd").format(date));
            val rs = stmt.executeQuery()

            if(rs.next()){
                return Quote(
                    rs.getLong("id"),
                    rs.getString("quote"),
                    rs.getDate("date"),
                    QuoteType.valueOf(rs.getString("quote_type")),
                    rs.getBoolean("automated"),
                )
            }else{
                throw RuntimeException("Quote not found")
            }
        }

        //TODO: move to hashmap
        private fun getUserFromId(userId: Long): User {
            val cachedUser = Instance.bot!!.getUserById(userId)
            if (cachedUser != null) {
                return cachedUser
            }

            return Instance.bot!!.retrieveUserById(userId).complete()
        }
    }

}