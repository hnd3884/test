package org.apache.lucene.store;

import java.io.IOException;
import org.apache.lucene.util.ThreadInterruptedException;

public final class SleepingLockWrapper extends FilterDirectory
{
    public static final long LOCK_OBTAIN_WAIT_FOREVER = -1L;
    public static long DEFAULT_POLL_INTERVAL;
    private final long lockWaitTimeout;
    private final long pollInterval;
    
    public SleepingLockWrapper(final Directory delegate, final long lockWaitTimeout) {
        this(delegate, lockWaitTimeout, SleepingLockWrapper.DEFAULT_POLL_INTERVAL);
    }
    
    public SleepingLockWrapper(final Directory delegate, final long lockWaitTimeout, final long pollInterval) {
        super(delegate);
        this.lockWaitTimeout = lockWaitTimeout;
        this.pollInterval = pollInterval;
        if (lockWaitTimeout < 0L && lockWaitTimeout != -1L) {
            throw new IllegalArgumentException("lockWaitTimeout should be LOCK_OBTAIN_WAIT_FOREVER or a non-negative number (got " + lockWaitTimeout + ")");
        }
        if (pollInterval < 0L) {
            throw new IllegalArgumentException("pollInterval must be a non-negative number (got " + pollInterval + ")");
        }
    }
    
    @Override
    public Lock obtainLock(final String lockName) throws IOException {
        LockObtainFailedException failureReason = null;
        final long maxSleepCount = this.lockWaitTimeout / this.pollInterval;
        long sleepCount = 0L;
        try {
            return this.in.obtainLock(lockName);
        }
        catch (final LockObtainFailedException failed) {
            if (failureReason == null) {
                failureReason = failed;
            }
            try {
                Thread.sleep(this.pollInterval);
            }
            catch (final InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
            }
            if (sleepCount++ >= maxSleepCount && this.lockWaitTimeout != -1L) {
                String reason = "Lock obtain timed out: " + this.toString();
                if (failureReason != null) {
                    reason = reason + ": " + failureReason;
                }
                throw new LockObtainFailedException(reason, failureReason);
            }
            return this.in.obtainLock(lockName);
        }
    }
    
    @Override
    public String toString() {
        return "SleepingLockWrapper(" + this.in + ")";
    }
    
    static {
        SleepingLockWrapper.DEFAULT_POLL_INTERVAL = 1000L;
    }
}
