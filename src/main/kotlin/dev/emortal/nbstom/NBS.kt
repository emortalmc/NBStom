package dev.emortal.nbstom

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player
import net.minestom.server.particle.Particle
import net.minestom.server.particle.ParticleCreator
import net.minestom.server.utils.PacketUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import kotlin.io.path.readBytes

/**
 * Class to read and play .nbs files
 */
class NBS(path: Path) {

    val version: Byte
    val instrumentCount: Byte
    val length: Int
    val layerCount: Int
    val songName: String
    val author: String
    val originalAuthor: String
    val description: String
    val tps: Double
    val autoSaving: Boolean
    val autoSavingDuration: Byte
    val timeSignature: Byte
    val minutesSpent: Int
    val leftClicks: Int
    val rightClicks: Int
    val noteBlocksAdded: Int
    val noteBlocksRemoved: Int
    val midiFileName: String
    val loop: Boolean
    val maxLoopCount: Byte
    val loopStartTick: Int

    val ticks: Array<NBSTick?>

    init {
        val bytes = path.readBytes()
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        buffer.unsignedShort
        version = buffer.get()
        instrumentCount = buffer.get()
        length = buffer.unsignedShort
        layerCount = buffer.unsignedShort
        songName = buffer.getNBSString()
        author = buffer.getNBSString()
        originalAuthor = buffer.getNBSString()
        description = buffer.getNBSString()
        tps = buffer.unsignedShort / 100.0
        autoSaving = buffer.get().toInt() == 1
        autoSavingDuration = buffer.get()
        timeSignature = buffer.get()
        minutesSpent = buffer.int
        leftClicks = buffer.int
        rightClicks = buffer.int
        noteBlocksAdded = buffer.int
        noteBlocksRemoved = buffer.int
        midiFileName = buffer.getNBSString()
        loop = buffer.get().toInt() == 1
        maxLoopCount = buffer.get()
        loopStartTick = buffer.unsignedShort

        ticks = readNotes(buffer)
    }

    private fun readNotes(buffer: ByteBuffer): Array<NBSTick?> {
        var currentTick = -1
        val ticks = Array<NBSTick?>(length + 1) { null }

        while (true) {
            val tickJumps = buffer.unsignedShort
            if (tickJumps == 0) break
            currentTick += tickJumps

            val notes = readTickNoteLayers(buffer)
            ticks[currentTick] = NBSTick(currentTick, notes)
        }

        return ticks
    }

    private fun readTickNoteLayers(buffer: ByteBuffer): List<NBSNote> {
        val notes = mutableListOf<NBSNote>()
        var currentLayer = -1

        while (true) {
            val layerJumps = buffer.unsignedShort
            if (layerJumps == 0) break
            currentLayer += layerJumps

            notes.add(NBSNote.readNote(buffer))
        }

        return notes
    }


    companion object {
        private val playingTasks = ConcurrentHashMap<Player, MinestomRunnable>()

        private val scope = CoroutineScope(Dispatchers.IO)

        /**
         * Stops playing the song to a player
         * @param player The player to stop playing to
         */
        fun stopPlaying(player: Player) {
            playingTasks[player]?.cancel()
            playingTasks.remove(player)
        }

        /**
         * Play the song to a player
         * @param song The song to play
         * @param player The player to play the song to
         */
        fun play(song: NBS, player: Player) {
            val task = object : MinestomRunnable(repeat = Duration.ofMillis((1000.0 / song.tps).toLong()), iterations = song.length, coroutineScope = scope) {
                override suspend fun run() {
                    val nbstick = song.ticks[currentIteration.get() - 1]
                    nbstick?.notes?.forEach {
                        val sound = NBSNote.toSound(it)
                        player.playSound(sound, Sound.Emitter.self())
                    }
                }

            }

            playingTasks[player] = task
        }

        /**
         * Play the song to a player
         * @param song The song to play
         * @param player The player to play the song to
         */
        fun playWithParticles(song: NBS, player: Player, viewersToo: Boolean = true) {
            val task = object : MinestomRunnable(repeat = Duration.ofMillis((1000.0 / song.tps).toLong()), iterations = song.length, coroutineScope = scope) {
                val rand = ThreadLocalRandom.current()

                override suspend fun run() {
                    val nbstick = song.ticks[currentIteration.get() - 1]
                    nbstick?.notes?.forEach {
                        val sound = NBSNote.toSound(it)
                        player.playSound(sound, Sound.Emitter.self())

                        val useCount = it.key - 33
                        val particlePacket = ParticleCreator.createParticlePacket(Particle.NOTE, player.position.x + rand.nextDouble(-0.4, 0.4), player.position.y + 1.5 + rand.nextDouble(0.0, 0.2), player.position.z + rand.nextDouble(-0.4, 0.4), useCount / 24f, 0f, 0f, 1)

                        if (viewersToo) {
                            player.sendPacketToViewersAndSelf(particlePacket)
                        } else {
                            player.sendPacket(particlePacket)
                        }
                    }
                }

            }

            playingTasks[player] = task
        }
    }

}