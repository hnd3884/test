package com.unboundid.util;

import java.io.Closeable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CloseableLock
{
    private final Lock lock;
    private final ReentrantLock reentrantLock;
    
    public CloseableLock() {
        this(false);
    }
    
    public CloseableLock(final boolean fair) {
        this.reentrantLock = new ReentrantLock(fair);
        this.lock = new Lock(this.reentrantLock);
    }
    
    public Lock lock() {
        this.reentrantLock.lock();
        return this.lock;
    }
    
    public Lock lockInterruptibly() throws InterruptedException {
        this.reentrantLock.lockInterruptibly();
        return this.lock;
    }
    
    public Lock tryLock(final long waitTime, final TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        if (waitTime <= 0L) {
            Validator.violation("CloseableLock.tryLock.waitTime must be greater than zero.  The provided value was " + waitTime);
        }
        if (this.reentrantLock.tryLock(waitTime, timeUnit)) {
            return this.lock;
        }
        throw new TimeoutException(UtilityMessages.ERR_CLOSEABLE_LOCK_TRY_LOCK_TIMEOUT.get(StaticUtils.millisToHumanReadableDuration(timeUnit.toMillis(waitTime))));
    }
    
    public boolean isFair() {
        return this.reentrantLock.isFair();
    }
    
    public boolean isLocked() {
        return this.reentrantLock.isLocked();
    }
    
    public boolean isHeldByCurrentThread() {
        return this.reentrantLock.isHeldByCurrentThread();
    }
    
    public int getHoldCount() {
        return this.reentrantLock.getHoldCount();
    }
    
    public boolean hasQueuedThreads() {
        return this.reentrantLock.hasQueuedThreads();
    }
    
    public boolean hasQueuedThread(final Thread thread) {
        Validator.ensureNotNull(thread);
        return this.reentrantLock.hasQueuedThread(thread);
    }
    
    public int getQueueLength() {
        return this.reentrantLock.getQueueLength();
    }
    
    @Override
    public String toString() {
        return "CloseableLock(lock=" + this.reentrantLock.toString() + ')';
    }
    
    public final class Lock implements Closeable
    {
        private final ReentrantLock lock;
        
        private Lock(final ReentrantLock lock) {
            this.lock = lock;
        }
        
        public void avoidCompilerWarning() {
        }
        
        @Override
        public void close() {
            this.lock.unlock();
        }
    }
}
