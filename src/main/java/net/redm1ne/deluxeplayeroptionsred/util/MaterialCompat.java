package net.redm1ne.deluxeplayeroptionsred.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling material compatibility across Minecraft versions.
 * Provides methods to get materials that work on both old (1.16) and new (1.21+) versions.
 */
public final class MaterialCompat {

    // Cache for material lookups
    private static final Map<String, Material> MATERIAL_CACHE = new HashMap<>();

    private MaterialCompat() {}

    /**
     * Get a material by name, with fallback for older versions.
     *
     * @param modernName Modern material name (1.17+)
     * @param legacyName Legacy material name (1.16)
     * @return The material, or null if neither exists
     */
    @Nullable
    public static Material getMaterial(@NotNull String modernName, @NotNull String legacyName) {
        String cacheKey = modernName + "|" + legacyName;

        if (MATERIAL_CACHE.containsKey(cacheKey)) {
            return MATERIAL_CACHE.get(cacheKey);
        }

        Material material = tryGetMaterial(modernName);
        if (material == null) {
            material = tryGetMaterial(legacyName);
        }

        MATERIAL_CACHE.put(cacheKey, material);
        return material;
    }

    /**
     * Get a material by trying multiple names in order.
     *
     * @param names Material names to try in order
     * @return The first valid material, or null
     */
    @Nullable
    public static Material getMaterial(@NotNull String... names) {
        for (String name : names) {
            Material material = tryGetMaterial(name);
            if (material != null) {
                return material;
            }
        }
        return null;
    }

    /**
     * Try to get a material by name safely.
     *
     * @param name The material name
     * @return The material, or null if invalid
     */
    @Nullable
    private static Material tryGetMaterial(@NotNull String name) {
        try {
            return Material.matchMaterial(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get the ON indicator material (lime dye / ink_sack:10).
     *
     * @return The ON indicator material
     */
    @NotNull
    public static Material getOnIndicator() {
        if (VersionUtils.isAtLeast(17)) {
            return Material.LIME_DYE;
        }
        // 1.16 fallback
        return Material.valueOf("INK_SACK");
        // Note: data value 10 would need to be handled separately
    }

    /**
     * Get the OFF indicator material (gray ink sac / ink_sack:8).
     *
     * @return The OFF indicator material
     */
    @NotNull
    public static Material getOffIndicator() {
        if (VersionUtils.isAtLeast(17)) {
            return Material.GRAY_DYE;
        }
        // 1.16 fallback
        return Material.INK_SAC;
    }

    /**
     * Get the close button material (nether star).
     *
     * @return The close button material
     */
    @NotNull
    public static Material getCloseButton() {
        return Material.NETHER_STAR;
    }

    /**
     * Get glass pane material for decoration.
     *
     * @param colorData Color data value (0-15 for legacy)
     * @return The glass pane material
     */
    @NotNull
    public static Material getGlassPane(int colorData) {
        if (VersionUtils.isAtLeast(13)) {
            // Modern: use colored glass panes
            return switch (colorData) {
                case 0 -> Material.WHITE_STAINED_GLASS_PANE;
                case 1 -> Material.ORANGE_STAINED_GLASS_PANE;
                case 2 -> Material.MAGENTA_STAINED_GLASS_PANE;
                case 3 -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
                case 4 -> Material.YELLOW_STAINED_GLASS_PANE;
                case 5 -> Material.LIME_STAINED_GLASS_PANE;
                case 6 -> Material.PINK_STAINED_GLASS_PANE;
                case 7 -> Material.GRAY_STAINED_GLASS_PANE;
                case 8 -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
                case 9 -> Material.CYAN_STAINED_GLASS_PANE;
                case 10 -> Material.PURPLE_STAINED_GLASS_PANE;
                case 11 -> Material.BLUE_STAINED_GLASS_PANE;
                case 12 -> Material.BROWN_STAINED_GLASS_PANE;
                case 13 -> Material.GREEN_STAINED_GLASS_PANE;
                case 14 -> Material.RED_STAINED_GLASS_PANE;
                case 15 -> Material.BLACK_STAINED_GLASS_PANE;
                default -> Material.WHITE_STAINED_GLASS_PANE;
            };
        }
        // Legacy - would need Bukkit.createItemStack with data
        return Material.WHITE_STAINED_GLASS_PANE;
    }

    /**
     * Get speed item material.
     *
     * @return Sugar material
     */
    @NotNull
    public static Material getSpeedMaterial() {
        return Material.SUGAR;
    }

    /**
     * Get jump item material (diamond boots).
     *
     * @return Diamond boots material
     */
    @NotNull
    public static Material getJumpMaterial() {
        return Material.DIAMOND_BOOTS;
    }

    /**
     * Get double jump item material (chainmail boots).
     *
     * @return Chainmail boots material
     */
    @NotNull
    public static Material getDoubleJumpMaterial() {
        return Material.CHAINMAIL_BOOTS;
    }

    /**
     * Get fly item material (feather).
     *
     * @return Feather material
     */
    @NotNull
    public static Material getFlyMaterial() {
        return Material.FEATHER;
    }

    /**
     * Get stacker item material (magma cream).
     *
     * @return Magma cream material
     */
    @NotNull
    public static Material getStackerMaterial() {
        return Material.MAGMA_CREAM;
    }

    /**
     * Get visibility item material (ender eye / eye of ender).
     *
     * @return Ender eye material
     */
    @NotNull
    public static Material getVisibilityMaterial() {
        Material material = getMaterial("ENDER_EYE", "EYE_OF_ENDER");
        return material != null ? material : Material.ENDER_PEARL;
    }

    /**
     * Get chat item material (paper).
     *
     * @return Paper material
     */
    @NotNull
    public static Material getChatMaterial() {
        return Material.PAPER;
    }

    /**
     * Get radio item material (jukebox).
     *
     * @return Jukebox material
     */
    @NotNull
    public static Material getRadioMaterial() {
        return Material.JUKEBOX;
    }

    /**
     * Get PvP item material (diamond sword).
     *
     * @return Diamond sword material
     */
    @NotNull
    public static Material getPvPMaterial() {
        return Material.DIAMOND_SWORD;
    }
}
