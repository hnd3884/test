package org.apache.tomcat.util.threads;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.util.Collection;
import org.apache.tomcat.util.res.StringManager;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue extends LinkedBlockingQueue<Runnable>
{
    private static final long serialVersionUID = 1L;
    protected static final StringManager sm;
    private static final int DEFAULT_FORCED_REMAINING_CAPACITY = -1;
    private transient volatile ThreadPoolExecutor parent;
    private int forcedRemainingCapacity;
    
    public TaskQueue() {
        this.parent = null;
        this.forcedRemainingCapacity = -1;
    }
    
    public TaskQueue(final int capacity) {
        super(capacity);
        this.parent = null;
        this.forcedRemainingCapacity = -1;
    }
    
    public TaskQueue(final Collection<? extends Runnable> c) {
        super(c);
        this.parent = null;
        this.forcedRemainingCapacity = -1;
    }
    
    public void setParent(final ThreadPoolExecutor tp) {
        this.parent = tp;
    }
    
    public boolean force(final Runnable o) {
        if (this.parent == null || this.parent.isShutdown()) {
            throw new RejectedExecutionException(TaskQueue.sm.getString("taskQueue.notRunning"));
        }
        return super.offer(o);
    }
    
    @Deprecated
    public boolean force(final Runnable o, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (this.parent == null || this.parent.isShutdown()) {
            throw new RejectedExecutionException(TaskQueue.sm.getString("taskQueue.notRunning"));
        }
        return super.offer(o, timeout, unit);
    }
    
    @Override
    public boolean offer(final Runnable o) {
        if (this.parent == null) {
            return super.offer(o);
        }
        if (this.parent.getPoolSize() == this.parent.getMaximumPoolSize()) {
            return super.offer(o);
        }
        if (this.parent.getSubmittedCount() <= this.parent.getPoolSize()) {
            return super.offer(o);
        }
        return this.parent.getPoolSize() >= this.parent.getMaximumPoolSize() && super.offer(o);
    }
    
    @Override
    public Runnable poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        final Runnable runnable = super.poll(timeout, unit);
        if (runnable == null && this.parent != null) {
            this.parent.stopCurrentThreadIfNeeded();
        }
        return runnable;
    }
    
    @Override
    public Runnable take() throws InterruptedException {
        if (this.parent != null && this.parent.currentThreadShouldBeStopped()) {
            return this.poll(this.parent.getKeepAliveTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        }
        return super.take();
    }
    
    @Override
    public int remainingCapacity() {
        if (this.forcedRemainingCapacity > -1) {
            return this.forcedRemainingCapacity;
        }
        return super.remainingCapacity();
    }
    
    public void setForcedRemainingCapacity(final int forcedRemainingCapacity) {
        this.forcedRemainingCapacity = forcedRemainingCapacity;
    }
    
    void resetForcedRemainingCapacity() {
        this.forcedRemainingCapacity = -1;
    }
    
    static {
        sm = StringManager.getManager(TaskQueue.class);
    }
}
