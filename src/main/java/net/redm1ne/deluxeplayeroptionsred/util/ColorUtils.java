package net.redm1ne.deluxeplayeroptionsred.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling color codes across Minecraft versions.
 * Supports legacy color codes (& + char) and HEX colors (1.16+).
 */
public final class ColorUtils {

    // HEX color pattern: <#RRGGBB> or #RRGGBB
    private static final Pattern HEX_PATTERN = Pattern.compile("[&#]([A-Fa-f0-9]{6})");

    private ColorUtils() {}

    /**
     * Translate color codes in a string.
     * Supports both legacy (& + char) and HEX colors (#RRGGBB).
     *
     * @param text The text to translate
     * @return The translated text with colors
     */
    public static String translate(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String result = text;

        // Handle HEX colors for 1.16+
        if (VersionUtils.supportsHexColors()) {
            result = translateHexColors(result);
        }

        // Translate legacy color codes
        result = ChatColor.translateAlternateColorCodes('&', result);

        return result;
    }

    /**
     * Translate HEX color codes in a string.
     * Supports both &#RRGGBB and <#RRGGBB> formats.
     *
     * @param text The text to translate
     * @return The text with HEX colors converted
     */
    private static String translateHexColors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            try {
                ChatColor chatColor = ChatColor.of("#" + hexColor);
                matcher.appendReplacement(buffer, chatColor.toString());
            } catch (IllegalArgumentException e) {
                // Invalid hex color, keep original
                matcher.appendReplacement(buffer, matcher.group());
            }
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    /**
     * Translate a list of strings.
     *
     * @param list The list to translate
     * @return A new list with all strings translated
     */
    public static List<String> translateList(List<String> list) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<String> translated = new ArrayList<>(list.size());
        for (String line : list) {
            translated.add(translate(line));
        }
        return translated;
    }

    /**
     * Convert text to native Minecraft color codes (from & to section sign).
     * This bypasses hex color translation.
     *
     * @param text The text to convert
     * @return The text with native color codes
     */
    public static String toNative(String text) {
        if (text == null) {
            return "";
        }
        return text.replace('&', '\u00A7');
    }

    /**
     * Strip all color codes from a string.
     *
     * @param text The text to strip
     * @return The text without any color codes
     */
    public static String stripColors(String text) {
        if (text == null) {
            return "";
        }

        // Remove all color codes (section sign + char)
        String result = text.replaceAll("[\u00A7&]([0-9A-Fa-fK-Ok-oRr])", "");

        // Remove HEX color codes
        result = result.replaceAll("[&#]([A-Fa-f0-9]{6})", "");

        return result.trim();
    }

    /**
     * Send a translated message to a player.
     *
     * @param player The player to send to
     * @param message The message to send
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(translate(message));
    }

    /**
     * Send a translated message to a command sender.
     *
     * @param sender The command sender to send to
     * @param message The message to send
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(translate(message));
    }

    /**
     * Send a colored line separator to a player.
     *
     * @param player The player to send to
     * @param color The color code for the line
     */
    public static void sendLine(Player player, String color) {
        String line = color != null && !color.isEmpty()
            ? translate(color + "---------------------------------------------------")
            : "---------------------------------------------------";
        player.sendMessage(line);
    }

    /**
     * Send a colored line separator to a command sender.
     *
     * @param sender The command sender to send to
     * @param color The color code for the line
     */
    public static void sendLine(CommandSender sender, String color) {
        String line = color != null && !color.isEmpty()
            ? translate(color + "---------------------------------------------------")
            : "---------------------------------------------------";
        sender.sendMessage(line);
    }

    /**
     * Create a colored text component for titles/scoreboards.
     *
     * @param text The text to format
     * @return The formatted text
     */
    public static String formatText(String text) {
        if (text == null || text.isEmpty()) {
            return " ";
        }

        String translated = translate(text.trim());

        // Add gray color to words that don't start with a color code
        StringBuilder result = new StringBuilder();
        String[] words = translated.split(" ");
        String lastColor = "\u00A77"; // Gray

        for (String word : words) {
            if (word.startsWith("\u00A7")) {
                result.append(word);
                // Update last color
                if (word.length() >= 2) {
                    lastColor = word.substring(0, 2);
                }
            } else {
                result.append(lastColor).append(word);
            }
            result.append(" ");
        }

        return result.toString().trim();
    }

    /**
     * Log a colored message to the console.
     *
     * @param message The message to log
     */
    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(translate(message));
    }
}
