package net.redm1ne.deluxeplayeroptionsred.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling sound compatibility across Minecraft versions.
 * Sound names changed between versions, particularly in 1.17+.
 */
public final class SoundCompat {

    // Cache for sound lookups
    private static final Map<String, Sound> SOUND_CACHE = new HashMap<>();

    private SoundCompat() {}

    /**
     * Get a sound by trying multiple names in order.
     *
     * @param names Sound names to try in order
     * @return The first valid sound, or null
     */
    @Nullable
    public static Sound getSound(@NotNull String... names) {
        String cacheKey = String.join("|", names);

        if (SOUND_CACHE.containsKey(cacheKey)) {
            return SOUND_CACHE.get(cacheKey);
        }

        for (String name : names) {
            Sound sound = tryGetSound(name);
            if (sound != null) {
                SOUND_CACHE.put(cacheKey, sound);
                return sound;
            }
        }

        SOUND_CACHE.put(cacheKey, null);
        return null;
    }

    /**
     * Try to get a sound by name safely.
     *
     * @param name The sound name
     * @return The sound, or null if invalid
     */
    @Nullable
    private static Sound tryGetSound(@NotNull String name) {
        try {
            return Sound.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get the generic explosion sound.
     * Changed from EXPLODE to ENTITY_GENERIC_EXPLODE in 1.17+.
     *
     * @return The explosion sound
     */
    @NotNull
    public static Sound getExplosionSound() {
        Sound sound = getSound("ENTITY_GENERIC_EXPLODE", "EXPLODE");
        return sound != null ? sound : Sound.ENTITY_GENERIC_EXPLODE;
    }

    /**
     * Get the level up sound.
     *
     * @return The level up sound
     */
    @NotNull
    public static Sound getLevelUpSound() {
        Sound sound = getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP");
        return sound != null ? sound : Sound.ENTITY_PLAYER_LEVELUP;
    }

    /**
     * Get the click sound.
     *
     * @return The click sound
     */
    @NotNull
    public static Sound getClickSound() {
        Sound sound = getSound("UI_BUTTON_CLICK", "CLICK");
        return sound != null ? sound : Sound.UI_BUTTON_CLICK;
    }

    /**
     * Get the pop sound.
     *
     * @return The pop sound
     */
    @NotNull
    public static Sound getPopSound() {
        Sound sound = getSound("ENTITY_ITEM_PICKUP", "ITEM_PICKUP");
        return sound != null ? sound : Sound.ENTITY_ITEM_PICKUP;
    }

    /**
     * Get the teleport sound (enderpearl).
     *
     * @return The teleport sound
     */
    @NotNull
    public static Sound getTeleportSound() {
        Sound sound = getSound("ENTITY_ENDERMAN_TELEPORT", "ENDERMAN_TELEPORT");
        return sound != null ? sound : Sound.ENTITY_ENDERMAN_TELEPORT;
    }

    /**
     * Get the flame sound.
     *
     * @return The flame sound
     */
    @NotNull
    public static Sound getFlameSound() {
        return Sound.ITEM_FIRECHARGE_USE;
    }

    /**
     * Play a sound to a player with default volume and pitch.
     *
     * @param player The player
     * @param sound The sound to play
     */
    public static void playSound(@NotNull Player player, @NotNull Sound sound) {
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }

    /**
     * Play a sound to a player with custom volume and pitch.
     *
     * @param player The player
     * @param sound The sound to play
     * @param volume The volume (0.0 - 1.0)
     * @param pitch The pitch (0.5 - 2.0)
     */
    public static void playSound(@NotNull Player player, @NotNull Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    /**
     * Play a sound to a player, trying multiple sound names.
     *
     * @param player The player
     * @param names Sound names to try in order
     */
    public static void playSound(@NotNull Player player, @NotNull String... names) {
        Sound sound = getSound(names);
        if (sound != null) {
            playSound(player, sound);
        }
    }
}
