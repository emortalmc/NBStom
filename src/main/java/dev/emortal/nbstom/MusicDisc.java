package dev.emortal.nbstom;

import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

public enum MusicDisc {
    MUSIC_DISC_13(Material.MUSIC_DISC_13, SoundEvent.MUSIC_DISC_13, "13", "C418 - 13", 178),
    MUSIC_DISC_CAT(Material.MUSIC_DISC_CAT, SoundEvent.MUSIC_DISC_CAT, "cat", "C418 - cat", 185),
    MUSIC_DISC_BLOCKS(Material.MUSIC_DISC_BLOCKS, SoundEvent.MUSIC_DISC_BLOCKS, "blocks", "C418 - blocks", 345),
    MUSIC_DISC_CHIRP(Material.MUSIC_DISC_CHIRP, SoundEvent.MUSIC_DISC_CHIRP, "chirp", "C418 - chirp", 185),
    MUSIC_DISC_FAR(Material.MUSIC_DISC_FAR, SoundEvent.MUSIC_DISC_FAR, "far", "C418 - far", 174),
    MUSIC_DISC_MALL(Material.MUSIC_DISC_MALL, SoundEvent.MUSIC_DISC_MALL, "mall", "C418 - mall", 197),
    MUSIC_DISC_MELLOHI(Material.MUSIC_DISC_MELLOHI, SoundEvent.MUSIC_DISC_MELLOHI, "mellohi", "C418 - mellohi", 96),
    MUSIC_DISC_STAL(Material.MUSIC_DISC_STAL, SoundEvent.MUSIC_DISC_STAL, "stal", "C418 - stal", 150),
    MUSIC_DISC_STRAD(Material.MUSIC_DISC_STRAD, SoundEvent.MUSIC_DISC_STRAD, "strad", "C418 - strad", 188),
    MUSIC_DISC_WARD(Material.MUSIC_DISC_WARD, SoundEvent.MUSIC_DISC_WARD, "ward", "C418 - ward", 251),
    MUSIC_DISC_11(Material.MUSIC_DISC_11, SoundEvent.MUSIC_DISC_11, "11", "C418 - 11", 71),
    MUSIC_DISC_WAIT(Material.MUSIC_DISC_WAIT, SoundEvent.MUSIC_DISC_WAIT, "wait", "C418 - wait", 228),
    MUSIC_DISC_OTHERSIDE(
            Material.MUSIC_DISC_OTHERSIDE,
            SoundEvent.MUSIC_DISC_OTHERSIDE,
            "otherside",
            "Lena Raine - otherside",
            195
    ),
    MUSIC_DISC_RELIC(
            Material.MUSIC_DISC_RELIC,
            SoundEvent.MUSIC_DISC_RELIC,
            "Relic",
            "Aaron Cherof - Relic",
            219
    ),
    MUSIC_DISC_5(Material.MUSIC_DISC_5, SoundEvent.MUSIC_DISC_5, "5", "Samuel Åberg - 5", 178),
    MUSIC_DISC_PIGSTEP(
            Material.MUSIC_DISC_PIGSTEP,
            SoundEvent.MUSIC_DISC_PIGSTEP,
            "Pigstep",
            "Lena Raine - Pigstep",
            148
    );

    final Material material;
    final SoundEvent sound;
    final String shortName;
    final String description;
    final int length;
    MusicDisc(Material material, SoundEvent sound, String shortName, String description, int length) {
        this.material = material;
        this.sound = sound;
        this.shortName = shortName;
        this.description = description;
        this.length = length;
    }

    public Material getMaterial() {
        return material;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public int getLength() {
        return length;
    }

    public static MusicDisc fromMaterial(Material material) {
        return valueOf(material.name().split(":")[1].toUpperCase());
    }

    public static MusicDisc fromSoundEvent(SoundEvent soundEvent) {
        return valueOf(soundEvent.name());
    }
}