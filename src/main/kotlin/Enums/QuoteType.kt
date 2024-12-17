package com.beauver.discord.bots.Enums

enum class QuoteType {
    DAY("Day"),
    MONTH("Month"),
    YEAR("Year"),
    USER("User");

    val display: String;
    constructor(display: String){
        this.display = display
    }
}