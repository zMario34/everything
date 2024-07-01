package tech.zmario.everything.bukkit.minigames.arena.settings;

import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;

/**
 * Represents the settings of an arena.
 * <p>
 * The settings of an arena are used to create a new instance of an arena.
 */
public interface ArenaSettings {

    /**
     * Returns the friendly name of the arena.
     *
     * @return the display name of the arena
     */
    String getDisplayName();

    TemplateMap<?> getTemplateMap();

}
