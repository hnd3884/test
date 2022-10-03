package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;
import java.util.Deque;
import org.apache.tomcat.dbcp.pool2.TrackedUse;
import org.apache.tomcat.dbcp.pool2.PooledObjectState;
import org.apache.tomcat.dbcp.pool2.PooledObject;

public class DefaultPooledObject<T> implements PooledObject<T>
{
    private final T object;
    private PooledObjectState state;
    private final long createTimeMillis;
    private volatile long lastBorrowTimeMillis;
    private volatile long lastUseTimeMillis;
    private volatile long lastReturnTimeMillis;
    private volatile boolean logAbandoned;
    private volatile CallStack borrowedBy;
    private volatile CallStack usedBy;
    private volatile long borrowedCount;
    
    public DefaultPooledObject(final T object) {
        this.state = PooledObjectState.IDLE;
        this.createTimeMillis = System.currentTimeMillis();
        this.lastBorrowTimeMillis = this.createTimeMillis;
        this.lastUseTimeMillis = this.createTimeMillis;
        this.lastReturnTimeMillis = this.createTimeMillis;
        this.logAbandoned = false;
        this.borrowedBy = NoOpCallStack.INSTANCE;
        this.usedBy = NoOpCallStack.INSTANCE;
        this.borrowedCount = 0L;
        this.object = object;
    }
    
    @Override
    public T getObject() {
        return this.object;
    }
    
    @Override
    public long getCreateTime() {
        return this.createTimeMillis;
    }
    
    @Override
    public long getActiveTimeMillis() {
        final long rTime = this.lastReturnTimeMillis;
        final long bTime = this.lastBorrowTimeMillis;
        if (rTime > bTime) {
            return rTime - bTime;
        }
        return System.currentTimeMillis() - bTime;
    }
    
    @Override
    public long getIdleTimeMillis() {
        final long elapsed = System.currentTimeMillis() - this.lastReturnTimeMillis;
        return (elapsed >= 0L) ? elapsed : 0L;
    }
    
    @Override
    public long getLastBorrowTime() {
        return this.lastBorrowTimeMillis;
    }
    
    @Override
    public long getLastReturnTime() {
        return this.lastReturnTimeMillis;
    }
    
    @Override
    public long getBorrowedCount() {
        return this.borrowedCount;
    }
    
    @Override
    public long getLastUsedTime() {
        if (this.object instanceof TrackedUse) {
            return Math.max(((TrackedUse)this.object).getLastUsed(), this.lastUseTimeMillis);
        }
        return this.lastUseTimeMillis;
    }
    
    @Override
    public int compareTo(final PooledObject<T> other) {
        final long lastActiveDiff = this.getLastReturnTime() - other.getLastReturnTime();
        if (lastActiveDiff == 0L) {
            return System.identityHashCode(this) - System.identityHashCode(other);
        }
        return (int)Math.min(Math.max(lastActiveDiff, -2147483648L), 2147483647L);
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("Object: ");
        result.append(this.object.toString());
        result.append(", State: ");
        synchronized (this) {
            result.append(this.state.toString());
        }
        return result.toString();
    }
    
    @Override
    public synchronized boolean startEvictionTest() {
        if (this.state == PooledObjectState.IDLE) {
            this.state = PooledObjectState.EVICTION;
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized boolean endEvictionTest(final Deque<PooledObject<T>> idleQueue) {
        if (this.state == PooledObjectState.EVICTION) {
            this.state = PooledObjectState.IDLE;
            return true;
        }
        if (this.state == PooledObjectState.EVICTION_RETURN_TO_HEAD) {
            this.state = PooledObjectState.IDLE;
            if (!idleQueue.offerFirst(this)) {}
        }
        return false;
    }
    
    @Override
    public synchronized boolean allocate() {
        if (this.state == PooledObjectState.IDLE) {
            this.state = PooledObjectState.ALLOCATED;
            this.lastBorrowTimeMillis = System.currentTimeMillis();
            this.lastUseTimeMillis = this.lastBorrowTimeMillis;
            ++this.borrowedCount;
            if (this.logAbandoned) {
                this.borrowedBy.fillInStackTrace();
            }
            return true;
        }
        if (this.state == PooledObjectState.EVICTION) {
            this.state = PooledObjectState.EVICTION_RETURN_TO_HEAD;
            return false;
        }
        return false;
    }
    
    @Override
    public synchronized boolean deallocate() {
        if (this.state == PooledObjectState.ALLOCATED || this.state == PooledObjectState.RETURNING) {
            this.state = PooledObjectState.IDLE;
            this.lastReturnTimeMillis = System.currentTimeMillis();
            this.borrowedBy.clear();
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized void invalidate() {
        this.state = PooledObjectState.INVALID;
    }
    
    @Override
    public void use() {
        this.lastUseTimeMillis = System.currentTimeMillis();
        this.usedBy.fillInStackTrace();
    }
    
    @Override
    public void printStackTrace(final PrintWriter writer) {
        boolean written = this.borrowedBy.printStackTrace(writer);
        written |= this.usedBy.printStackTrace(writer);
        if (written) {
            writer.flush();
        }
    }
    
    @Override
    public synchronized PooledObjectState getState() {
        return this.state;
    }
    
    @Override
    public synchronized void markAbandoned() {
        this.state = PooledObjectState.ABANDONED;
    }
    
    @Override
    public synchronized void markReturning() {
        this.state = PooledObjectState.RETURNING;
    }
    
    @Override
    public void setLogAbandoned(final boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }
    
    @Override
    public void setRequireFullStackTrace(final boolean requireFullStackTrace) {
        this.borrowedBy = CallStackUtils.newCallStack("'Pooled object created' yyyy-MM-dd HH:mm:ss Z 'by the following code has not been returned to the pool:'", true, requireFullStackTrace);
        this.usedBy = CallStackUtils.newCallStack("The last code to use this object was:", false, requireFullStackTrace);
    }
}
