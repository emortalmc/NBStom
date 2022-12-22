package dev.emortal.nbstom.commands

import dev.emortal.nbstom.MusicDisc
import dev.emortal.nbstom.MusicPlayerInventory
import dev.emortal.nbstom.NBS
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentLiteral
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.Player
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import net.minestom.server.timer.Task
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import kotlin.io.path.nameWithoutExtension
import kotlin.math.roundToLong

object MusicCommand : Command("music") {

    private val stopPlayingTaskMap = ConcurrentHashMap<UUID, Task>()
    private val playingDiscTag = Tag.Integer("playingDisc")

    private var nbsSongs = ConcurrentHashMap<String, NBS>()
    private var suggestions = ConcurrentHashMap.newKeySet<String>()

    fun refreshSongs() {
        try {
            nbsSongs.clear()
            suggestions.clear()

            Files.list(Path.of("./nbs/")).collect(Collectors.toUnmodifiableList()).forEach {
                nbsSongs[it.nameWithoutExtension] = NBS(it)
            }

            suggestions.addAll(MusicDisc.values().map { it.shortName } + nbsSongs.keys().toList())
        } catch (e: Exception) {
            e.printStackTrace()
            nbsSongs.clear()
        }
    }

    init {
        refreshSongs()

        // If no arguments given, open inventory
        setDefaultExecutor { sender, _ ->
            val player = sender as? Player ?: return@setDefaultExecutor

            player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 2f))
            player.openInventory(MusicPlayerInventory.inventory)
        }

        val refreshArgument = ArgumentLiteral("refresh")
        val stopArgument = ArgumentLiteral("stop")
        val discArgument = ArgumentType.StringArray("disc").setSuggestionCallback { _, context, suggestion ->
            suggestions.forEach {
                suggestion.addEntry(SuggestionEntry(it))
            }
        }


        addSyntax({ sender, _ ->
            val player = sender as? Player ?: return@addSyntax

            val discValues = MusicDisc.values()
            val playingDisc = player.getTag(playingDiscTag)?.let { discValues[it] }

            playingDisc?.sound?.let {
                player.stopSound(SoundStop.named(it))
                player.removeTag(playingDiscTag)
            }

            stopPlayingTaskMap[player.uuid]?.cancel()
            stopPlayingTaskMap.remove(player.uuid)
            NBS.stopPlaying(player)
        }, stopArgument)

        addSyntax({ sender, context ->
            val player = sender as? Player ?: return@addSyntax

            val disc = context.get(discArgument).joinToString(separator = " ")
            val discValues = MusicDisc.values()
            val playingDisc = player.getTag(playingDiscTag)?.let { discValues[it] }

            playingDisc?.sound?.let {
                player.stopSound(SoundStop.named(it))
                player.removeTag(playingDiscTag)
            }

            stopPlayingTaskMap[player.uuid]?.cancel()
            stopPlayingTaskMap.remove(player.uuid)
            NBS.stopPlaying(player)

            var discName: String
            try {
                val nowPlayingDisc = MusicDisc.valueOf("MUSIC_DISC_${disc.uppercase()}")

                discName = nowPlayingDisc.description

                player.setTag(playingDiscTag, discValues.indexOf(nowPlayingDisc))
                player.playSound(Sound.sound(nowPlayingDisc.sound, Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self())

                stopPlayingTaskMap[player.uuid] = player.scheduler().buildTask {

                    if (player.hasTag(LoopCommand.loopTag)) {
                        MinecraftServer.getCommandManager().execute(sender, "music ${nowPlayingDisc.shortName}")
                    } else {
                        MinecraftServer.getCommandManager().execute(sender, "music stop")
                    }

                }.delay(Duration.ofSeconds(nowPlayingDisc.length.toLong())).schedule()
            } catch (e: IllegalArgumentException) {
                if (!nbsSongs.containsKey(disc)) {
                    sender.sendMessage(Component.text("Invalid song", NamedTextColor.RED))
                    return@addSyntax
                }

                val nbs = nbsSongs[disc]!!
                NBS.playWithParticles(nbs, player)

                if (disc == "DJ Got Us Fallin' in Love") {
                    player.scheduler().buildTask {
                        player.sendMessage("Creeper?")
                    }.delay(Duration.ofMillis(3850)).schedule()
                    player.scheduler().buildTask {
                        player.sendMessage("Aww man")
                    }.delay(Duration.ofMillis(5900)).schedule()
                }

                stopPlayingTaskMap[player.uuid] = player.scheduler().buildTask {

                    if (player.hasTag(LoopCommand.loopTag)) {
                        MinecraftServer.getCommandManager().execute(sender, "music ${disc}")
                    } else {
                        MinecraftServer.getCommandManager().execute(sender, "music stop")
                    }

                }.delay(Duration.ofSeconds((nbs.length / nbs.tps).roundToLong())).schedule()

                discName = "${nbs.originalAuthor.ifEmpty { nbs.author }} - ${nbs.songName}"
            }

            player.sendActionBar(
                Component.text()
                    .append(Component.text("Playing: ", NamedTextColor.GRAY))
                    .append(Component.text(discName, NamedTextColor.AQUA))
            )
        }, discArgument)
        addSyntax({ _, _ ->
            refreshSongs()
        }, refreshArgument)

    }
}