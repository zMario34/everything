package tech.zmario.everything.api.scheduler;

import tech.zmario.everything.api.scheduler.objects.TaskInfo;

public abstract class ScheduledTask {

    private final TaskInfo taskInfo;

    protected ScheduledTask(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public abstract void cancel();

}
