package com.beauver.discord.bots.Classes

import net.dv8tion.jda.api.entities.User

class DiscordUser {

    var userId: Long? = null
    var displayName: String? = null
    var avatarUrl: String? = null
    val quotes: MutableList<Quote> = mutableListOf()

    //<editor-fold desc="Constructors & Overloading>
    /**
     * @author Beau
     * @param userId The users discord ID
     * @param displayName the users discords Display Name
     */
    constructor(userId: Long, displayName: String){
        this.userId = userId
        this.displayName = displayName
    }

    /**
     * @author Beau
     * @param userId The users discord ID
     * @param displayName the users discords Display Name
     * @param avatarUrl The users avatar URL
     */
    constructor(userId: Long, displayName: String, avatarUrl: String){
        this.userId = userId
        this.displayName = displayName
        this.avatarUrl = avatarUrl
    }

    /**
     * @author Beau
     * @param user JDA's user class
     */
    constructor(user: User){
        this.userId = user.idLong
        this.displayName = user.effectiveName
        this.avatarUrl = user.effectiveAvatarUrl
    }
    //</editor-fold>

    fun addQuote(quote: Quote) {
        this.quotes.add(quote)
    }

    fun removeQuote(quote: Quote) {
        this.quotes.remove(quote)
    }
}