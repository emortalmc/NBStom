package dev.emortal.nbstom

import net.kyori.adventure.sound.Sound
import net.minestom.server.sound.SoundEvent
import java.nio.ByteBuffer
import kotlin.math.pow

class NBSNote(/*tick: Int, */val instrument: Byte, val key: Byte, val volume: Byte, val pan: Byte, val pitch: Short) {
    companion object {

        val soundEvents = arrayOf(
            SoundEvent.BLOCK_NOTE_BLOCK_HARP,
            SoundEvent.BLOCK_NOTE_BLOCK_BASS,
            SoundEvent.BLOCK_NOTE_BLOCK_BASEDRUM,
            SoundEvent.BLOCK_NOTE_BLOCK_SNARE,
            SoundEvent.BLOCK_NOTE_BLOCK_HAT,
            SoundEvent.BLOCK_NOTE_BLOCK_GUITAR,
            SoundEvent.BLOCK_NOTE_BLOCK_FLUTE,
            SoundEvent.BLOCK_NOTE_BLOCK_BELL,
            SoundEvent.BLOCK_NOTE_BLOCK_CHIME,
            SoundEvent.BLOCK_NOTE_BLOCK_XYLOPHONE,
            SoundEvent.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,
            SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL,
            SoundEvent.BLOCK_NOTE_BLOCK_DIDGERIDOO,
            SoundEvent.BLOCK_NOTE_BLOCK_BIT,
            SoundEvent.BLOCK_NOTE_BLOCK_BANJO,
            SoundEvent.BLOCK_NOTE_BLOCK_PLING
        )

        fun readNote(buffer: ByteBuffer) =
            NBSNote(
                //buffer.int,
                buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.get(),
                buffer.short
            )

        fun toSound(note: NBSNote) =
            Sound.sound(
                soundEvents[note.instrument.toInt().coerceIn(0, soundEvents.size - 1)],
                Sound.Source.RECORD,
                note.volume / 100f,
                2f.pow((note.key - 45) / 12f)
            )

    }
}