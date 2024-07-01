package tech.zmario.everything.api.scheduler;

import tech.zmario.everything.api.scheduler.objects.TaskInfo;

import java.util.HashMap;
import java.util.Map;

public abstract class Scheduler {

    private final Map<Integer, ScheduledTask> tasks = new HashMap<>();

    public abstract void schedule(TaskInfo taskInfo);

    public abstract void cancel(int id);

    public TaskInfo.Builder taskBuilder() {
        return TaskInfo.builder();
    }

    public Map<Integer, ScheduledTask> getTasks() {
        return tasks;
    }

    public ScheduledTask getTask(int id) {
        return tasks.get(id);
    }
}
