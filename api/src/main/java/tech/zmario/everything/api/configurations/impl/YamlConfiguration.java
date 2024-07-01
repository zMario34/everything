package tech.zmario.everything.api.configurations.impl;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.configurations.Configuration;
import tech.zmario.everything.api.configurations.ConfigurationProperties;
import tech.zmario.everything.api.configurations.adapters.ObjectAdapter;
import tech.zmario.everything.api.configurations.adapters.base.YamlObjectAdapter;
import tech.zmario.everything.api.configurations.objects.Property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class YamlConfiguration extends Configuration {

    private final Map<String, Object> cache = new HashMap<>();

    private Map<String, Object> yamlData;

    public YamlConfiguration(EverythingLibrary library, File file, ConfigurationProperties properties, Locale locale,
                             ObjectAdapter<?, ?>... adapters) {
        super(library, file, properties, locale, adapters);
    }

    public static YamlConfiguration create(EverythingLibrary library, File file, ConfigurationProperties properties,
                                           Locale locale, ObjectAdapter<?, ?>... adapters) {
        return new YamlConfiguration(library, file, properties, locale, adapters);
    }

    @Override
    public <T> T get(Property<T> property) {
        if (cache.containsKey(property.getKey())) return (T) cache.get(property.getKey());

        /*Object oldValue = yamlData.get(property.getKey());

        if (oldValue == null) {
            set(property, property.getDefaultValue());
            return property.getDefaultValue();
        }
        Class<?> type = property.getDefaultValue() == null ? oldValue.getClass() : property.getDefaultValue().getClass();

        for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
            if (adapters.containsKey(clazz)) {
                YamlObjectAdapter<?> adapter = (YamlObjectAdapter<?>) adapters.get(clazz);
                return (T) adapter.get(yamlData, property.getKey());
            }
        }

        if (type.isPrimitive() || type.equals(String.class)) {
            return (T) yamlData.get(property.getKey());
        }

        try {
            T instance = (T) type.newInstance();

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);

                boolean contains = yamlData.containsKey(property.getKey() + "." + field.getName());

                if (!contains) {
                    getLibrary().getLogger().warning(String.format("Property %s.%s not found", property.getKey(), field.getName()));
                    continue;
                }

                if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    field.set(instance, yamlData.get(property.getKey() + "." + field.getName()));
                    continue;
                }
                Object value = get(new Property<>(property.getKey() + "." + field.getName(), null));

                if (value != null) field.set(instance, value);
            }

            cache.put(property.getKey(), instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException ignored) {
            getLibrary().getLogger().warning(() -> "Failed to create instance of " + type.getName() + " for property " + property.getKey());
        }*/

        String key = property.getKey();

        String[] keys = key.split("\\.");
        Class<?> type = property.getDefaultValue() == null ? null : property.getDefaultValue().getClass();
        T value = null;

        if (type != null) {
            for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
                if (adapters.containsKey(clazz)) {
                    YamlObjectAdapter<?> adapter = (YamlObjectAdapter<?>) adapters.get(clazz);

                    value = (T) adapter.get(yamlData, key);
                    break;
                }
            }
        }

        if (value == null) {
            if (keys.length <= 1) {
                value = (T) yamlData.get(key);
            } else {
                Map<String, Object> currentMap = yamlData;

                for (int i = 0; i < keys.length - 1; i++)
                    currentMap = (Map<String, Object>) yamlData.get(keys[i]);

                value = (T) currentMap.get(keys[keys.length - 1]);
            }
        }

        cache.put(key, value);

        return value;
    }

    @Override
    public <T> void set(Property<T> property, Object value) {
        String key = property.getKey();
        String[] keys = key.split("\\.");

        Class<?> type = value.getClass();

        for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
            if (adapters.containsKey(clazz)) {
                YamlObjectAdapter<?> adapter = (YamlObjectAdapter<?>) adapters.get(clazz);

                adapter.set(yamlData, key, value);
                save();
                return;
            }
        }

        if (keys.length <= 1) {
            yamlData.put(key, value);
            save();
            return;
        }

        Map<String, Object> currentMap = yamlData;

        for (int i = 0; i < keys.length - 1; i++)
            currentMap = (Map<String, Object>) yamlData.computeIfAbsent(keys[i], k -> new HashMap<>());

        currentMap.put(keys[keys.length - 1], value);
        save();
    }

    @Override
    public void save() {
        try (FileWriter outputStream = new FileWriter(getFile())) {
            DumperOptions options = new DumperOptions();

            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setCanonical(false);
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);

            String output = yaml.dumpAs(yamlData, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
            output = output.replaceAll(": !!.*", ":");

            outputStream.write(output);
        } catch (IOException e) {
            getLibrary().getLogger().warning(() -> "Failed to save file: " + getFile().getName());
        }
    }

    @Override
    public void reload() {
        cache.clear();
        load();
    }

    @Override
    public void load() {
        File file = getFile();

        try {
            file.createNewFile();
        } catch (IOException e) {
            getLibrary().getLogger().log(Level.SEVERE, String.format("Failed to create file: %s", file.getName()), e);
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            Yaml yaml = new Yaml();

            yamlData = yaml.load(inputStream);
        } catch (IOException ex) {
            getLibrary().getLogger().log(Level.SEVERE, String.format("Failed to load file: %s", file.getName()), ex);
        }

        if (yamlData == null) yamlData = new HashMap<>();

        Class<?> clazz = getProperties().getClass();
        boolean save = false;

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAccessible()) field.setAccessible(true);

            if (field.getType().isAssignableFrom(Property.class)) {
                try {
                    Property<?> property = (Property<?>) field.get(getProperties());

                    if (!yamlData.containsKey(property.getKey())) {
                        set(property, property.getDefaultValue());
                        save = true;
                    }
                } catch (IllegalAccessException e) {
                    getLibrary().getLogger().log(Level.SEVERE, "Failed to access field", e);
                }
            }
        }

        if (save) save();
    }

    @Override
    public <T, F> void registerAdapter(ObjectAdapter<T, F> adapter) {
        if (!(adapter instanceof YamlObjectAdapter<?>))
            throw new IllegalArgumentException("adapter must be an instance of YamlObjectAdapter");

        super.registerAdapter(adapter);
    }
}