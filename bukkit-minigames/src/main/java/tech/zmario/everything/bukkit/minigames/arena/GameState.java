package tech.zmario.everything.bukkit.minigames.arena;

public enum GameState {

    WAITING,
    STARTING,
    PLAYING,
    ENDING;

    public boolean isLobby() {
        return this == WAITING || this == STARTING;
    }
}
