package com.beauver.discord.bots.Commands

import com.beauver.discord.bots.Classes.DiscordUser
import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Database.Database
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.IntegrationType
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import java.awt.Color
import java.util.Date

class AddQuote : ListenerAdapter() {

    fun getCommand(): CommandData {
        return Commands.slash("add-quote", "Adds a quote for a user")
            .setIntegrationTypes(IntegrationType.ALL)
            .setContexts(InteractionContextType.ALL)
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)

            .addOption(OptionType.USER, "target", "Person who you'd like to quote", true)
            .addOption(OptionType.STRING, "quote", "The quote you want to add", true)
    }

    fun getMessageContextCommand(): CommandData {
        return Commands.message("Add Quote")
            .setIntegrationTypes(IntegrationType.ALL)
            .setContexts(InteractionContextType.ALL)
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if(event.name != "add-quote") return

        val user = event.getOption("target")!!.asUser
        val quoteText = event.getOption("quote")!!.asString

        val quote: Quote = Quote(
            quoteText,
            DiscordUser(user),
            event.guild?.id ?: "DM",
            Date()
        )

        val embed = addQuote(quote)
        event.replyEmbeds(embed.build()).setEphemeral(true).setEphemeral(true).queue()
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        if(event.name != "Add Quote") return

        val user = event.target.author
        val quoteText = event.target.contentRaw

        val quote: Quote = Quote(
            quoteText,
            DiscordUser(user),
            event.guild?.id ?: "DM",
            Date()
        )

        val embed = addQuote(quote)
        event.replyEmbeds(embed.build()).setEphemeral(true).setEphemeral(true).queue()
    }

    private fun addQuote(quote: Quote): EmbedBuilder {
        try{
            Database.addUserQuote(quote)
            val embed = EmbedBuilder()
            embed.setTitle("200 OK")
            embed.setDescription("Quote has been successfully added!")
            embed.setColor(Color.GREEN)
            return embed
        }catch(e:Exception){
            val embed = EmbedBuilder()
            embed.setTitle("500 Server Error")
            embed.setDescription("Could not upload quote.")
            embed.setColor(Color.RED)
            return embed
        }
    }

}