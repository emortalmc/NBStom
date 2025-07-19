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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class NBS {

    private static final Map<UUID, NBSPlayer> playingSongs = new ConcurrentHashMap<>();

    private static class NBSPlayer {
        final NBSSong song;
        final Audience audience;
        final Scheduler scheduler;
        final UUID stopId;
        final CompletableFuture<Void> onFinishFuture;

        Task task;
        int tick = 0;
        int loops = 0;

        NBSPlayer(NBSSong song, Audience audience, Scheduler scheduler, UUID stopId, CompletableFuture<Void> onFinishFuture) {
            this.song = song;
            this.audience = audience;
            this.scheduler = scheduler;
            this.stopId = stopId;
            this.onFinishFuture = onFinishFuture;
        }

        void schedule() {
            if (this.task != null && this.task.isAlive()) {
                this.task.cancel();
            }

            this.task = this.scheduler.submitTask(() -> {
                if (tick > song.getLength()) {
                    if (song.isLoop() && (song.getMaxLoopCount() == 0 || loops < song.getMaxLoopCount())) {
                        this.loops++;
                        this.tick = song.getLoopStart();
                    } else {
                        playingSongs.remove(this.stopId);
                        this.onFinishFuture.complete(null);
                        return TaskSchedule.stop();
                    }
                }

                List<Sound> sounds = song.getTicks().get(tick);
                if (sounds != null) {
                    for (Sound sound : sounds) {
                        audience.playSound(sound, Sound.Emitter.self());
                    }
                }

                tick++;

                long delayInTicks = (long) (20.0 / song.getTps());

                if (delayInTicks < 1) {
                    delayInTicks = 1;
                }

                return TaskSchedule.tick((int)delayInTicks);
            });
        }

        void pause() {
            if (this.task != null) {
                this.task.cancel();
            }
        }

        void stop() {
            if (this.task != null) {
                this.task.cancel();
            }
            this.onFinishFuture.cancel(false);
        }
    }

    /**
     * Plays this NBS song to a player and returns a future that completes when the song is finished.
     *
     * @param player The player to play the song to
     * @return A {@link CompletableFuture} that completes when the song finishes.
     */
    public static CompletableFuture<Void> play(NBSSong song, Player player) {
        return play(song, player, player.scheduler(), player.getUuid());
    }

    /**
     * Plays this NBS song to an audience and returns a future that completes when the song is finished.
     *
     * @param audience The audience to play the song to
     * @param scheduler The scheduler to tick the song on
     * @param stopId The id for use with {@link #stop(UUID)} later
     * @return A {@link CompletableFuture} that completes when the song finishes.
     */
    public static CompletableFuture<Void> play(NBSSong song, Audience audience, Scheduler scheduler, UUID stopId) {
        stop(stopId);

        CompletableFuture<Void> future = new CompletableFuture<>();
        NBSPlayer nbsPlayer = new NBSPlayer(song, audience, scheduler, stopId, future);
        playingSongs.put(stopId, nbsPlayer);
        nbsPlayer.schedule();

        return future;
    }

    public static void stop(Player player) {
        stop(player.getUuid());
    }

    public static void stop(UUID stopId) {
        NBSPlayer nbsPlayer = playingSongs.remove(stopId);
        if (nbsPlayer != null) {
            nbsPlayer.stop();
        }
    }

    /**
     * Checks if a song is currently playing for the given ID.
     *
     * @param stopId The ID to check.
     * @return true if a song is playing, false otherwise.
     */
    public static boolean isPlaying(UUID stopId) {
        return playingSongs.containsKey(stopId);
    }

    /**
     * Pauses a song for a specific player.
     * The song can be resumed using {@link #resume(Player)}.
     *
     * @param player The player to pause the song for.
     */
    public static void pause(Player player) {
        pause(player.getUuid());
    }

    /**
     * Pauses a song for a specific stopId.
     * The song can be resumed using {@link #resume(UUID)}.
     *
     * @param stopId The id of the song to pause.
     */
    public static void pause(UUID stopId) {
        NBSPlayer nbsPlayer = playingSongs.get(stopId);
        if (nbsPlayer != null) {
            nbsPlayer.pause();
        }
    }

    /**
     * Resumes a paused song for a specific player.
     *
     * @param player The player to resume the song for.
     */
    public static void resume(Player player) {
        resume(player.getUuid());
    }

    /**
     * Resumes a paused song for a specific stopId.
     *
     * @param stopId The id of the song to resume.
     */
    public static void resume(UUID stopId) {
        NBSPlayer nbsPlayer = playingSongs.get(stopId);
        if (nbsPlayer != null) {
            if (nbsPlayer.task == null || !nbsPlayer.task.isAlive()) {
                nbsPlayer.schedule();
            }
        }
    }
}