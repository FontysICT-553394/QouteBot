package com.beauver.discord.bots.Commands

import com.beauver.discord.bots.Classes.DiscordUser
import com.beauver.discord.bots.Classes.Quote
import com.beauver.discord.bots.Database.Database
import com.beauver.discord.bots.Enums.QuoteType
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
import java.time.Instant
import java.time.LocalDate
import java.util.Calendar
import java.util.Date


class SetAutomatedQuote : ListenerAdapter() {

    private val allowedUsers = listOf(553649764177149972L,701896705049755659L)

    fun getCommand(): CommandData{
        val optionData = OptionData(OptionType.STRING, "quote", "Type of quote you want to modify", true)
            .addChoice("Quote of the day", "DAY")
            .addChoice("Quote of the month", "MONTH")
            .addChoice("Quote of the year", "YEAR")

        return Commands.slash("set-quote", "Sets an automated quote system to the new quote")
            .setIntegrationTypes(IntegrationType.ALL)
            .setContexts(InteractionContextType.ALL)
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)

            .addOptions(optionData)
    }

    fun getMessageContextCommand(): CommandData {
        return Commands.message("Make quote of the XXXX")
            .setIntegrationTypes(IntegrationType.ALL)
            .setContexts(InteractionContextType.ALL)
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if(event.name != "set-quote") return

        if(!allowedUsers.contains(event.user.idLong)){
            event.reply("You are not permitted to set this message as a quote.").setEphemeral(true).queue()
            return
        }

        val quoteType = event.getOption("quote")!!.asString
        event.replyModal(getModal("<<Quote Here>>", quoteType.uppercase())).queue()
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        if(event.name != "Make quote of the XXXX") return

        if(!allowedUsers.contains(event.user.idLong)){
            event.reply("You are not permitted to set this message as a quote.").setEphemeral(true).queue()
            return
        }

        event.replyModal(getModal(event.target.contentRaw)).queue()
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

        //broken on purpose for test
        val quoteType = event.getValue("quote-type").asString
        val validQuoteTypes = listOf("DAY", "MONTH", "YEAR")
        if (!validQuoteTypes.contains(quoteType)) {
            val embed = EmbedBuilder()
            embed.setTitle("422 Unprocessable Entity")
            embed.setDescription("Please check if your quoteType is spelled correctly")
            embed.setColor(Color.RED)
            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            return
        }
        val quoteText = event.getValue("quote")!!.asString

        try{
            val oldQuote = Database.currentQotd
            val newQuote = Quote(
                quoteText,
                Date.from(Instant.now()),
                QuoteType.valueOf(quoteType)
            )

            if(LocalDate.from(oldQuote.dateSaid!!.toInstant()).dayOfYear == LocalDate.from(newQuote.dateSaid!!.toInstant()).dayOfYear) {
                try{
                    Database.updateAutomaticQuoteDatabase(newQuote, oldQuote)
                    println("Updated quote of the $quoteType")

                    val embed = EmbedBuilder()
                    embed.setTitle("200 OK")
                    embed.setDescription("Successfully uploaded the new quote")
                    embed.setColor(Color.GREEN)
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                }catch(e:Exception){
                    val embed = EmbedBuilder()
                    embed.setTitle("500 Server Error")
                    embed.setDescription("Could not upload quote.")
                    embed.setColor(Color.RED)
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                }
            }else{
                try{
                    Database.addAutomaticQuoteDatabase(newQuote)
                    println("Added quote of the $quoteType")

                    val embed = EmbedBuilder()
                    embed.setTitle("200 OK")
                    embed.setDescription("Successfully uploaded the new quote")
                    embed.setColor(Color.GREEN)
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                }catch (e: Exception){
                    val embed = EmbedBuilder()
                    embed.setTitle("500 Server Error")
                    embed.setDescription("Could not upload quote.")
                    embed.setColor(Color.RED)
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                }
            }
        }catch (e: Exception){
            //if it can't get the quote
            val newQuote = Quote(
                quoteText,
                Date.from(Instant.now()),
                QuoteType.valueOf(quoteType)
            )

            try{
                Database.addAutomaticQuoteDatabase(newQuote)
                println("Added quote of the $quoteType")

                val embed = EmbedBuilder()
                embed.setTitle("200 OK")
                embed.setDescription("Successfully uploaded the new quote")
                embed.setColor(Color.GREEN)
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }catch (e: Exception){
                val embed = EmbedBuilder()
                embed.setTitle("500 Server Error")
                embed.setDescription("Could not upload quote.")
                embed.setColor(Color.RED)
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
        }
    }

    private fun getModal(quoteText: String, quoteType: String = "DAY"): Modal {
        val subject = TextInput.create("quote-type", "Quote Type", TextInputStyle.SHORT)
            .setPlaceholder("Quote type, such as: DAY, MONTH, YEAR")
            .setValue(quoteType)
            .setMinLength(0)
            .setMaxLength(8)
            .build()

        val body = TextInput.create("quote", "The Quote", TextInputStyle.PARAGRAPH)
            .setPlaceholder("Your quote goes here")
            .setValue(quoteText)
            .setMinLength(0)
            .setMaxLength(1000)
            .build()

        return Modal.create("quote-submission", "Quote Submission")
            .addComponents(ActionRow.of(subject), ActionRow.of(body))
            .build()
    }
}