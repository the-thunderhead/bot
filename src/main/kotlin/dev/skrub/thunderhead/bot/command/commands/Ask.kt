package dev.skrub.thunderhead.bot.command.commands

import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.MessageCreate
import dev.skrub.thunderhead.bot.command.Command
import dev.skrub.thunderhead.bot.command.Type
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.utils.FileUpload
import redempt.crunch.Crunch
import java.awt.Color
import java.awt.Font
import java.io.InputStream
import java.net.URL
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.round

class Ask : Command(
    name = "ask",
    description = "Ask me a question, and I will answer with the truth.",
    restrictions = Pair(false, DefaultMemberPermissions.ENABLED),
    type = Type.NORMAL
) {
    override fun execute(event: GenericCommandInteractionEvent) {
        val question = event.getOption<String>("question")!!
        try {
            val bufferedImage: BufferedImage = BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB)
            val graphics2D: Graphics2D = bufferedImage.createGraphics()
            val answer = (round(
                Crunch.compileExpression(question.replace("x", "*").replace("÷", "/").replace("`", ""))
                    .evaluate() * 1000
            ) / 1000).toString().replace(Regex("\\.0\$"), "")

            graphics2D.drawImage(
                ImageIO.read(this::class.java.classLoader.getResourceAsStream("ask_background.png")),
                0,
                0,
                null
            )
            graphics2D.font = Font.createFont(
                Font.TRUETYPE_FONT,
                this::class.java.classLoader.getResourceAsStream("Overpass-SemiBold.ttf")
            ).deriveFont(80f)
            graphics2D.color = Color(243, 240, 205)
            graphics2D.drawString(answer, 300 - (graphics2D.fontMetrics.stringWidth(answer) / 2), 300)
            graphics2D.dispose()

            val outputStream = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "jpg", outputStream)

            event.reply(MessageCreate {
                content += "Expression: `$question`"
                files += FileUpload.fromData(ByteArrayInputStream(outputStream.toByteArray()), "answer.jpg")
            }).queue()

        } catch (e: Exception) {
            val answer = if (question.contains("scythe")) "warning" else listOf(
                "yes-986",
                "yes-never",
                "yes-truly",
                "yes-would",
                "yes-safely",
                "yes-analysis",
                "yes-yes",
                "no-some",
                "no-no",
                "no-better",
                "no-inquiry",
                "no-reconsider",
                "no-although",
                "no-history-2",
                "maybe-care",
                "maybe-definite",
                "maybe-exact",
                "maybe-balanced",
                "maybe-best",
                "maybe-maybe",
                "maybe-427",
                "non-offend",
                "non-apologies",
                "non-hmm",
                "non-quaint",
                "non-nevermind",
                "non-scythes",
                "non-difficult",
                "non-especially",
                "non-belies"
            ).random()
            event.reply(MessageCreate {
                content += "You asked: *${question.replace("*", "\\*")}*"
                files += FileUpload.fromData(
                    URL("https://askthethunderhead.com/sharing/$answer.jpg").openStream() as InputStream,
                    "answer.jpg"
                )
            }).queue()
        }
    }

    override fun addOptions(slashCommandData: SlashCommandData) {
        slashCommandData.option<String>("question", "The question (or math expression) you wish to me answer.", true)
    }
}