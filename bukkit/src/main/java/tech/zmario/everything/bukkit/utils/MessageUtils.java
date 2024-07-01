package tech.zmario.everything.bukkit.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.zmario.everything.api.configurations.Configuration;
import tech.zmario.everything.api.configurations.objects.Property;
import tech.zmario.everything.bukkit.objects.Placeholder;

import java.time.Duration;
import java.util.AbstractList;
import java.util.List;

public class MessageUtils {

    public static BukkitAudiences BUKKIT_AUDIENCES = null;

    public static void sendTitle(Player player, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        Audience audience = BUKKIT_AUDIENCES.player(player);

        audience.showTitle(Title.title(title, subtitle,
                Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))));
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(player, Utils.legacyColorize(title), Utils.legacyColorize(subtitle), fadeIn, stay, fadeOut);
    }

    public static void sendActionBar(Player player, Component message) {
        Audience audience = BUKKIT_AUDIENCES.player(player);

        audience.sendActionBar(message);
    }

    public static void sendActionBar(Player player, String message) {
        sendActionBar(player, Utils.legacyColorize(message));
    }

    public static void sendMessage(CommandSender sender, Component message) {
        Audience audience = BUKKIT_AUDIENCES.sender(sender);

        audience.sendMessage(message);
    }

    /**
     * Replace placeholders in a string
     *
     * @param string       to replace the placeholders in
     * @param placeholders to replace
     * @return the string with the placeholders replaced
     */
    public static String replacePlaceholders(String string, Placeholder... placeholders) {
        for (Placeholder placeholder : placeholders)
            string = string.replace(placeholder.getKey(), placeholder.getValue());

        return string;
    }

    /**
     * Send a message to a player
     *
     * @param sender        who will receive the message
     * @param configuration the configuration to get the message from
     * @param property      the property of the message
     * @param placeholders  to replace in the message
     */
    public static void sendMessage(CommandSender sender, Configuration configuration, Property<?> property,
                                   Placeholder... placeholders) {
        Object object = configuration.get(property);

        if (object instanceof String) {
            String string = (String) object;

            string = replacePlaceholders(string, placeholders);

            sendMessage(sender, Utils.legacyColorize(string));
        } else if (object instanceof AbstractList<?>) {
            List<String> list = (List<String>) object;

            list.replaceAll(string -> replacePlaceholders(string, placeholders));

            Component component = Component.empty();

            for (String line : list)
                component = component.append(Utils.legacyColorize(line)).append(Component.newline());

            sendMessage(sender, component);
        } else if (object instanceof Component) {
            Component component = (Component) object;

            for (Placeholder placeholder : placeholders) {
                component = component.replaceText(text -> text.match(placeholder.getKey())
                        .replacement(placeholder.getValue()));
            }

            sendMessage(sender, component);
        } else {
            throw new IllegalArgumentException("Invalid message type: " + object.getClass().getSimpleName());
        }
    }
}
