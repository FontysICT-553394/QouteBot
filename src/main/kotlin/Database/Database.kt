package com.beauver.discord.bots.Database

import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Enums.QuoteType
import com.beauver.discord.bots.Instance
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
    }

}