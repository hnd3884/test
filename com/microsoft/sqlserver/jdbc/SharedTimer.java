package com.microsoft.sqlserver.jdbc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.io.Serializable;

class SharedTimer implements Serializable
{
    private static final long serialVersionUID = -4069361613863955760L;
    static final String CORE_THREAD_PREFIX = "mssql-jdbc-shared-timer-core-";
    private static final AtomicLong CORE_THREAD_COUNTER;
    private static final Object lock;
    private final long id;
    private final AtomicInteger refCount;
    private static volatile SharedTimer instance;
    private ScheduledThreadPoolExecutor executor;
    
    private SharedTimer() {
        this.id = SharedTimer.CORE_THREAD_COUNTER.getAndIncrement();
        this.refCount = new AtomicInteger();
        (this.executor = new ScheduledThreadPoolExecutor(1, task -> {
            new Thread(task, "mssql-jdbc-shared-timer-core-" + this.id);
            final Thread thread;
            final Thread t = thread;
            t.setDaemon(true);
            return t;
        })).setRemoveOnCancelPolicy(true);
    }
    
    public long getId() {
        return this.id;
    }
    
    static boolean isRunning() {
        return SharedTimer.instance != null;
    }
    
    public void removeRef() {
        synchronized (SharedTimer.lock) {
            if (this.refCount.get() <= 0) {
                throw new IllegalStateException("removeRef() called more than actual references");
            }
            if (this.refCount.decrementAndGet() == 0) {
                this.executor.shutdownNow();
                this.executor = null;
                SharedTimer.instance = null;
            }
        }
    }
    
    public static SharedTimer getTimer() {
        synchronized (SharedTimer.lock) {
            if (SharedTimer.instance == null) {
                SharedTimer.instance = new SharedTimer();
            }
            SharedTimer.instance.refCount.getAndIncrement();
            return SharedTimer.instance;
        }
    }
    
    public ScheduledFuture<?> schedule(final TDSTimeoutTask task, final long delaySeconds) {
        return this.schedule(task, delaySeconds, TimeUnit.SECONDS);
    }
    
    public ScheduledFuture<?> schedule(final TDSTimeoutTask task, final long delay, final TimeUnit unit) {
        if (this.executor == null) {
            throw new IllegalStateException("Cannot schedule tasks after shutdown");
        }
        return this.executor.schedule(task, delay, unit);
    }
    
    static {
        CORE_THREAD_COUNTER = new AtomicLong();
        lock = new Object();
    }
}
