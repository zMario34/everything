package tech.zmario.everything.bukkit.minigames.phases;

import tech.zmario.everything.bukkit.minigames.GameHandler;
import tech.zmario.everything.bukkit.minigames.arena.Arena;

import java.util.List;

public abstract class GamePhase {

    private final GameHandler gameHandler;

    public GamePhase(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public abstract void start(Arena arena);

    public abstract void end(Arena arena);

    public abstract GamePhase getNextPhase();

    public abstract List<String> getScoreboardLines(Arena arena);

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public void startNext(Arena arena) {
        GamePhase nextPhase = getNextPhase();

        if (nextPhase != null) arena.setCurrentPhase(nextPhase);
    }
}
