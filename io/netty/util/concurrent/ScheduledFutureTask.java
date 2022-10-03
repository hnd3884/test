package io.netty.util.concurrent;

import io.netty.util.internal.DefaultPriorityQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import io.netty.util.internal.PriorityQueueNode;

final class ScheduledFutureTask<V> extends PromiseTask<V> implements ScheduledFuture<V>, PriorityQueueNode
{
    private static final long START_TIME;
    private long id;
    private long deadlineNanos;
    private final long periodNanos;
    private int queueIndex;
    
    static long nanoTime() {
        return System.nanoTime() - ScheduledFutureTask.START_TIME;
    }
    
    static long deadlineNanos(final long delay) {
        final long deadlineNanos = nanoTime() + delay;
        return (deadlineNanos < 0L) ? Long.MAX_VALUE : deadlineNanos;
    }
    
    static long initialNanoTime() {
        return ScheduledFutureTask.START_TIME;
    }
    
    ScheduledFutureTask(final AbstractScheduledEventExecutor executor, final Runnable runnable, final long nanoTime) {
        super(executor, runnable);
        this.queueIndex = -1;
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0L;
    }
    
    ScheduledFutureTask(final AbstractScheduledEventExecutor executor, final Runnable runnable, final long nanoTime, final long period) {
        super(executor, runnable);
        this.queueIndex = -1;
        this.deadlineNanos = nanoTime;
        this.periodNanos = validatePeriod(period);
    }
    
    ScheduledFutureTask(final AbstractScheduledEventExecutor executor, final Callable<V> callable, final long nanoTime, final long period) {
        super(executor, callable);
        this.queueIndex = -1;
        this.deadlineNanos = nanoTime;
        this.periodNanos = validatePeriod(period);
    }
    
    ScheduledFutureTask(final AbstractScheduledEventExecutor executor, final Callable<V> callable, final long nanoTime) {
        super(executor, callable);
        this.queueIndex = -1;
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0L;
    }
    
    private static long validatePeriod(final long period) {
        if (period == 0L) {
            throw new IllegalArgumentException("period: 0 (expected: != 0)");
        }
        return period;
    }
    
    ScheduledFutureTask<V> setId(final long id) {
        if (this.id == 0L) {
            this.id = id;
        }
        return this;
    }
    
    @Override
    protected EventExecutor executor() {
        return super.executor();
    }
    
    public long deadlineNanos() {
        return this.deadlineNanos;
    }
    
    void setConsumed() {
        if (this.periodNanos == 0L) {
            assert nanoTime() >= this.deadlineNanos;
            this.deadlineNanos = 0L;
        }
    }
    
    public long delayNanos() {
        return deadlineToDelayNanos(this.deadlineNanos());
    }
    
    static long deadlineToDelayNanos(final long deadlineNanos) {
        return (deadlineNanos == 0L) ? 0L : Math.max(0L, deadlineNanos - nanoTime());
    }
    
    public long delayNanos(final long currentTimeNanos) {
        return (this.deadlineNanos == 0L) ? 0L : Math.max(0L, this.deadlineNanos() - (currentTimeNanos - ScheduledFutureTask.START_TIME));
    }
    
    @Override
    public long getDelay(final TimeUnit unit) {
        return unit.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
    }
    
    @Override
    public int compareTo(final Delayed o) {
        if (this == o) {
            return 0;
        }
        final ScheduledFutureTask<?> that = (ScheduledFutureTask<?>)o;
        final long d = this.deadlineNanos() - that.deadlineNanos();
        if (d < 0L) {
            return -1;
        }
        if (d > 0L) {
            return 1;
        }
        if (this.id < that.id) {
            return -1;
        }
        assert this.id != that.id;
        return 1;
    }
    
    @Override
    public void run() {
        assert this.executor().inEventLoop();
        try {
            if (this.delayNanos() > 0L) {
                if (this.isCancelled()) {
                    this.scheduledExecutor().scheduledTaskQueue().removeTyped(this);
                }
                else {
                    this.scheduledExecutor().scheduleFromEventLoop(this);
                }
                return;
            }
            if (this.periodNanos == 0L) {
                if (this.setUncancellableInternal()) {
                    final V result = this.runTask();
                    this.setSuccessInternal(result);
                }
            }
            else if (!this.isCancelled()) {
                this.runTask();
                if (!this.executor().isShutdown()) {
                    if (this.periodNanos > 0L) {
                        this.deadlineNanos += this.periodNanos;
                    }
                    else {
                        this.deadlineNanos = nanoTime() - this.periodNanos;
                    }
                    if (!this.isCancelled()) {
                        this.scheduledExecutor().scheduledTaskQueue().add(this);
                    }
                }
            }
        }
        catch (final Throwable cause) {
            this.setFailureInternal(cause);
        }
    }
    
    private AbstractScheduledEventExecutor scheduledExecutor() {
        return (AbstractScheduledEventExecutor)this.executor();
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        final boolean canceled = super.cancel(mayInterruptIfRunning);
        if (canceled) {
            this.scheduledExecutor().removeScheduled(this);
        }
        return canceled;
    }
    
    boolean cancelWithoutRemove(final boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }
    
    @Override
    protected StringBuilder toStringBuilder() {
        final StringBuilder buf = super.toStringBuilder();
        buf.setCharAt(buf.length() - 1, ',');
        return buf.append(" deadline: ").append(this.deadlineNanos).append(", period: ").append(this.periodNanos).append(')');
    }
    
    @Override
    public int priorityQueueIndex(final DefaultPriorityQueue<?> queue) {
        return this.queueIndex;
    }
    
    @Override
    public void priorityQueueIndex(final DefaultPriorityQueue<?> queue, final int i) {
        this.queueIndex = i;
    }
    
    static {
        START_TIME = System.nanoTime();
    }
}
