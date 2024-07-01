package tech.zmario.everything.bukkit.scoreboard.objects;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import tech.zmario.everything.bukkit.EverythingBukkit;
import tech.zmario.everything.bukkit.utils.Utils;

import java.util.Arrays;
import java.util.function.Consumer;

public class Scoreboard {

    private final BPlayerBoard board;

    private Scoreboard(Player player, String title) {
        Netherboard netherboard = Netherboard.instance();

        board = netherboard.createBoard(player, Utils.colorize(title));
    }

    public static Scoreboard create(Player player, String title) {
        return new Scoreboard(player, title);
    }

    public Scoreboard setTitle(String title) {
        board.setName(Utils.colorize(title));
        return this;
    }

    public Scoreboard setLine(int index, String text) {
        board.set(Utils.colorize(text), index);
        return this;
    }

    public Scoreboard removeLine(int index) {
        board.remove(index);
        return this;
    }

    public String getLine(int index) {
        return board.get(index);
    }

    public Scoreboard setLines(String... lines) {
        board.setAll(Arrays.stream(lines).map(Utils::colorize).toArray(String[]::new));
        return this;
    }

    public void delete() {
        board.delete();
    }

    @ApiStatus.Internal
    public void registerUpdater(Consumer<Scoreboard> updater, EverythingBukkit everything) {
        everything.getScoreboardRegistry().registerUpdater(this, updater);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String title = "";
        private String[] lines;
        private Consumer<Scoreboard> updater;

        private Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder lines(String... lines) {
            this.lines = lines;
            return this;
        }

        public Builder updater(Consumer<Scoreboard> updater) {
            this.updater = updater;
            return this;
        }

        public Scoreboard register(Player player, EverythingBukkit everything) {
            Scoreboard scoreboard = Scoreboard.create(player, title);

            if (lines != null) scoreboard.setLines(lines);
            if (updater != null) scoreboard.registerUpdater(updater, everything);

            return scoreboard;
        }
    }
}
