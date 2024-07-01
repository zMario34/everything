package tech.zmario.everything.api.scheduler.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class TaskInfo {

    private final int period;
    private final long stopAfter;
    private final Runnable tickRunnable;
    private final Runnable pauseRunnable;
    private final Runnable completionRunnable;
    private final Runnable cancelRunnable;
    private final List<BooleanSupplier> cancelSuppliers;
    private final List<BooleanSupplier> pauseSuppliers;
    private final int delay;

    private TaskInfo(int delay, int period, long stopAfter, Runnable tickRunnable, Runnable completionRunnable,
                     Runnable cancelRunnable, Runnable pauseRunnable, List<BooleanSupplier> cancelSuppliers,
                     List<BooleanSupplier> pauseSuppliers) {
        this.delay = delay;
        this.period = period;
        this.stopAfter = stopAfter;
        this.tickRunnable = tickRunnable;
        this.pauseRunnable = pauseRunnable;
        this.completionRunnable = completionRunnable;
        this.cancelRunnable = cancelRunnable;
        this.cancelSuppliers = cancelSuppliers;
        this.pauseSuppliers = pauseSuppliers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getDelay() {
        return delay;
    }

    public int getPeriod() {
        return period;
    }

    public long getStopAfter() {
        return stopAfter;
    }

    public List<BooleanSupplier> getCancelSuppliers() {
        return cancelSuppliers;
    }

    public List<BooleanSupplier> getPauseSuppliers() {
        return pauseSuppliers;
    }

    public Runnable getCancelRunnable() {
        return cancelRunnable;
    }

    public Runnable getCompletionRunnable() {
        return completionRunnable;
    }

    public Runnable getTickRunnable() {
        return tickRunnable;
    }

    public Runnable getPauseRunnable() {
        return pauseRunnable;
    }

    public static class Builder {

        private final List<BooleanSupplier> cancelSuppliers = new ArrayList<>();
        private final List<BooleanSupplier> pauseSuppliers = new ArrayList<>();

        private int delay = 20;
        private int period;
        private long stopAfter = 0;

        private Runnable tickRunnable = () -> {
        };
        private Runnable completionRunnable = () -> {
        };
        private Runnable pauseRunnable = () -> {
        };
        private Runnable cancelRunnable = () -> {
        };

        private Builder() {
        }

        public Builder delay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder period(int period) {
            this.period = period;
            return this;
        }

        public Builder stopAfter(long stopAfter, TimeUnit timeUnit) {
            this.stopAfter = timeUnit.toSeconds(stopAfter) * 20;
            return this;
        }

        public Builder onTick(Runnable tickRunnable) {
            this.tickRunnable = tickRunnable;
            return this;
        }

        public Builder onComplete(Runnable completionRunnable) {
            this.completionRunnable = completionRunnable;
            return this;
        }

        public Builder onPause(Runnable pauseRunnable) {
            this.pauseRunnable = pauseRunnable;
            return this;
        }

        public Builder onCancel(Runnable cancelRunnable) {
            this.cancelRunnable = cancelRunnable;
            return this;
        }

        public Builder cancelIf(BooleanSupplier cancelSupplier) {
            this.cancelSuppliers.add(cancelSupplier);
            return this;
        }

        public Builder pauseIf(BooleanSupplier pauseSupplier) {
            this.pauseSuppliers.add(pauseSupplier);
            return this;
        }

        public TaskInfo build() {
            return new TaskInfo(delay, period, stopAfter, tickRunnable, completionRunnable, cancelRunnable, pauseRunnable,
                    cancelSuppliers, pauseSuppliers);
        }
    }
}
