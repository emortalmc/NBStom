package dev.emortal.nbstom;

import net.kyori.adventure.audience.Audience;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.testing.Env;
import net.minestom.testing.EnvTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@EnvTest
public class NBSTests {

    private static final Path TEST_NBS_PATH = Path.of("src/test/resources/test.nbs");

    @Test
    public void readAndPlay(Env env) throws IOException, InterruptedException {
        NBSSong testSong = new NBSSong(TEST_NBS_PATH);
        assertEquals(testSong.getSongName(), "Test");
        assertEquals(testSong.getTps(), 10.0);
        assertEquals(testSong.getVersion(), 5);
        assertEquals(testSong.getLayerCount(), 23);

        UUID uuid = UUID.randomUUID();
        NBS.play(testSong, Audience.empty(), env.process().scheduler(), uuid);
        env.tickWhile(() -> NBS.playingTaskMap.get(uuid).isAlive(), Duration.ofSeconds(1));
        Thread.sleep(2000);
    }

}
