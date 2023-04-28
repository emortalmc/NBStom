package dev.emortal.nbstom;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class NBS {

    public static final Map<UUID, Task> playingTaskMap = new ConcurrentHashMap<>();

    /**
     * Plays this NBS song to an audience
     * Can be cancelled by using {@link #stop(UUID)} with the player's UUID. This stops automatically when the player leaves.
     *
     * @param player The player to play the song to
     */
    public static void play(NBSSong song, Player player) {
        play(song, player, player.scheduler(), player.getUuid());
    }

    /**
     * Plays this NBS song to an audience
     * Can be cancelled by using {@link #stop(UUID)} with the same stopId or by cancelling via the scheduler
     *
     * @param audience The audience to play the song to
     * @param scheduler The scheduler to tick the song on
     * @param stopId The id for use with {@link #stop(UUID)} later
     */
    public static void play(NBSSong song, Audience audience, Scheduler scheduler, UUID stopId) {
        playingTaskMap.put(stopId, scheduler.submitTask(new Supplier<>() {
            int tick = 0;

            @Override
            public TaskSchedule get() {
                if (tick > song.getLength() + 1) {
                    return TaskSchedule.stop();
                }

                List<Sound> sounds = song.getTicks().get(tick);
                if (sounds != null) {
                    for (Sound sound : sounds) {
                        audience.playSound(sound, Sound.Emitter.self());
                    }
                }

                tick++;

                return TaskSchedule.millis((long) (1000.0 / song.getTps()));
            }
        }));
    }

    public static void stop(Player player) {
        stop(player.getUuid());
    }
    public static void stop(UUID stopId) {
        Task task = playingTaskMap.get(stopId);
        if (task != null) {
            task.cancel();
        }
        playingTaskMap.remove(stopId);
    }

}
