package dev.emortal.nbstom

import net.minestom.server.entity.Player
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.extensions.Extension

class NBStomExtension : Extension() {
    override fun initialize() {
        eventNode.addListener(RemoveEntityFromInstanceEvent::class.java) {
            val player = it.entity as? Player ?: return@addListener
            NBS.stopPlaying(player)
        }

        logger.info("[${origin.name}] Initialized!")
    }

    override fun terminate() {
        logger.info("[${origin.name}] Terminated!")
    }
}