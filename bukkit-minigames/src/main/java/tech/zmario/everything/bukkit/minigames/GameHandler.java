package tech.zmario.everything.bukkit.minigames;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tech.zmario.everything.api.configurations.ConfigurationProperties;
import tech.zmario.everything.bukkit.EverythingBukkit;
import tech.zmario.everything.bukkit.configurations.impl.BukkitYamlConfiguration;
import tech.zmario.everything.bukkit.minigames.loader.MapLoader;
import tech.zmario.everything.bukkit.minigames.loader.factory.MapLoaderFactory;
import tech.zmario.everything.bukkit.minigames.manager.ArenaLoader;
import tech.zmario.everything.bukkit.minigames.manager.ArenaManager;

import java.io.File;

public class GameHandler {

    private final EverythingBukkit everything;
    private final Plugin plugin;

    private BukkitYamlConfiguration language;
    private ArenaManager arenaManager;
    private ArenaLoader arenaLoader;

    public GameHandler(EverythingBukkit everything, @NotNull Plugin plugin) {
        this.everything = everything;
        this.plugin = plugin;
    }

    public static @NotNull GameHandler create(@NotNull Plugin plugin) {
        EverythingBukkit everything = Bukkit.getServicesManager().getRegistration(EverythingBukkit.class).getProvider();
        GameHandler gameHandler = new GameHandler(everything, plugin);

        MapLoaderFactory.initialize(gameHandler);
        return gameHandler;
    }

    public void initialize(@NotNull ArenaManager arenaManager,
                           @NotNull ArenaLoader arenaLoader,
                           @NotNull ConfigurationProperties configurationProperties) {
        if (MapLoaderFactory.getMapLoaders().isEmpty()) {
            throw new IllegalStateException("No MapLoaders were registered! Please register at least one MapLoader.");
        }

        this.arenaManager = arenaManager;
        this.arenaLoader = arenaLoader;

        language = BukkitYamlConfiguration.create(everything, new File(everything.getFolder(), "language.yml"),
                configurationProperties, null);

        arenaManager.loadArenas();
    }

    public void disable() {
        arenaManager.disable();
    }

    public EverythingBukkit getEverything() {
        return everything;
    }

    public Plugin getOwnerPlugin() {
        return plugin;
    }

    public BukkitYamlConfiguration getLanguage() {
        return language;
    }

    public ArenaLoader getArenaLoader() {
        return arenaLoader;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}
