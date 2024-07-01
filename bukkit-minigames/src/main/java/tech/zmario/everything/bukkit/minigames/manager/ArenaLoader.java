package tech.zmario.everything.bukkit.minigames.manager;

import tech.zmario.everything.bukkit.minigames.arena.Arena;
import tech.zmario.everything.bukkit.minigames.arena.settings.ArenaSettings;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;

import java.io.File;

public interface ArenaLoader {

    ArenaSettings loadSettings(File file, TemplateMap<?> map);

    Arena createArena(ArenaSettings settings, TemplateMap<?> map);

}
