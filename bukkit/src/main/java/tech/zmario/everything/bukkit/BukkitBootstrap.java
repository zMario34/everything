package tech.zmario.everything.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the Bukkit implementation of Everything library.
 */
public class BukkitBootstrap extends JavaPlugin {

    private EverythingBukkit everythingBukkit;

    @Override
    public void onEnable() {
        everythingBukkit = new EverythingBukkit(this);
    }

    @Override
    public void onDisable() {
        everythingBukkit.disable();
    }
}
