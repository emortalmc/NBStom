package dev.emortal.nbstom;

import dev.emortal.nbstom.types.Note;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NBSSong {

    private final byte version;
    private final byte instrumentCount;
    private final int length;
    private final int layerCount;
    private final String songName;
    private final String author;
    private final String originalAuthor;
    private final String description;
    private final double tps;
//    final boolean autoSaving;
//    final byte autoSavingDuration;
    private final byte timeSignature;
//    final int minutesSpent;
//    final int leftClicks;
//    final int rightClicks;
//    final int noteBlocksAdded;
//    final int noteBlocksRemoved;
//    final String midiFileName;
    private final boolean loop;
    private final byte maxLoopCount;
    private final int loopStart;

    private final List<List<Sound>> ticks;


    public NBSSong(Path path) throws IOException {
        var buffer = ByteBuffer.wrap(Files.readAllBytes(path)).order(ByteOrder.LITTLE_ENDIAN);

        BufferUtils.getUnsignedShort(buffer); // first 2 bytes are always nothing in new versions of NBS files
        this.version = buffer.get();
        this.instrumentCount = buffer.get();
        this.length = BufferUtils.getUnsignedShort(buffer);
        this.layerCount = BufferUtils.getUnsignedShort(buffer);
        this.songName = BufferUtils.getNBSString(buffer);
        this.author = BufferUtils.getNBSString(buffer);
        this.originalAuthor = BufferUtils.getNBSString(buffer);
        this.description = BufferUtils.getNBSString(buffer);
        this.tps = BufferUtils.getUnsignedShort(buffer) / 100.0;
        buffer.get(); // auto saving
        buffer.get(); // auto saving duration
        this.timeSignature = buffer.get();
        buffer.getInt(); // minutes spent
        buffer.getInt(); // left clicks
        buffer.getInt(); // right clicks
        buffer.getInt(); // note blocks added
        buffer.getInt(); // note blocks removed
        BufferUtils.getNBSString(buffer); // midi file name
        this.loop = buffer.get() == 1;
        this.maxLoopCount = buffer.get();
        this.loopStart = BufferUtils.getUnsignedShort(buffer);

        this.ticks = getTicks(buffer);
    }

    private List<List<Sound>> getTicks(ByteBuffer buffer) {
        List<List<Sound>> ticks = new ArrayList<>(length);

        while (true) {
            int jumps = BufferUtils.getUnsignedShort(buffer);
            if (jumps == 0) break;

            List<Sound> tick = getNotes(buffer);
            ticks.add(tick);
        }

        return ticks;
    }

    private @Nullable List<Sound> getNotes(ByteBuffer buffer) {
        List<Sound> notes = new ArrayList<>();

        while (true) {
            int jumps = BufferUtils.getUnsignedShort(buffer);
            if (jumps == 0) break;

            notes.add(Note.readNote(buffer).toSound(Sound.Source.RECORD));
        }

        if (notes.size() == 0) return null;
        return notes;
    }


    public byte getVersion() {
        return version;
    }

    public byte getInstrumentCount() {
        return instrumentCount;
    }

    public int getLength() {
        return length;
    }

    public int getLayerCount() {
        return layerCount;
    }

    public String getSongName() {
        return songName;
    }

    public String getAuthor() {
        return author;
    }

    public String getOriginalAuthor() {
        return originalAuthor;
    }

    public String getDescription() {
        return description;
    }

    public double getTps() {
        return tps;
    }

    public byte getTimeSignature() {
        return timeSignature;
    }

    public boolean isLoop() {
        return loop;
    }

    public byte getMaxLoopCount() {
        return maxLoopCount;
    }

    public int getLoopStart() {
        return loopStart;
    }

    public List<List<Sound>> getTicks() {
        return ticks;
    }
}
