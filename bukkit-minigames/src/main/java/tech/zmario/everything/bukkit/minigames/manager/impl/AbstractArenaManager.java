package tech.zmario.everything.bukkit.minigames.manager.impl;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.zmario.everything.bukkit.minigames.GameHandler;
import tech.zmario.everything.bukkit.minigames.arena.Arena;
import tech.zmario.everything.bukkit.minigames.arena.settings.ArenaSettings;
import tech.zmario.everything.bukkit.minigames.loader.MapLoader;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;
import tech.zmario.everything.bukkit.minigames.manager.ArenaLoader;
import tech.zmario.everything.bukkit.minigames.manager.ArenaManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;

public abstract class AbstractArenaManager implements ArenaManager {

    private final GameHandler gameHandler;
    private final ArenaLoader arenaLoader;
    private final MapLoader<?> mapLoader;

    private final Map<String, Arena> arenas = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<UUID, String> arenasByPlayer = new HashMap<>();

    public AbstractArenaManager(GameHandler gameHandler, MapLoader<?> mapLoader, ArenaLoader arenaLoader) {
        this.gameHandler = gameHandler;
        this.mapLoader = mapLoader;
        this.arenaLoader = arenaLoader;
    }

    /**
     * Method to load all arenas from the arenas folder
     *
     * @see ArenaLoader#loadSettings(File, TemplateMap)
     */
    public void loadArenas() {
        File folder = new File(gameHandler.getEverything().getPlugin().getDataFolder(), "arenas");

        if (!folder.exists()) folder.mkdirs();

        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith(".yml")) return;
            String name = file.getName().replace(".yml", "");

            try {
                TemplateMap<?> freshMap = mapLoader.loadMap(name, true);
                TemplateMap<?> clonedMap = mapLoader.cloneMap(freshMap);

                ArenaSettings arenaSettings = arenaLoader.loadSettings(file, freshMap);

                createArena(arenaSettings, clonedMap);
            } catch (NullPointerException ex) {
                gameHandler.getEverything().getLogger().log(Level.SEVERE,
                        String.format("Skipping arena %s due to an error", name), ex);
            }
        }
    }

    /**
     * Method to clone an arena from a template
     *
     * @param settings the settings to apply
     * @return the cloned arena
     */
    public @NotNull Arena cloneArena(@NotNull ArenaSettings settings) {
        Preconditions.checkNotNull(settings, "settings");
        TemplateMap<?> clonedMap = mapLoader.cloneMap(settings.getTemplateMap());

        return createArena(settings, clonedMap);
    }

    @ApiStatus.Internal
    public Arena createArena(ArenaSettings settings, TemplateMap<?> clonedMap) {
        Arena arena = arenaLoader.createArena(settings, clonedMap);

        arenas.put(arena.getIdentifier(), arena);

        return arena;
    }

    /**
     * Method to clone an arena multiple times
     *
     * @param settings the settings to clone
     * @param lobbies  the amount of games to create
     */
    public void scaleArena(@NotNull ArenaSettings settings, int lobbies) {
        Preconditions.checkNotNull(settings, "settings");

        for (int i = 0; i < lobbies; i++) cloneArena(settings);
    }

    /**
     * Method to get an arena by its name
     *
     * @param name the name of the arena
     * @return the arena
     */
    @Override
    public @Nullable Arena getArena(@NotNull String name) {
        return arenas.get(Preconditions.checkNotNull(name, "name"));
    }

    @Override
    public @Nullable Arena getArena(@NotNull Player player) {
        Preconditions.checkNotNull(player, "player");

        return getArena(arenasByPlayer.get(player.getUniqueId()));
    }

    /**
     * Method to get the map loader
     *
     * @return the map loader
     */
    @Override
    public @NotNull MapLoader<?> getMapLoader() {
        return mapLoader;
    }

    /**
     * Method to get the arena loader
     *
     * @return the arena loader
     */
    @Override
    public ArenaLoader getArenaLoader() {
        return arenaLoader;
    }

    public void disable() {
        arenas.values().forEach(Arena::disable);
        arenas.clear();
    }

    @Override
    public abstract void addPlayer(Player player, @NotNull Arena arena);

    public Map<UUID, String> getArenasByPlayer() {
        return arenasByPlayer;
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }
}
