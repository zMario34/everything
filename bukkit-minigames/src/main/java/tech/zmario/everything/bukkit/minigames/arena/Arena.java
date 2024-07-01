package tech.zmario.everything.bukkit.minigames.arena;

import org.bukkit.entity.Player;
import tech.zmario.everything.bukkit.minigames.arena.settings.ArenaSettings;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;
import tech.zmario.everything.bukkit.minigames.phases.GamePhase;

public interface Arena {

    String getIdentifier();

    TemplateMap<?> getCurrentMap();

    ArenaSettings getSettings();

    GamePhase getCurrentPhase();

    void setCurrentPhase(GamePhase phase);

    boolean addPlayer(Player player);

    int getPlayers();

    int getMaxPlayers();

    int getPlayersPerTeam();

    void disable();

}
