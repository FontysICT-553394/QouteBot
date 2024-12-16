package com.beauver.discord.bots.Enums

enum class QuoteType {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year"),
    USER("User");

    val display: String;
    constructor(display: String){
        this.display = display
    }
}