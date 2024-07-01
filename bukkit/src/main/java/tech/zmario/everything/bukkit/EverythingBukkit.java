package tech.zmario.everything.bukkit;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.handler.EverythingCommandHandler;
import tech.zmario.everything.api.manager.AddonManager;
import tech.zmario.everything.api.manager.CooldownsManager;
import tech.zmario.everything.api.scheduler.Scheduler;
import tech.zmario.everything.bukkit.interactiveitem.data.InteractiveItemRegistry;
import tech.zmario.everything.bukkit.scheduler.BukkitScheduler;
import tech.zmario.everything.bukkit.scoreboard.registry.ScoreboardRegistry;
import tech.zmario.everything.bukkit.utils.MessageUtils;
import tech.zmario.everything.bukkit.utils.Utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public final class EverythingBukkit implements EverythingLibrary {

    private static EverythingBukkit instance;

    private final JavaPlugin plugin;
    private final LibraryManager libraryManager;
    private final BukkitScheduler scheduler;
    private final InteractiveItemRegistry interactiveItemRegistry;
    private final ScoreboardRegistry scoreboardRegistry;
    private final BukkitAudiences audiences;
    private final AddonManager addonManager;
    private final BukkitCommandHandler commandHandler;
    private final CooldownsManager cooldownsManager;

    public EverythingBukkit(JavaPlugin plugin) {
        if (!plugin.getName().equals("everything")) {
            if (instance == null) {
                getServer().getLogger().log(Level.WARNING, "Everything plugin is being initialized as another plugin (%s). " +
                                                           "This may cause issues with other plugins using the Everything library.",
                        plugin.getName());
            } else {
                getServer().getLogger().severe("Everything library already initialized! Will not initialize again.");
            }
        } else instance = this;

        this.plugin = plugin;

        libraryManager = new BukkitLibraryManager(plugin);
        addonManager = AddonManager.create(this);
        interactiveItemRegistry = InteractiveItemRegistry.create();
        scoreboardRegistry = ScoreboardRegistry.create(this);
        audiences = BukkitAudiences.create(plugin);
        scheduler = BukkitScheduler.create(this);
        commandHandler = BukkitCommandHandler.create(plugin);
        cooldownsManager = CooldownsManager.create();

        new EverythingCommandHandler(this);

        if (Utils.BUKKIT_VERSION < 14 && !plugin.getServer().getPluginManager().isPluginEnabled("NBTAPI")) {
            libraryManager.addRepository("https://repo.codemc.org/repository/maven-public/");
            libraryManager.loadLibrary(Library.builder()
                    .groupId("de.tr7zw")
                    .artifactId("item-nbt-api-plugin")
                    .version("2.12.2")
                    .build());

            getLogger().info("NBTAPI has been loaded to support versions below 1.14.");
        }

        interactiveItemRegistry.init(this);
        addonManager.loadAddons();

        MessageUtils.bukkitAudiences = audiences;

        registerService(EverythingLibrary.class, this);

        getLogger().info("Everything plugin has been enabled! This plugin is only a library and does not " +
                         "provide any commands or features.");
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public void disable() {
        interactiveItemRegistry.unregisterAll();
        addonManager.disableAddons();

        getLogger().info("Everything library has been disabled!");
    }

    @Override
    public <T> void registerService(Class<T> clazz, T service) {
        plugin.getServer().getServicesManager().register(clazz, service, plugin, ServicePriority.Normal);
    }

    @Override
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public AudienceProvider getAudiences() {
        return audiences;
    }

    @Override
    public File getFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public BukkitCommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public CooldownsManager getCooldownsManager() {
        return cooldownsManager;
    }

    public InteractiveItemRegistry getInteractiveItemRegistry() {
        return interactiveItemRegistry;
    }

    public ScoreboardRegistry getScoreboardRegistry() {
        return scoreboardRegistry;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Get the instance of the EverythingBukkit library.
     *
     * @throws IllegalStateException if the library is not initialized
     * @return the instance of the EverythingBukkit library
     */
    public static EverythingBukkit get() {
        if (instance == null)
            throw new IllegalStateException("EverythingBukkit is not initialized! Please install the plugin or initialize it manually.");

        return instance;
    }
}
