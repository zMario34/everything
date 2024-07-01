package tech.zmario.everything.api.configurations;

import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.configurations.adapters.ObjectAdapter;
import tech.zmario.everything.api.configurations.objects.Property;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public abstract class Configuration {

    protected final Map<Class<?>, ObjectAdapter<?, ?>> adapters = new HashMap<>();

    private final EverythingLibrary library;
    private final File file;
    private final ConfigurationProperties properties;

    protected Configuration(EverythingLibrary library, File file, ConfigurationProperties properties, Locale locale,
                            ObjectAdapter<?, ?>... adapters) {
        this.library = library;
        this.properties = properties;

        if (locale != null) {
            properties.init(locale);

            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            // e.g. file.getName() = "config.yml" + english locale -> "config-en.yml"
            file = new File(file.getParentFile(), file.getName().replace("." + extension,
                    "-" + locale.getLanguage() + "." + extension));
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                library.getLogger().log(Level.SEVERE, String.format("Failed to create file %s", file.getName()), e);
            }
        }

        this.file = file;

        for (ObjectAdapter<?, ?> objectAdapter : adapters) registerAdapter(objectAdapter);
    }

    public File getFile() {
        return file;
    }

    public abstract <T> T get(Property<T> property);

    public abstract <T> void set(Property<T> property, Object value);

    public abstract void save();

    public abstract void reload();

    public abstract void load();

    public void delete() {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            library.getLogger().log(Level.SEVERE, String.format("Failed to delete file %s", file.getName()), e);
        }
    }

    public EverythingLibrary getLibrary() {
        return library;
    }

    public <T, F> void registerAdapter(ObjectAdapter<T, F> adapter) {
        adapters.put(adapter.getClazz(), adapter);
    }

    public Map<Class<?>, ObjectAdapter<?, ?>> getAdapters() {
        return adapters;
    }

    public ConfigurationProperties getProperties() {
        return properties;
    }
}
