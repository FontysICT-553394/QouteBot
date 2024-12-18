package com.beauver.discord.bots.Commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.IntegrationType
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import java.awt.Color


class SetAutomatedQuote : ListenerAdapter() {

    private val allowedUsers = listOf(553649764177149972L,701896705049755659L)

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

    fun getMessageContextCommand(): CommandData {
        return Commands.message("Make quote of the XXXX")
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if(event.name != "set-quote") return

        if(!allowedUsers.contains(event.user.idLong)){
            event.reply("You are not permitted to set this message as a quote.").setEphemeral(true).queue()
            return
        }
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        if(event.name != "Make quote of the XXXX") return

        if(!allowedUsers.contains(event.user.idLong)){
            event.reply("You are not permitted to set this message as a quote.").setEphemeral(true).queue()
            return
        }

        val subject = TextInput.create("quote-type", "Quote Type", TextInputStyle.SHORT)
            .setPlaceholder("Quote type, such as: DAY, MONTH, YEAR")
            .setValue("DAY")
            .setMinLength(4)
            .setMaxLength(4)
            .build()

        val body = TextInput.create("quote", "The Quote", TextInputStyle.PARAGRAPH)
            .setPlaceholder("Your quote goes here")
            .setValue(event.target.contentRaw)
            .setMinLength(0)
            .setMaxLength(1000)
            .build()

        val modal = Modal.create("quote-submission", "Quote Submission")
            .addComponents(ActionRow.of(subject), ActionRow.of(body))
            .build()

        event.replyModal(modal).queue()
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        if(event.modalId != "quote-submission") return

        if(event.getValue("quote-type") == null || event.getValue("quote") == null) {
            val embed = EmbedBuilder()
            embed.setTitle("422 Unprocessable Entity")
            embed.setDescription("Please fill out the all text fields.")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            return
        }

        val quoteType = event.getValue("quote-type")!!.asString
        if(quoteType != "DAY" || quoteType != "MONTH" || quoteType != "YEAR"){
            val embed = EmbedBuilder()
            embed.setTitle("422 Unprocessable Entity")
            embed.setDescription("Please check if your quoteType is spelled correctly")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            return
        }

        event.reply("Not yet implemented. :D").setEphemeral(true).queue()
    }
}