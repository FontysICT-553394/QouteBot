package com.beauver.discord.bots.Classes

import com.beauver.discord.bots.Instance
import com.beauver.discord.bots.Enums.QuoteType
import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class Quote {

    var quoteId: Long? = null
    var quote: String? = null;
    var sender: DiscordUser? = null;
    var guildId: String? = null
    var isAutomatedQuote = false;
    var dateSaid: Date? = null;
    var quoteType: QuoteType? = null;

    //<editor-fold desc="Constructors Overloading">
    /**
     * Use this when making quote objects said by discord users.
     * @author Beau
     * @param quoteId The ID of the quote
     * @param quote The quote text
     * @param user the DiscordUser who made the quote
     * @param guildId The guild this was said in, if it was DMs, use "DM"
     * @param dateSaid The date of when this quote was said
     */
    constructor(quoteId: Long, quote: String, user: DiscordUser, guildId: String, dateSaid: Date) {
        this.quoteId = quoteId
        this.quote = quote
        this.sender = user
        this.dateSaid = dateSaid
        this.guildId = guildId
        quoteType = QuoteType.USER
    }

    /**
     * Use this when making quote objects said by discord users.
     * @author Beau
     * @param quote The quote text
     * @param dateSaid The date of when this quote was said
     * @param guildId The guild this was said in, if it was DMs, use "DM"
     * @param user the DiscordUser who made the quote
     */
    constructor(quote: String, user: DiscordUser, guildId: String, dateSaid: Date) {
        this.quote = quote
        this.sender = user
        this.dateSaid = dateSaid
        this.guildId = guildId
        quoteType = QuoteType.USER
    }

    /**
     * Use this when making quote objects automatically generated, like QOTD
     * @author Beau
     * @param quoteId The ID of the quote
     * @param quote The quote text
     * @param date The date when this quote was relevant
     * @param isAutomatedQuote Whether the quote is automated (like QOTD)
     * @param quoteType What kind of QOTX this is
     */
    constructor(quoteId: Long, quote: String, date: Date, quoteType: QuoteType, isAutomatedQuote: Boolean = true) {
        this.quoteId = quoteId
        this.quote = quote
        this.dateSaid = date
        this.isAutomatedQuote = isAutomatedQuote
        this.quoteType = quoteType
    }

    /**
     * Use this when making quote objects automatically generated, like QOTD
     * @author Beau
     * @param quote The quote text
     * @param date The date when this quote was relevant
     * @param isAutomatedQuote Whether the quote is automated (like QOTD)
     * @param quoteType What kind of QOTX this is
     */
    constructor(qoute: String, date: Date, qouteType: QuoteType, isAutomatedQuote: Boolean = true) {
        this.quote = qoute
        this.dateSaid = date
        this.isAutomatedQuote = isAutomatedQuote
        this.quoteType = qouteType
    }
    //</editor-fold>

    /**
     * @author Beau
     * @param color The color of the embed, defaults to green
     * @return Embed
     */
    fun toEmbed(requester: User, color: Color = Color.GREEN): EmbedBuilder{
        val embed = EmbedBuilder()
        embed.setDescription(quote)
        embed.setColor(color)

        if(sender != null && !isAutomatedQuote) {
            embed.setTitle("Quote ${if(quoteId == null) "" else quoteId}:")
            embed.setThumbnail(sender!!.avatarUrl)
            embed.setFooter(requester.effectiveName, requester.effectiveAvatarUrl)
            embed.addField("Sender:", sender!!.displayName!!, true)

            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy");
            embed.addField("Relevance Date:", simpleDateFormat.format(dateSaid), true)

            if(sender!!.avatarUrl != null){
                embed.setThumbnail(sender!!.avatarUrl)
            }
        }

        if(isAutomatedQuote){
            embed.setTitle("Quote of the ${quoteType.toString().lowercase()}:")
            embed.setFooter(requester.effectiveName, requester.effectiveAvatarUrl)
            embed.setThumbnail(Instance.env!!.get("BOT_PFP"))
            embed.addField("Sender:", "QuoteBot", true)
            when(quoteType){
                QuoteType.DAY -> {
                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy");
                    embed.addField("Relevance Date:", simpleDateFormat.format(dateSaid), true)
                }
                QuoteType.MONTH -> {
                    val simpleDateFormat = SimpleDateFormat("MM-yyyy");
                    embed.addField("Relevance Month:", simpleDateFormat.format(dateSaid), true)
                }
                QuoteType.YEAR -> {
                    val simpleDateFormat = SimpleDateFormat("yyyy");
                    embed.addField("Relevance Year:", simpleDateFormat.format(dateSaid), true)
                }
                QuoteType.USER -> TODO()
                null -> TODO()
            }
        }

        embed.build()
        return embed
    }

}