package com.beauver.discord.bots.Database

import com.beauver.discord.bots.Classes.DiscordUser
import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Enums.QuoteType
import com.beauver.discord.bots.Instance
import net.dv8tion.jda.api.entities.User
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.time.Instant

class Database {


    companion object{
        val userCache = mutableMapOf<Long, User>()
        private val jdbc = "jdbc:mysql://${Instance.env!!.get("DB_IP")}:${Instance.env!!.get("DB_PORT")}/${Instance.env!!.get("DB_NAME")}"

        var currentQotd: Quote by MutableLazy { getAutomaticQuoteDatabase(QuoteType.DAY, Date(Instant.now().toEpochMilli())) }
        var currentQotm: Quote by MutableLazy { getAutomaticQuoteDatabase(QuoteType.MONTH, Date(Instant.now().toEpochMilli())) }
        var currentQoty: Quote by MutableLazy { getAutomaticQuoteDatabase(QuoteType.YEAR, Date(Instant.now().toEpochMilli())) }

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

        fun addUserQuote(quote: Quote){
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                "INSERT INTO quotes(quote, sender_id, guild_id, date, quote_type, automated) " +
                        "VALUES (?,?,?,?,'USER',false)")

            stmt.setString(1, quote.quote)
            stmt.setLong(2, quote.sender!!.userId!!.toLong())
            stmt.setString(3, quote.guildId)
            stmt.setDate(4, Date(quote.dateSaid!!.time))

            if(stmt.executeUpdate() <= 0){
                throw RuntimeException("Quote could not be created.")
            }
        }

        fun updateAutomaticQuoteDatabase(quote: Quote, oldQuote: Quote){
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                    "UPDATE quotes SET quote = ? " +
                            "WHERE automated = true " +
                            "AND quote_type = ? " +
                            "AND quote = ?"
            )
            stmt.setString(1, quote.quote)
            stmt.setString(2, quote.quoteType.toString().uppercase())
            stmt.setString(3, oldQuote.quote)

            val rs = stmt.executeUpdate()

            if(rs <= 0){
                throw RuntimeException("Quote could not be updated")
            }

            currentQotd = getAutomaticQuoteDatabase(QuoteType.DAY, Date(Instant.now().toEpochMilli()))
            currentQotm = getAutomaticQuoteDatabase(QuoteType.MONTH, Date(Instant.now().toEpochMilli()))
            currentQoty = getAutomaticQuoteDatabase(QuoteType.YEAR, Date(Instant.now().toEpochMilli()))
        }

        fun addAutomaticQuoteDatabase(quote: Quote){
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                "INSERT INTO quotes(quote, sender_id, guild_id, date, quote_type, automated)" +
                        "VALUES (?,1318175554557710336,0,?,?,true)")

            stmt.setString(1, quote.quote)
            stmt.setDate(2, Date(quote.dateSaid!!.time))
            stmt.setString(3, quote.quoteType.toString().uppercase())

            val rs = stmt.executeUpdate();

            if(rs <= 0){
                throw RuntimeException("Quote could not be sent to database.")
            }

            currentQotd = getAutomaticQuoteDatabase(QuoteType.DAY, Date(Instant.now().toEpochMilli()))
            currentQotm = getAutomaticQuoteDatabase(QuoteType.MONTH, Date(Instant.now().toEpochMilli()))
            currentQoty = getAutomaticQuoteDatabase(QuoteType.YEAR, Date(Instant.now().toEpochMilli()))
        }

        private fun getAutomaticQuoteDatabase(quoteType: QuoteType, date: Date): Quote {
            val conn = getConnection()

            val stmt = conn.prepareStatement(
                "SELECT * FROM quotes " +
                        "WHERE automated = true " +
                        "AND quote_type = ? " +
                        "AND DATE_FORMAT(date, '%Y-%m-%d') LIKE ?"
            )
            stmt.setString(1, quoteType.toString().uppercase());

            when(quoteType) {
                QuoteType.DAY -> stmt.setString(2, "%" + SimpleDateFormat("yyyy-MM-dd").format(date) + "%");
                QuoteType.MONTH -> stmt.setString(2, "%" + SimpleDateFormat("yyyy-MM").format(date) + "%");
                QuoteType.YEAR -> stmt.setString(2, "%" + SimpleDateFormat("yyyy").format(date) + "%");
                else -> "";
            }
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

        //Hopefully getting around rate limit, no clue if this'll work
        private fun getUserFromId(userId: Long): User {
            val customCache = userCache[userId]
            if (customCache != null) {
                return customCache
            }
            val cachedUser = Instance.bot!!.getUserById(userId)
            if (cachedUser != null) {
                return cachedUser
            }

            val retrievedUser = Instance.bot!!.retrieveUserById(userId).complete()
            userCache.put(userId, retrievedUser)
            return retrievedUser
        }
    }

}