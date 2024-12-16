package com.beauver.discord.bots

import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Commands.GetQuoteAutomated
import com.beauver.discord.bots.Commands.SetAutomatedQuote
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import java.util.logging.Logger

var bot: JDA? = null
var env: Dotenv? = null
var logger: Logger = Logger.getLogger("Quote-Discord-Bot")

fun main() {
    setupEnv()
    startBot()
    registerCommands()
}

fun startBot(){
    bot = JDABuilder.createDefault(env!!.get("BOT_TOKEN"))
        .setActivity(Activity.competing("in the war against viktor!"))
        .addEventListeners(
            GetQuoteAutomated(),
            SetAutomatedQuote(),
        )
        .build();
    Instance.bot = bot;
}

fun setupEnv(){
    env = Dotenv.configure()
        .directory("")
        .filename("env")
        .load();
    Instance.env = env;
}

fun registerCommands(){
    bot!!.updateCommands().addCommands(
        GetQuoteAutomated().getCommand(),
        SetAutomatedQuote().getCommand()
    ).queue();
}

class Instance {
    companion object {
        var bot: JDA? = null
        var env: Dotenv? = null
        var logger: Logger = Logger.getLogger("Instance-Bot")
    }
}
