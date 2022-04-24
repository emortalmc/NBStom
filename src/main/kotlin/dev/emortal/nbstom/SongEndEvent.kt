package dev.emortal.nbstom

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.PlayerEvent

class SongEndEvent(val plr: Player) : PlayerEvent {
    override fun getPlayer(): Player = plr
}