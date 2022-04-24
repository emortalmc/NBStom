package dev.emortal.nbstom

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.PlayerEvent

class SongEndEvent(private val plr: Player, val nbs: NBS) : PlayerEvent {
    override fun getPlayer(): Player = plr
}