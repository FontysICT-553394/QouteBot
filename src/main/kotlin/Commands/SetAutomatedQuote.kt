package com.beauver.discord.bots.Commands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.IntegrationType
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class SetAutomatedQuote : ListenerAdapter() {

    fun getCommand(): CommandData{
        val optionData = OptionData(OptionType.STRING, "quote", "Type of quote you want to modify", true)
            .addChoice("Quote of the day", "qotd")
            .addChoice("Quote of the month", "qotm")
            .addChoice("Quote of the year", "qoty")

        return Commands.slash("set-quote", "Sets an automated quote system to the new quote")
            .setIntegrationTypes(IntegrationType.ALL)
            .setContexts(InteractionContextType.ALL)
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)

            .addOptions(optionData)
            .addOption(OptionType.BOOLEAN, "ephemeral", "Whether you want your message to be sent only to you or everyone.")
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if(event.name != "set-quote") return
        event.reply("Skill issue (not yet implemented)").setEphemeral(true).queue()
    }

}