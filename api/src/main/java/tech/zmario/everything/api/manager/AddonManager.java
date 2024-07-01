package tech.zmario.everything.api.manager;

import org.yaml.snakeyaml.Yaml;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.addons.Addon;
import tech.zmario.everything.api.addons.AddonProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class AddonManager {

    private final EverythingLibrary library;
    private final Map<String, Addon> addons = new HashMap<>();

    private AddonManager(EverythingLibrary library) {
        this.library = library;
    }

    public static AddonManager create(EverythingLibrary library) {
        return new AddonManager(library);
    }

    public void loadAddons() {
        File addonsFolder = new File(library.getFolder(), "addons");

        if (!addonsFolder.exists() && !addonsFolder.mkdirs()) {
            library.getLogger().severe("Failed to create addons folder!");
            return;
        }

        for (File file : addonsFolder.listFiles()) {
            if (file.getName().endsWith(".jar")) registerAddon(file);
        }
    }

    public void disableAddons() {
        for (Addon addon : addons.values()) addon.disable();

        addons.clear();
    }

    public void reloadAddons() {
        disableAddons();
        loadAddons();
    }

    private void registerAddon(File file) {
        try (JarFile jar = new JarFile(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(jar.getEntry("addon.yml"))))) {
            Yaml yaml = new Yaml();

            Map<String, Object> properties = yaml.load(reader);

            if (properties == null) {
                library.getLogger().severe(() -> "Failed to load addon.yml from " + file.getName());
                return;
            }

            AddonProperties addonProperties = new AddonProperties(properties);

            loadAddon(file, addonProperties);
        } catch (Exception e) {
            library.getLogger().log(Level.SEVERE, String.format("Failed to load addon %s", file.getName()), e);
        }
    }

    private void loadAddon(File file, AddonProperties properties) throws IOException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends Addon> addonClass;

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, getClass().getClassLoader())) {
            addonClass = classLoader.loadClass(properties.getMainClassName()).asSubclass(Addon.class);
        } catch (ClassNotFoundException e) {
            library.getLogger().log(Level.SEVERE, String.format("Failed to load addon %s", file.getName()), e);
            return;
        }

        File dataFolder = new File(library.getFolder(), properties.getName());

        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            library.getLogger().severe(() -> "Failed to create data folder for addon " + properties.getName());
            return;
        }
        Addon addon = addonClass.getConstructor(EverythingLibrary.class, File.class)
                .newInstance(library, dataFolder);

        addon.enable();

        addons.put(properties.getName(), addon);
    }

    public Map<String, Addon> getAddons() {
        return Collections.unmodifiableMap(addons);
    }
}
