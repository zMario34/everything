package tech.zmario.everything.bukkit.minigames.loader;

import org.jetbrains.annotations.Nullable;
import tech.zmario.everything.bukkit.minigames.GameHandler;
import tech.zmario.everything.bukkit.minigames.loader.factory.MapLoaderFactory;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;

import java.util.Map;
import java.util.TreeMap;

public abstract class MapLoader<T> {

    private final Map<String, TemplateMap<T>> loadedMaps = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final GameHandler gameHandler;

    public MapLoader(GameHandler gameHandler) {
        this.gameHandler = gameHandler;

        MapLoaderFactory.registerMapLoader(this);
    }

    public abstract @Nullable TemplateMap<T> loadMap(String name, boolean readOnly);

    public abstract boolean unloadMap(String name);

    public abstract TemplateMap<T> createMap(String name);

    public abstract TemplateMap<?> cloneMap(TemplateMap<?> template);

    public abstract boolean deleteMap(String name);

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public TemplateMap<T> getMap(String name) {
        return loadedMaps.get(name);
    }

    public Map<String, TemplateMap<T>> getLoadedMaps() {
        return loadedMaps;
    }

    public abstract String getIdentifier();
}
