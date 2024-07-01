package tech.zmario.everything.api.configurations.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.configurations.Configuration;
import tech.zmario.everything.api.configurations.ConfigurationProperties;
import tech.zmario.everything.api.configurations.adapters.ObjectAdapter;
import tech.zmario.everything.api.configurations.adapters.base.JsonObjectAdapter;
import tech.zmario.everything.api.configurations.objects.Property;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Locale;

public class JsonConfiguration extends Configuration {

    private static final Gson GSON = new Gson()
            .newBuilder()
            .setPrettyPrinting()
            .create();

    private JsonObject jsonObject;

    public JsonConfiguration(EverythingLibrary library, File file, ConfigurationProperties properties, Locale locale,
                             ObjectAdapter<?, ?>... adapters) {
        super(library, file, properties, locale, adapters);
    }

    public static JsonConfiguration create(EverythingLibrary library, File file, ConfigurationProperties properties,
                                           Locale locale, ObjectAdapter<?, ?>... adapters) {
        return new JsonConfiguration(library, file, properties, locale, adapters);
    }

    @Override
    public <T> T get(Property<T> property) {
        if (!jsonObject.has(property.getKey())) {
            set(property, property.getDefaultValue());

            return property.getDefaultValue();
        }
        JsonElement value = jsonObject.get(property.getKey());

        return (T) GSON.fromJson(value, property.getDefaultValue().getClass());
    }

    @Override
    public <T> void set(Property<T> property, Object value) {
        if (value == null) {
            jsonObject.remove(property.getKey());
            return;
        }

        jsonObject.add(property.getKey(), GSON.toJsonTree(value));
    }

    @Override
    public void save() {
        File file = getFile();

        try {
            file.createNewFile();

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(jsonObject, writer);
            }
        } catch (IOException e) {
            getLibrary().getLogger().warning("Failed to save file");
        }
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public void load() {
        File file = getFile();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            jsonObject = GSON.fromJson(new FileReader(getFile()), JsonObject.class);
        } catch (FileNotFoundException e) {
            getLibrary().getLogger().warning("File not found: " + file.getName());
        }

        if (jsonObject == null) jsonObject = new JsonObject();

        Class<?> clazz = getProperties().getClass();

        boolean save = false;

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAccessible()) field.setAccessible(true);

            if (field.getType().isAssignableFrom(Property.class)) {
                try {
                    Property<?> property = (Property<?>) field.get(getProperties());

                    if (!jsonObject.has(property.getKey())) {
                        set(property, property.getDefaultValue());
                        save = true;
                    }
                } catch (IllegalAccessException e) {
                    getLibrary().getLogger().severe("Failed to access field");
                }
            }
        }

        if (save) save();
    }

    @Override
    public <T, F> void registerAdapter(ObjectAdapter<T, F> adapter) {
        if (!(adapter instanceof JsonObjectAdapter<?>))
            throw new IllegalArgumentException("adapter must be an instance of JsonObjectAdapter");

        super.registerAdapter(adapter);
    }
}
