package dev.emortal.nbstom.commands;

import dev.emortal.nbstom.MusicDisc;
import dev.emortal.nbstom.NBS;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MusicCommand extends Command {

    public MusicCommand() {
        super("music");

        // If no arguments given, open inventory
        setDefaultExecutor((sender, ctx) -> {
            if (!(sender instanceof Player player)) return;

            player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 2f));
            player.openInventory(MusicPlayerInventory.getInventory());
        });

        Argument<String> stopArgument = new ArgumentLiteral("stop");
        Argument<String[]> discArgument = new ArgumentStringArray("disc").setSuggestionCallback((sender, context, suggestion) -> {
            for (MusicDisc disc : MusicDisc.values()) {
                suggestion.addEntry(new SuggestionEntry(disc.getShortName()));
            }
        });

        addSyntax((sender, ctx) -> {
            stop(sender);
        }, stopArgument);

        addSyntax((sender, ctx) -> {
            playDisc(sender, String.join(" ", ctx.get(discArgument)));
        }, discArgument);
    }


    public static void playDisc(CommandSender sender, String disc) {
        if (!(sender instanceof Player player)) return;

        List<MusicDisc> discValues = Arrays.asList(MusicDisc.values());

        stop(sender);

        MusicDisc nowPlayingDisc = MusicDisc.valueOf("MUSIC_DISC_" + disc.toUpperCase());

        String discName = nowPlayingDisc.getDescription();

        player.setTag(playingDiscTag, discValues.indexOf(nowPlayingDisc));
        player.playSound(Sound.sound(nowPlayingDisc.getSound(), Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());

        stopPlayingTaskMap.put(player.getUuid(), player.scheduler().buildTask(() -> {
            if (player.hasTag(LoopCommand.loopTag)) {
                playDisc(sender, nowPlayingDisc.getShortName());
            } else {
                stop(sender);
            }

        }).delay(Duration.ofSeconds(nowPlayingDisc.getLength())).schedule());

        player.sendActionBar(
                Component.text()
                        .append(Component.text("Playing: ", NamedTextColor.GRAY))
                        .append(Component.text(discName, NamedTextColor.AQUA))
        );
    }
    public static void stop(CommandSender sender) {
        if (!(sender instanceof Player player)) return;

        MusicDisc[] discValues = MusicDisc.values();

        Integer playingDisk = player.getTag(playingDiscTag);
        if (playingDisk == null) return;
        MusicDisc playingDisc = discValues[playingDisk];

        player.stopSound(SoundStop.named(playingDisc.getSound()));
        player.removeTag(playingDiscTag);

        Task stopPlayingTask = stopPlayingTaskMap.get(player.getUuid());
        if (stopPlayingTask != null) {
            stopPlayingTask.cancel();
            stopPlayingTaskMap.remove(player.getUuid());
        }

        NBS.stop(player);
    }

    private static final Map<UUID, Task> stopPlayingTaskMap = new ConcurrentHashMap<>();
    private static final Tag<Integer> playingDiscTag = Tag.Integer("playingDisc");

}