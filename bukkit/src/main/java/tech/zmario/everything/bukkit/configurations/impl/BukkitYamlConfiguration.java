package tech.zmario.everything.bukkit.configurations.impl;

import org.bukkit.command.CommandSender;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.configurations.ConfigurationProperties;
import tech.zmario.everything.api.configurations.adapters.ObjectAdapter;
import tech.zmario.everything.api.configurations.impl.YamlConfiguration;
import tech.zmario.everything.api.configurations.objects.Property;
import tech.zmario.everything.bukkit.objects.Placeholder;
import tech.zmario.everything.bukkit.utils.MessageUtils;

import java.io.File;
import java.util.Locale;

public class BukkitYamlConfiguration extends YamlConfiguration {

    public BukkitYamlConfiguration(EverythingLibrary library, File file, ConfigurationProperties properties,
                                   Locale locale, ObjectAdapter<?, ?>... adapters) {
        super(library, file, properties, locale, adapters);
    }

    public static BukkitYamlConfiguration create(EverythingLibrary library, File file, ConfigurationProperties properties,
                                                 Locale locale, ObjectAdapter<?, ?>... adapters) {
        return new BukkitYamlConfiguration(library, file, properties, locale, adapters);
    }

    public void sendMessage(CommandSender sender, Property<?> property, Placeholder... placeholders) {
        MessageUtils.sendMessage(sender, this, property, placeholders);
    }
}
