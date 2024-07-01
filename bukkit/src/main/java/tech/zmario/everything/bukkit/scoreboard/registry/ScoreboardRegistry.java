package tech.zmario.everything.bukkit.scoreboard.registry;

import tech.zmario.everything.api.scheduler.objects.TaskInfo;
import tech.zmario.everything.bukkit.EverythingBukkit;
import tech.zmario.everything.bukkit.scoreboard.objects.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ScoreboardRegistry {

    private final Map<Scoreboard, Consumer<Scoreboard>> boards = new HashMap<>();

    public ScoreboardRegistry(EverythingBukkit library) {
        TaskInfo taskInfo = TaskInfo.builder()
                .delay(10)
                .period(10)
                .onTick(() -> boards.forEach((board, consumer) -> consumer.accept(board)))
                .build();

        library.getScheduler().schedule(taskInfo);
    }

    public static ScoreboardRegistry create(EverythingBukkit library) {
        return new ScoreboardRegistry(library);
    }

    public void registerUpdater(Scoreboard board, Consumer<Scoreboard> updater) {
        boards.put(board, updater);
    }

    public void unregisterBoard(Scoreboard board) {
        boards.remove(board);
    }
}
