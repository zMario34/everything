package tech.zmario.everything.bukkit.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import tech.zmario.everything.api.scheduler.ScheduledTask;
import tech.zmario.everything.api.scheduler.objects.TaskInfo;

public class BukkitScheduledTask extends ScheduledTask {

    private final BukkitRunnable task;
    private final TaskInfo taskInfo;

    public BukkitScheduledTask(TaskInfo taskInfo, BukkitRunnable task) {
        super(taskInfo);
        this.task = task;
        this.taskInfo = taskInfo;
    }

    @Override
    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
