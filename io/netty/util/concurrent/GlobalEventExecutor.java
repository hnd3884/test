package io.netty.util.concurrent;

import java.util.Queue;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;
import io.netty.util.internal.ThreadExecutorMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import io.netty.util.internal.logging.InternalLogger;

public final class GlobalEventExecutor extends AbstractScheduledEventExecutor implements OrderedEventExecutor
{
    private static final InternalLogger logger;
    private static final long SCHEDULE_QUIET_PERIOD_INTERVAL;
    public static final GlobalEventExecutor INSTANCE;
    final BlockingQueue<Runnable> taskQueue;
    final ScheduledFutureTask<Void> quietPeriodTask;
    final ThreadFactory threadFactory;
    private final TaskRunner taskRunner;
    private final AtomicBoolean started;
    volatile Thread thread;
    private final Future<?> terminationFuture;
    
    private GlobalEventExecutor() {
        this.taskQueue = new LinkedBlockingQueue<Runnable>();
        this.quietPeriodTask = new ScheduledFutureTask<Void>(this, Executors.callable(new Runnable() {
            @Override
            public void run() {
            }
        }, (Void)null), ScheduledFutureTask.deadlineNanos(GlobalEventExecutor.SCHEDULE_QUIET_PERIOD_INTERVAL), -GlobalEventExecutor.SCHEDULE_QUIET_PERIOD_INTERVAL);
        this.taskRunner = new TaskRunner();
        this.started = new AtomicBoolean();
        this.terminationFuture = new FailedFuture<Object>(this, new UnsupportedOperationException());
        this.scheduledTaskQueue().add(this.quietPeriodTask);
        this.threadFactory = ThreadExecutorMap.apply(new DefaultThreadFactory(DefaultThreadFactory.toPoolName(this.getClass()), false, 5, null), this);
    }
    
    Runnable takeTask() {
        final BlockingQueue<Runnable> taskQueue = this.taskQueue;
        while (true) {
            final ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
            if (scheduledTask == null) {
                Runnable task = null;
                try {
                    task = taskQueue.take();
                }
                catch (final InterruptedException ex) {}
                return task;
            }
            final long delayNanos = scheduledTask.delayNanos();
            Runnable task2 = null;
            if (delayNanos > 0L) {
                try {
                    task2 = taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
                }
                catch (final InterruptedException e) {
                    return null;
                }
            }
            if (task2 == null) {
                this.fetchFromScheduledTaskQueue();
                task2 = taskQueue.poll();
            }
            if (task2 != null) {
                return task2;
            }
        }
    }
    
    private void fetchFromScheduledTaskQueue() {
        final long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        for (Runnable scheduledTask = this.pollScheduledTask(nanoTime); scheduledTask != null; scheduledTask = this.pollScheduledTask(nanoTime)) {
            this.taskQueue.add(scheduledTask);
        }
    }
    
    public int pendingTasks() {
        return this.taskQueue.size();
    }
    
    private void addTask(final Runnable task) {
        this.taskQueue.add(ObjectUtil.checkNotNull(task, "task"));
    }
    
    @Override
    public boolean inEventLoop(final Thread thread) {
        return thread == this.thread;
    }
    
    @Override
    public Future<?> shutdownGracefully(final long quietPeriod, final long timeout, final TimeUnit unit) {
        return this.terminationFuture();
    }
    
    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }
    
    @Deprecated
    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isShuttingDown() {
        return false;
    }
    
    @Override
    public boolean isShutdown() {
        return false;
    }
    
    @Override
    public boolean isTerminated() {
        return false;
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) {
        return false;
    }
    
    public boolean awaitInactivity(final long timeout, final TimeUnit unit) throws InterruptedException {
        ObjectUtil.checkNotNull(unit, "unit");
        final Thread thread = this.thread;
        if (thread == null) {
            throw new IllegalStateException("thread was not started");
        }
        thread.join(unit.toMillis(timeout));
        return !thread.isAlive();
    }
    
    @Override
    public void execute(final Runnable task) {
        this.addTask(ObjectUtil.checkNotNull(task, "task"));
        if (!this.inEventLoop()) {
            this.startThread();
        }
    }
    
    private void startThread() {
        if (this.started.compareAndSet(false, true)) {
            final Thread t = this.threadFactory.newThread(this.taskRunner);
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    t.setContextClassLoader(null);
                    return null;
                }
            });
            (this.thread = t).start();
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(GlobalEventExecutor.class);
        SCHEDULE_QUIET_PERIOD_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
        INSTANCE = new GlobalEventExecutor();
    }
    
    final class TaskRunner implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                final Runnable task = GlobalEventExecutor.this.takeTask();
                if (task != null) {
                    try {
                        task.run();
                    }
                    catch (final Throwable t) {
                        GlobalEventExecutor.logger.warn("Unexpected exception from the global event executor: ", t);
                    }
                    if (task != GlobalEventExecutor.this.quietPeriodTask) {
                        continue;
                    }
                }
                final Queue<ScheduledFutureTask<?>> scheduledTaskQueue = GlobalEventExecutor.this.scheduledTaskQueue;
                if (GlobalEventExecutor.this.taskQueue.isEmpty() && (scheduledTaskQueue == null || scheduledTaskQueue.size() == 1)) {
                    final boolean stopped = GlobalEventExecutor.this.started.compareAndSet(true, false);
                    assert stopped;
                    if (GlobalEventExecutor.this.taskQueue.isEmpty()) {
                        break;
                    }
                    if (!GlobalEventExecutor.this.started.compareAndSet(false, true)) {
                        break;
                    }
                    continue;
                }
            }
        }
    }
}
