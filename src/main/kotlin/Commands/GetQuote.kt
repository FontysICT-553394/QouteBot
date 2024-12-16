package com.beauver.discord.bots.Commands

import com.beauver.discord.bots.Classes.DiscordUser
import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Database.Database
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.IntegrationType
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.awt.Color

class GetQuote : ListenerAdapter() {

    fun getCommand(): CommandData{
        val options = OptionData(OptionType.STRING , "quote", "The type of quote you want to get", true)
            .addChoice("Random Quote", "random")
            .addChoice("All Quotes", "all")
            .addChoice("Quote ID", "id")
            .addChoice("Matching String", "matching")

        return Commands.slash("quote-user", "Gets the specified quote")
            .setIntegrationTypes(IntegrationType.ALL)
            .setContexts(InteractionContextType.ALL)
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            .addOptions(options)
            .addOption(OptionType.USER, "target", "person you want to get their quotes from.", true)
            .addOption(OptionType.STRING, "value", "Value if you want to get a quote via ID or matching string. Leave empty else wise.")
            .addOption(OptionType.BOOLEAN, "ephemeral", "Whether you want your message to be sent only to you or everyone.")
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if(event.name != "quote-user") {
            return
        }

        var ephemeral = true
        if (event.getOption("ephemeral") != null) {
            ephemeral = event.getOption("ephemeral")!!.asBoolean
        }

        when(event.getOption("quote")!!.asString){
            "random" -> randomQuote(event, ephemeral)
            "all" -> allQuotes(event, ephemeral)
            "id" -> idQuote(event, ephemeral)
            "matching" -> matchingQuote(event, ephemeral)
        }
    }

    private fun randomQuote(event: SlashCommandInteractionEvent, ephemeral: Boolean) {
        val user: User = event.getOption("target")!!.asUser
        val quotes = Database.getUserQuotes(DiscordUser(user))

        try{
            event.replyEmbeds(quotes.random().toEmbed(event.user).build()).setEphemeral(ephemeral).queue()
        }catch(e: Exception){
            val embed = EmbedBuilder()
            embed.setTitle("404 Quote Not Found")
            embed.setDescription("No random quote could be found.")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
        }
    }

    private fun idQuote(event: SlashCommandInteractionEvent, ephemeral: Boolean){
        val option = event.getOption("value")?.asString

        if(option == null){
            val embed = EmbedBuilder()
            embed.setTitle("422 Unprocessable Entity")
            embed.setDescription("Please fill out the 'value' field for this option")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            return
        }

        try{
            option.toInt()
        }catch(e: Exception){
            val embed = EmbedBuilder()
            embed.setTitle("422 Unprocessable Entity")
            embed.setDescription("Please put a number in the 'value' field")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            return
        }

        try{
            event.replyEmbeds(Database.getQuoteById(option.toInt()).toEmbed(event.user).build()).setEphemeral(ephemeral).queue()
        }catch(e: Exception){
            val embed = EmbedBuilder()
            embed.setTitle("404 Quote Not Found")
            embed.setDescription("No quote with ID: $option could be found.")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).setEphemeral(ephemeral).queue()
        }
    }

    private fun matchingQuote(event: SlashCommandInteractionEvent, ephemeral: Boolean){
        val option = event.getOption("value")?.asString

        if(option == null){
            val embed = EmbedBuilder()
            embed.setTitle("422 Unprocessable Entity")
            embed.setDescription("Please fill out the 'value' field for this option")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            return
        }

        try{
            event.replyEmbeds(Database.getQuoteByString(option).toEmbed(event.user).build()).setEphemeral(ephemeral).queue()
        }catch(e: Exception){
            val embed = EmbedBuilder()
            embed.setTitle("404 Quote Not Found")
            embed.setDescription("No quote with value: $option could be found.")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
        }
    }

    private fun allQuotes(event: SlashCommandInteractionEvent, ephemeral: Boolean){
        val user: User = event.getOption("target")!!.asUser
        val quotes = Database.getUserQuotes(DiscordUser(user))

        val embed = EmbedBuilder()
        embed.setTitle("${user.effectiveName}'s quotes:")
        embed.setDescription("This user has ${quotes.count()} quotes.")
        embed.setColor(Color.GREEN)
        embed.setFooter(event.user.effectiveName, event.user.effectiveAvatarUrl)

        var counter = 1;
        quotes.forEach { quote ->
            embed.addField("Quote $counter:", quote.quote!!,true)
            counter++;
        }
        event.replyEmbeds(embed.build()).setEphemeral(ephemeral).queue()
        // implement page stuff later
    }
}