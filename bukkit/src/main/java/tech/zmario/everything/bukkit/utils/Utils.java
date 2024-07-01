package tech.zmario.everything.bukkit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    /**
     * The version of the server as an integer (ex. 1.16.5 -> 16, 1.8.8 -> 8)
     */
    public static final int BUKKIT_VERSION;
    /**
     * Whether the server is running Paper
     */
    public static final boolean PAPER_SUPPORTED;
    /**
     * Pattern to match hex colors using the '&' character
     */
    public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
    public static final Server SERVER = Bukkit.getServer();

    static {
        BUKKIT_VERSION = Integer.parseInt(Bukkit.getServer().getVersion().split(" ")[2].split("\\.")[1]);

        boolean supported;

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            supported = true;
        } catch (ClassNotFoundException ignored) {
            supported = false;
        }

        PAPER_SUPPORTED = supported;

        SERVER.getLogger().info("Bukkit version: " + BUKKIT_VERSION);
    }

    private Utils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Deserialize a MiniMessage string into a Component
     *
     * @param message The message to deserialize
     * @return The deserialized message as a Component
     * @see Component
     */
    public static Component miniMessage(String message) {
        if (message == null) throw new IllegalArgumentException("message cannot be null");

        if (message.isEmpty()) return Component.empty();

        return MiniMessage.miniMessage().deserialize(message);
    }

    /**
     * Colorize a string using the '&' character
     *
     * @param message The message to colorize
     * @return The colorized message
     */
    public static String colorize(String message) {
        return colorize(message, '&');
    }

    public static Component legacyColorize(String message) {
        if (message == null) throw new IllegalArgumentException("message cannot be null");

        if (message.isEmpty()) return Component.empty();

        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    /**
     * Colorize a string using the specified character
     *
     * @param message The message to colorize
     * @return The colorized message
     */
    public static String colorize(String message, char character) {
        if (message == null) throw new IllegalArgumentException("message cannot be null");

        if (message.isEmpty()) return "";

        if (BUKKIT_VERSION >= 16) {
            Matcher matcher = character == '&' ? HEX_PATTERN.matcher(message) :
                    Pattern.compile(character + "(#[A-Fa-f0-9]{6})").matcher(message);

            while (matcher.find()) {
                String hex = matcher.group(1);
                message = message.replace(hex, net.md_5.bungee.api.ChatColor.of(hex).toString());
            }
        }

        return ChatColor.translateAlternateColorCodes(character, message);
    }

    /**
     * Find an offline player by name, if the server is running Paper 1.16 or higher, it will use the cached method
     *
     * @param name The name of the player to find
     * @return The player if found, otherwise null
     *
     * @see org.bukkit.Server#getOfflinePlayer(String)
     * @see org.bukkit.Server#getOfflinePlayerIfCached(String) (if running Paper 1.16 or higher)
     */
    public static OfflinePlayer findOfflinePlayer(String name) {
        return PAPER_SUPPORTED && BUKKIT_VERSION >= 16 ?
                SERVER.getOfflinePlayerIfCached(name) : SERVER.getOfflinePlayer(name);
    }

    /**
     * Whether the server is running Paper and is 1.16 or higher, allowing the use of Adventure by default
     *
     * @return true if the server is running Paper and is 1.16 or higher
     */
    public static boolean hasPaperAndAdventure() {
        return PAPER_SUPPORTED && BUKKIT_VERSION >= 16;
    }

    public static List<String> colorize(List<String> lore) {
        return lore.stream().map(Utils::colorize).collect(Collectors.toList());
    }

    public static List<Component> legacyColorize(List<String> lore) {
        return lore.stream().map(Utils::legacyColorize).collect(Collectors.toList());
    }
}
