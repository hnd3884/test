package com.unboundid.util;

import java.io.Closeable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CloseableReadWriteLock
{
    private final ReadLock readLock;
    private final ReentrantReadWriteLock readWriteLock;
    private final WriteLock writeLock;
    
    public CloseableReadWriteLock() {
        this(false);
    }
    
    public CloseableReadWriteLock(final boolean fair) {
        this.readWriteLock = new ReentrantReadWriteLock(fair);
        this.readLock = new ReadLock(this.readWriteLock.readLock());
        this.writeLock = new WriteLock(this.readWriteLock.writeLock());
    }
    
    public WriteLock lockWrite() {
        this.readWriteLock.writeLock().lock();
        return this.writeLock;
    }
    
    public WriteLock lockWriteInterruptibly() throws InterruptedException {
        this.readWriteLock.writeLock().lockInterruptibly();
        return this.writeLock;
    }
    
    public WriteLock tryLockWrite(final long waitTime, final TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        if (waitTime <= 0L) {
            Validator.violation("CloseableLock.tryLockWrite.waitTime must be greater than zero.  The provided value was " + waitTime);
        }
        if (this.readWriteLock.writeLock().tryLock(waitTime, timeUnit)) {
            return this.writeLock;
        }
        throw new TimeoutException(UtilityMessages.ERR_CLOSEABLE_RW_LOCK_TRY_LOCK_WRITE_TIMEOUT.get(StaticUtils.millisToHumanReadableDuration(timeUnit.toMillis(waitTime))));
    }
    
    public ReadLock lockRead() {
        this.readWriteLock.readLock().lock();
        return this.readLock;
    }
    
    public ReadLock lockReadInterruptibly() throws InterruptedException {
        this.readWriteLock.readLock().lockInterruptibly();
        return this.readLock;
    }
    
    public ReadLock tryLockRead(final long waitTime, final TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        if (waitTime <= 0L) {
            Validator.violation("CloseableLock.tryLockRead.waitTime must be greater than zero.  The provided value was " + waitTime);
        }
        if (this.readWriteLock.readLock().tryLock(waitTime, timeUnit)) {
            return this.readLock;
        }
        throw new TimeoutException(UtilityMessages.ERR_CLOSEABLE_RW_LOCK_TRY_LOCK_READ_TIMEOUT.get(StaticUtils.millisToHumanReadableDuration(timeUnit.toMillis(waitTime))));
    }
    
    public boolean isFair() {
        return this.readWriteLock.isFair();
    }
    
    public boolean isWriteLocked() {
        return this.readWriteLock.isWriteLocked();
    }
    
    public boolean isWriteLockedByCurrentThread() {
        return this.readWriteLock.isWriteLockedByCurrentThread();
    }
    
    public int getWriteHoldCount() {
        return this.readWriteLock.getWriteHoldCount();
    }
    
    public int getReadLockCount() {
        return this.readWriteLock.getReadLockCount();
    }
    
    public int getReadHoldCount() {
        return this.readWriteLock.getReadHoldCount();
    }
    
    public boolean hasQueuedThreads() {
        return this.readWriteLock.hasQueuedThreads();
    }
    
    public boolean hasQueuedThread(final Thread thread) {
        return this.readWriteLock.hasQueuedThread(thread);
    }
    
    public int getQueueLength() {
        return this.readWriteLock.getQueueLength();
    }
    
    @Override
    public String toString() {
        return "CloseableReadWriteLock(lock=" + this.readWriteLock.toString() + ')';
    }
    
    public final class ReadLock implements Closeable
    {
        private final ReentrantReadWriteLock.ReadLock lock;
        
        private ReadLock(final ReentrantReadWriteLock.ReadLock lock) {
            this.lock = lock;
        }
        
        public void avoidCompilerWarning() {
        }
        
        @Override
        public void close() {
            this.lock.unlock();
        }
    }
    
    public final class WriteLock implements Closeable
    {
        private final ReentrantReadWriteLock.WriteLock lock;
        
        private WriteLock(final ReentrantReadWriteLock.WriteLock lock) {
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
