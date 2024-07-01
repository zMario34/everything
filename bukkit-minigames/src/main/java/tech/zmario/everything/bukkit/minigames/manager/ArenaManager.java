package tech.zmario.everything.bukkit.minigames.manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.zmario.everything.bukkit.minigames.arena.Arena;
import tech.zmario.everything.bukkit.minigames.arena.settings.ArenaSettings;
import tech.zmario.everything.bukkit.minigames.loader.MapLoader;

public interface ArenaManager {

    void loadArenas();

    Arena cloneArena(@NotNull ArenaSettings settings);

    void scaleArena(@NotNull ArenaSettings settings, int lobbies);

    @Nullable Arena getArena(@NotNull String name);

    @Nullable Arena getArena(@NotNull Player player);

    MapLoader<?> getMapLoader();

    ArenaLoader getArenaLoader();

    void addPlayer(Player player, Arena arena);

    void disable();

}
