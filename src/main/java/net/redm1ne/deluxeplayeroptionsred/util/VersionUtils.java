package net.redm1ne.deluxeplayeroptionsred.util;

import org.bukkit.Bukkit;

/**
 * Utility class for detecting and comparing Minecraft versions.
 * Supports versions from 1.16 to 1.21+.
 */
public final class VersionUtils {

    private static final int MAJOR_VERSION;
    private static final int MINOR_VERSION;
    private static final String BUKKIT_VERSION;
    private static final String MINECRAFT_VERSION;

    static {
        BUKKIT_VERSION = Bukkit.getBukkitVersion().split("-")[0];
        String[] parts = BUKKIT_VERSION.split("\\.");

        // Format: 1.XX.Y or X.XX.Y
        // parts[0] = "1", parts[1] = "XX", parts[2] = "Y" (if exists)
        if (parts.length >= 2) {
            MAJOR_VERSION = Integer.parseInt(parts[0]);
            MINOR_VERSION = Integer.parseInt(parts[1]);
        } else {
            MAJOR_VERSION = 1;
            MINOR_VERSION = 16;
        }

        MINECRAFT_VERSION = MAJOR_VERSION + "." + MINOR_VERSION;
    }

    private VersionUtils() {}

    /**
     * Get the Bukkit version string (e.g., "1.21.1")
     *
     * @return The Bukkit version
     */
    public static String getBukkitVersion() {
        return BUKKIT_VERSION;
    }

    /**
     * Get the Minecraft version as major.minor (e.g., "1.21")
     *
     * @return The Minecraft version string
     */
    public static String getMinecraftVersion() {
        return MINECRAFT_VERSION;
    }

    /**
     * Get the minor version number (16, 17, 18, 19, 20, 21)
     *
     * @return The minor version
     */
    public static int getMinorVersion() {
        return MINOR_VERSION;
    }

    /**
     * Check if the server is running at least the specified minor version.
     *
     * @param minorVersion The minimum minor version (e.g., 16, 17, 20)
     * @return true if server version is equal or greater
     */
    public static boolean isAtLeast(int minorVersion) {
        return MINOR_VERSION >= minorVersion;
    }

    /**
     * Check if the server is running exactly the specified version.
     *
     * @param minorVersion The minor version to check
     * @return true if versions match
     */
    public static boolean isVersion(int minorVersion) {
        return MINOR_VERSION == minorVersion;
    }

    /**
     * Check if the server is running 1.16.x
     *
     * @return true if running 1.16.x
     */
    public static boolean is116() {
        return MINOR_VERSION == 16;
    }

    /**
     * Check if the server is running 1.17.x
     *
     * @return true if running 1.17.x
     */
    public static boolean is117() {
        return MINOR_VERSION == 17;
    }

    /**
     * Check if the server is running 1.18.x
     *
     * @return true if running 1.18.x
     */
    public static boolean is118() {
        return MINOR_VERSION == 18;
    }

    /**
     * Check if the server is running 1.19.x
     *
     * @return true if running 1.19.x
     */
    public static boolean is119() {
        return MINOR_VERSION == 19;
    }

    /**
     * Check if the server is running 1.20.x
     *
     * @return true if running 1.20.x
     */
    public static boolean is120() {
        return MINOR_VERSION == 20;
    }

    /**
     * Check if the server is running 1.21.x or higher
     *
     * @return true if running 1.21.x or higher
     */
    public static boolean is121OrNewer() {
        return MINOR_VERSION >= 21;
    }

    /**
     * Check if HEX colors are supported (1.16+)
     *
     * @return true if HEX colors are supported
     */
    public static boolean supportsHexColors() {
        return isAtLeast(16);
    }

    /**
     * Check if the new Material names are used (1.17+)
     *
     * @return true if new material names are used
     */
    public static boolean usesNewMaterialNames() {
        return isAtLeast(17);
    }

    /**
     * Check if the new Sound names are used (1.17+)
     *
     * @return true if new sound names are used
     */
    public static boolean usesNewSoundNames() {
        return isAtLeast(17);
    }

    /**
     * Check if new PotionEffectType names are used (JUMP_BOOST vs JUMP)
     *
     * @return true if new names are used
     */
    public static boolean usesNewPotionNames() {
        return isAtLeast(21);
    }

    @Override
    public String toString() {
        return "VersionUtils{minecraft=" + MINECRAFT_VERSION + ", bukkit=" + BUKKIT_VERSION + "}";
    }
}
