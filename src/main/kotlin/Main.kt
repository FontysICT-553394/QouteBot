package com.beauver.discord.bots

import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Commands.AddQuote
import com.beauver.discord.bots.Commands.GetQuote
import com.beauver.discord.bots.Commands.GetQuoteAutomated
import com.beauver.discord.bots.Commands.SetAutomatedQuote
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import java.util.logging.Logger

var bot: JDA? = null
var env: Dotenv? = null
var logger: Logger = Logger.getLogger("Quote-Discord-Bot")

fun main() {
    setupEnv()
    startBot()
    registerCommands()
    registerTestCmds()
}

fun startBot(){
    bot = JDABuilder.createDefault(env!!.get("BOT_TOKEN"))
        .setActivity(Activity.playing("all your quotes"))
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
        .addEventListeners(
            GetQuoteAutomated(),
            GetQuote(),
            AddQuote(),
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

        GetQuote().getCommand(),
        GetQuote().getUserCommand(),
        GetQuote().getUserCommandEphemeral(),

        AddQuote().getCommand(),
        AddQuote().getMessageContextCommand(),

        SetAutomatedQuote().getCommand(),
        SetAutomatedQuote().getMessageContextCommand()
    ).queue(
        { println("Commands registered successfully!") },
        { error -> println("Command registration failed: ${error.message}") }
    )
}

fun registerTestCmds(){
    bot!!.getGuildById("1309597186425622600")?.updateCommands()?.addCommands(
        GetQuoteAutomated().getCommand(),

        GetQuote().getCommand(),
        GetQuote().getUserCommand(),

        AddQuote().getCommand(),
        AddQuote().getMessageContextCommand(),

        SetAutomatedQuote().getCommand(),
        SetAutomatedQuote().getMessageContextCommand()
    )?.queue()
}

class Instance {
    companion object {
        var bot: JDA? = null
        var env: Dotenv? = null
        var logger: Logger = Logger.getLogger("Quote-Discord-Bot")
    }
}
