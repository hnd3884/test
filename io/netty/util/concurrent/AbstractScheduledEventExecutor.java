package io.netty.util.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.Callable;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;
import java.util.Queue;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;

public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor
{
    private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR;
    static final Runnable WAKEUP_TASK;
    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
    long nextTaskId;
    
    protected AbstractScheduledEventExecutor() {
    }
    
    protected AbstractScheduledEventExecutor(final EventExecutorGroup parent) {
        super(parent);
    }
    
    protected static long nanoTime() {
        return ScheduledFutureTask.nanoTime();
    }
    
    protected static long deadlineToDelayNanos(final long deadlineNanos) {
        return ScheduledFutureTask.deadlineToDelayNanos(deadlineNanos);
    }
    
    protected static long initialNanoTime() {
        return ScheduledFutureTask.initialNanoTime();
    }
    
    PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
        if (this.scheduledTaskQueue == null) {
            this.scheduledTaskQueue = new DefaultPriorityQueue<ScheduledFutureTask<?>>(AbstractScheduledEventExecutor.SCHEDULED_FUTURE_TASK_COMPARATOR, 11);
        }
        return this.scheduledTaskQueue;
    }
    
    private static boolean isNullOrEmpty(final Queue<ScheduledFutureTask<?>> queue) {
        return queue == null || queue.isEmpty();
    }
    
    protected void cancelScheduledTasks() {
        assert this.inEventLoop();
        final PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        if (isNullOrEmpty(scheduledTaskQueue)) {
            return;
        }
        final ScheduledFutureTask[] array;
        final ScheduledFutureTask<?>[] scheduledTasks = array = scheduledTaskQueue.toArray(new ScheduledFutureTask[0]);
        for (final ScheduledFutureTask<?> task : array) {
            task.cancelWithoutRemove(false);
        }
        scheduledTaskQueue.clearIgnoringIndexes();
    }
    
    protected final Runnable pollScheduledTask() {
        return this.pollScheduledTask(nanoTime());
    }
    
    protected final Runnable pollScheduledTask(final long nanoTime) {
        assert this.inEventLoop();
        final ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null || scheduledTask.deadlineNanos() - nanoTime > 0L) {
            return null;
        }
        this.scheduledTaskQueue.remove();
        scheduledTask.setConsumed();
        return scheduledTask;
    }
    
    protected final long nextScheduledTaskNano() {
        final ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        return (scheduledTask != null) ? scheduledTask.delayNanos() : -1L;
    }
    
    protected final long nextScheduledTaskDeadlineNanos() {
        final ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        return (scheduledTask != null) ? scheduledTask.deadlineNanos() : -1L;
    }
    
    final ScheduledFutureTask<?> peekScheduledTask() {
        final Queue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue;
        return (scheduledTaskQueue != null) ? scheduledTaskQueue.peek() : null;
    }
    
    protected final boolean hasScheduledTasks() {
        final ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        return scheduledTask != null && scheduledTask.deadlineNanos() <= nanoTime();
    }
    
    @Override
    public ScheduledFuture<?> schedule(final Runnable command, long delay, final TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (delay < 0L) {
            delay = 0L;
        }
        this.validateScheduled0(delay, unit);
        return this.schedule((ScheduledFutureTask<?>)new ScheduledFutureTask<Object>(this, command, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
    }
    
    @Override
    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, long delay, final TimeUnit unit) {
        ObjectUtil.checkNotNull(callable, "callable");
        ObjectUtil.checkNotNull(unit, "unit");
        if (delay < 0L) {
            delay = 0L;
        }
        this.validateScheduled0(delay, unit);
        return this.schedule(new ScheduledFutureTask<V>(this, callable, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (period <= 0L) {
            throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", period));
        }
        this.validateScheduled0(initialDelay, unit);
        this.validateScheduled0(period, unit);
        return this.schedule((ScheduledFutureTask<?>)new ScheduledFutureTask<Object>(this, command, ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period)));
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay, final TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");
        if (initialDelay < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (delay <= 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", delay));
        }
        this.validateScheduled0(initialDelay, unit);
        this.validateScheduled0(delay, unit);
        return this.schedule((ScheduledFutureTask<?>)new ScheduledFutureTask<Object>(this, command, ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay)));
    }
    
    private void validateScheduled0(final long amount, final TimeUnit unit) {
        this.validateScheduled(amount, unit);
    }
    
    @Deprecated
    protected void validateScheduled(final long amount, final TimeUnit unit) {
    }
    
    final void scheduleFromEventLoop(final ScheduledFutureTask<?> task) {
        final PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue = this.scheduledTaskQueue();
        final long n = this.nextTaskId + 1L;
        this.nextTaskId = n;
        scheduledTaskQueue.add(task.setId(n));
    }
    
    private <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> task) {
        if (this.inEventLoop()) {
            this.scheduleFromEventLoop(task);
        }
        else {
            final long deadlineNanos = task.deadlineNanos();
            if (this.beforeScheduledTaskSubmitted(deadlineNanos)) {
                this.execute(task);
            }
            else {
                this.lazyExecute(task);
                if (this.afterScheduledTaskSubmitted(deadlineNanos)) {
                    this.execute(AbstractScheduledEventExecutor.WAKEUP_TASK);
                }
            }
        }
        return task;
    }
    
    final void removeScheduled(final ScheduledFutureTask<?> task) {
        assert task.isCancelled();
        if (this.inEventLoop()) {
            this.scheduledTaskQueue().removeTyped(task);
        }
        else {
            this.lazyExecute(task);
        }
    }
    
    protected boolean beforeScheduledTaskSubmitted(final long deadlineNanos) {
        return true;
    }
    
    protected boolean afterScheduledTaskSubmitted(final long deadlineNanos) {
        return true;
    }
    
    static {
        SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>() {
            @Override
            public int compare(final ScheduledFutureTask<?> o1, final ScheduledFutureTask<?> o2) {
                return o1.compareTo(o2);
            }
        };
        WAKEUP_TASK = new Runnable() {
            @Override
            public void run() {
            }
        };
    }
}
