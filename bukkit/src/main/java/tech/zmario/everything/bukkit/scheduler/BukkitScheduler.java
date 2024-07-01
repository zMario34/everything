package tech.zmario.everything.bukkit.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import tech.zmario.everything.api.scheduler.Scheduler;
import tech.zmario.everything.api.scheduler.objects.TaskInfo;
import tech.zmario.everything.bukkit.EverythingBukkit;

import java.util.function.BooleanSupplier;

public class BukkitScheduler extends Scheduler {

    private final EverythingBukkit everythingBukkit;

    private BukkitScheduler(EverythingBukkit everythingBukkit) {
        this.everythingBukkit = everythingBukkit;
    }

    public static BukkitScheduler create(EverythingBukkit plugin) {
        return new BukkitScheduler(plugin);
    }

    @Override
    public void schedule(TaskInfo taskInfo) {
        boolean timerTask = taskInfo.getPeriod() > 0;

        BukkitRunnable runnable = new BukkitRunnable() {
            private final long start = System.currentTimeMillis();
            private boolean paused = false;

            @Override
            public void run() {
                for (BooleanSupplier cancelSupplier : taskInfo.getCancelSuppliers()) {
                    if (cancelSupplier.getAsBoolean()) {
                        taskInfo.getCancelRunnable().run();
                        cancel();
                        return;
                    }
                }

                if (timerTask) {
                    for (BooleanSupplier pauseSupplier : taskInfo.getPauseSuppliers()) {
                        if (pauseSupplier.getAsBoolean()) {
                            if (!paused) {
                                taskInfo.getPauseRunnable().run();
                                paused = true;
                            }
                            return;
                        } else if (paused) paused = false;
                    }
                }

                if (taskInfo.getStopAfter() > 0 && System.currentTimeMillis() - start >= taskInfo.getStopAfter()) {
                    taskInfo.getCompletionRunnable().run();
                    cancel();
                    return;
                }

                if (timerTask) taskInfo.getTickRunnable().run();
                else taskInfo.getCompletionRunnable().run();
            }
        };

        if (timerTask) runnable.runTaskTimer(everythingBukkit.getPlugin(), taskInfo.getDelay(), taskInfo.getPeriod());
        else runnable.runTaskLater(everythingBukkit.getPlugin(), taskInfo.getDelay());

        getTasks().put(runnable.getTaskId(), new BukkitScheduledTask(taskInfo, runnable));
    }

    @Override
    public void cancel(int id) {
        getTask(id).cancel();
    }
}
