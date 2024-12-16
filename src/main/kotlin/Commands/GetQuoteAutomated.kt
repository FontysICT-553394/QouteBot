package com.beauver.discord.bots.Commands

import com.beauver.discord.bots.Database.Database
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.IntegrationType
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class GetQuoteAutomated : ListenerAdapter() {

    fun getCommand(): CommandData{
        val optionData = OptionData(OptionType.STRING, "quote", "Type of quote you want to get", true)
            .addChoice("Quote of the day", "qotd")
            .addChoice("Quote of the week", "qotw")
            .addChoice("Quote of the month", "qotm")
            .addChoice("Quote of the year", "qoty")

        return Commands.slash("quote", "Gets the specified quote!")
            .setIntegrationTypes(IntegrationType.ALL)
            .setContexts(InteractionContextType.ALL)
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)

            .addOptions(optionData)
            .addOption(OptionType.BOOLEAN, "ephemeral", "Whether you want your message to be sent only to you or everyone.")
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if(event.name != "quote") return

        var ephemeral = true

        if (event.getOption("ephemeral") != null) {
            ephemeral = event.getOption("ephemeral")!!.asBoolean
        }

        when (event.getOption("quote")!!.asString) {
            "qotd" -> qotd(ephemeral, event)
            "qotw" -> qotw(ephemeral, event)
            "qotm" -> qotm(ephemeral, event)
            "qoty" -> qoty(ephemeral, event)
            else -> event.reply("Invalid quote type selected").setEphemeral(true).queue()
        }
    }

    fun qotd(ephemeral:Boolean, event: SlashCommandInteractionEvent) {
        var embed = Database.currentQotd!!.toEmbed(event.user)
        event.replyEmbeds(embed.build()).setEphemeral(ephemeral).queue()
    }

    fun qotw(ephemeral:Boolean, event: SlashCommandInteractionEvent) {
        var embed = Database.currentQotw!!.toEmbed(event.user)
        event.replyEmbeds(embed.build()).setEphemeral(ephemeral).queue()
    }

    fun qotm(ephemeral:Boolean, event: SlashCommandInteractionEvent) {
        var embed = Database.currentQotm!!.toEmbed(event.user)
        event.replyEmbeds(embed.build()).setEphemeral(ephemeral).queue()
    }

    fun qoty(ephemeral:Boolean, event: SlashCommandInteractionEvent) {
        var embed = Database.currentQoty!!.toEmbed(event.user)
        event.replyEmbeds(embed.build()).setEphemeral(ephemeral).queue()
    }

}