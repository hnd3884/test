package org.apache.lucene.index;

import org.apache.lucene.util.ThreadInterruptedException;
import org.apache.lucene.store.RateLimiter;

public class MergeRateLimiter extends RateLimiter
{
    private static final int MIN_PAUSE_CHECK_MSEC = 25;
    volatile long totalBytesWritten;
    double mbPerSec;
    private long lastNS;
    private long minPauseCheckBytes;
    private boolean abort;
    long totalPausedNS;
    long totalStoppedNS;
    final MergePolicy.OneMerge merge;
    
    public MergeRateLimiter(final MergePolicy.OneMerge merge) {
        this.merge = merge;
        this.setMBPerSec(Double.POSITIVE_INFINITY);
    }
    
    @Override
    public synchronized void setMBPerSec(final double mbPerSec) {
        if (mbPerSec < 0.0) {
            throw new IllegalArgumentException("mbPerSec must be positive; got: " + mbPerSec);
        }
        this.mbPerSec = mbPerSec;
        this.minPauseCheckBytes = Math.min(1048576L, (long)(0.025 * mbPerSec * 1024.0 * 1024.0));
        assert this.minPauseCheckBytes >= 0L;
        this.notify();
    }
    
    @Override
    public synchronized double getMBPerSec() {
        return this.mbPerSec;
    }
    
    public long getTotalBytesWritten() {
        return this.totalBytesWritten;
    }
    
    @Override
    public long pause(final long bytes) throws MergePolicy.MergeAbortedException {
        this.totalBytesWritten += bytes;
        long curNS;
        long startNS = curNS = System.nanoTime();
        long pausedNS = 0L;
        while (true) {
            final PauseResult result = this.maybePause(bytes, curNS);
            if (result == PauseResult.NO) {
                this.lastNS = curNS;
                return pausedNS;
            }
            curNS = System.nanoTime();
            final long ns = curNS - startNS;
            startNS = curNS;
            if (result == PauseResult.STOPPED) {
                this.totalStoppedNS += ns;
            }
            else {
                assert result == PauseResult.PAUSED;
                this.totalPausedNS += ns;
            }
            pausedNS += ns;
        }
    }
    
    public synchronized long getTotalStoppedNS() {
        return this.totalStoppedNS;
    }
    
    public synchronized long getTotalPausedNS() {
        return this.totalPausedNS;
    }
    
    private synchronized PauseResult maybePause(final long bytes, final long curNS) throws MergePolicy.MergeAbortedException {
        this.checkAbort();
        final double secondsToPause = bytes / 1024.0 / 1024.0 / this.mbPerSec;
        final long targetNS = this.lastNS + (long)(1.0E9 * secondsToPause);
        long curPauseNS = targetNS - curNS;
        if (curPauseNS <= 2000000L) {
            return PauseResult.NO;
        }
        if (curPauseNS > 250000000L) {
            curPauseNS = 250000000L;
        }
        final int sleepMS = (int)(curPauseNS / 1000000L);
        final int sleepNS = (int)(curPauseNS % 1000000L);
        final double rate = this.mbPerSec;
        try {
            this.wait(sleepMS, sleepNS);
        }
        catch (final InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
        if (rate == 0.0) {
            return PauseResult.STOPPED;
        }
        return PauseResult.PAUSED;
    }
    
    public synchronized void checkAbort() throws MergePolicy.MergeAbortedException {
        if (this.abort) {
            throw new MergePolicy.MergeAbortedException("merge is aborted: " + this.merge.segString());
        }
    }
    
    public synchronized void setAbort() {
        this.abort = true;
        this.notify();
    }
    
    public synchronized boolean getAbort() {
        return this.abort;
    }
    
    @Override
    public long getMinPauseCheckBytes() {
        return this.minPauseCheckBytes;
    }
    
    private enum PauseResult
    {
        NO, 
        STOPPED, 
        PAUSED;
    }
}
