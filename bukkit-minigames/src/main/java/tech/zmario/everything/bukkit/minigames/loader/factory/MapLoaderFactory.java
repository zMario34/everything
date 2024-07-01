package tech.zmario.everything.bukkit.minigames.loader.factory;

import tech.zmario.everything.bukkit.minigames.GameHandler;
import tech.zmario.everything.bukkit.minigames.loader.MapLoader;
import tech.zmario.everything.bukkit.minigames.loader.map.impl.bukkit.BukkitMapLoader;
import tech.zmario.everything.bukkit.minigames.loader.map.impl.slime.SlimeMapLoader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapLoaderFactory {

    private static final Map<String, MapLoader<?>> mapLoaders = new HashMap<>();

    private MapLoaderFactory() {
        throw new IllegalStateException("MapLoaderFactory cannot be instantiated! Please use the static methods.");
    }

    /**
     * Initializes the MapLoaderFactory with the default MapLoaders (BukkitMapLoader and SlimeMapLoader).
     *
     * @param gameHandler the GameHandler instance
     */
    public static void initialize(GameHandler gameHandler) {
        if (gameHandler.getEverything().getServer().getPluginManager().isPluginEnabled("SlimeWorldManager"))
            registerMapLoader(new SlimeMapLoader(gameHandler, "file"));

        registerMapLoader(new BukkitMapLoader(gameHandler));
    }

    /**
     * Registers a MapLoader.
     *
     * @param mapLoader the MapLoader to register
     */
    public static void registerMapLoader(MapLoader<?> mapLoader) {
        mapLoaders.put(mapLoader.getIdentifier(), mapLoader);
    }

    /**
     * Unregisters a MapLoader.
     *
     * @param identifier the identifier of the MapLoader to unregister
     */
    public static void unregisterMapLoader(String identifier) {
        mapLoaders.remove(identifier);
    }

    /**
     * Gets a MapLoader by its name.
     *
     * @param name the name of the MapLoader
     * @return the MapLoader
     */
    public static MapLoader<?> getMapLoader(String name) {
        return mapLoaders.get(name);
    }

    /**
     * Gets all registered MapLoaders.
     *
     * @return an unmodifiable map of all registered MapLoaders
     */
    public static Map<String, MapLoader<?>> getMapLoaders() {
        return Collections.unmodifiableMap(mapLoaders);
    }

    /**
     * Clears all registered MapLoaders.
     */
    public static void clearMapLoaders() {
        mapLoaders.clear();
    }

    /**
     * Gets the default MapLoader.
     *
     * @return by default the SlimeMapLoader if available, otherwise the BukkitMapLoader
     */
    public static MapLoader<?> getDefaultMapLoader() {
        if (mapLoaders.isEmpty()) {
            throw new IllegalStateException("No MapLoaders have been registered yet!");
        }

        if (mapLoaders.containsKey("slime")) return mapLoaders.get("slime");

        return mapLoaders.get("bukkit");
    }
}
