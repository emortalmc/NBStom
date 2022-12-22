package dev.emortal.nbstom.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import net.minestom.server.tag.Tag

object LoopCommand : Command("loop") {

    val loopTag = Tag.Boolean("loopSong")

    init {

        setDefaultExecutor { sender, context ->
            val player = sender as? Player ?: return@setDefaultExecutor

            if (player.hasTag(loopTag)) {
                player.removeTag(loopTag)
                player.sendMessage(Component.text("Current song will no longer loop!", NamedTextColor.GREEN))
            } else {
                player.setTag(loopTag, true)
                player.sendMessage(Component.text("Current song will now loop!", NamedTextColor.GREEN))
            }
        }

    }

}