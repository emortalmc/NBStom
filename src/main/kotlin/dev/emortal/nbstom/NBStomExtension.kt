package dev.emortal.nbstom

import net.minestom.server.extensions.Extension

class NBStomExtension : Extension() {
    override fun initialize() {
        logger.info("[${origin.name}] Initialized!")
    }

    override fun terminate() {
        logger.info("[${origin.name}] Terminated!")
    }
}