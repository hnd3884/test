package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.ThreadInterruptedException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.index.TrackingIndexWriter;
import java.io.Closeable;

public class ControlledRealTimeReopenThread<T> extends Thread implements Closeable
{
    private final ReferenceManager<T> manager;
    private final long targetMaxStaleNS;
    private final long targetMinStaleNS;
    private final TrackingIndexWriter writer;
    private volatile boolean finish;
    private volatile long waitingGen;
    private volatile long searchingGen;
    private long refreshStartGen;
    private final ReentrantLock reopenLock;
    private final Condition reopenCond;
    
    public ControlledRealTimeReopenThread(final TrackingIndexWriter writer, final ReferenceManager<T> manager, final double targetMaxStaleSec, final double targetMinStaleSec) {
        this.reopenLock = new ReentrantLock();
        this.reopenCond = this.reopenLock.newCondition();
        if (targetMaxStaleSec < targetMinStaleSec) {
            throw new IllegalArgumentException("targetMaxScaleSec (= " + targetMaxStaleSec + ") < targetMinStaleSec (=" + targetMinStaleSec + ")");
        }
        this.writer = writer;
        this.manager = manager;
        this.targetMaxStaleNS = (long)(1.0E9 * targetMaxStaleSec);
        this.targetMinStaleNS = (long)(1.0E9 * targetMinStaleSec);
        manager.addListener(new HandleRefresh());
    }
    
    private synchronized void refreshDone() {
        this.searchingGen = this.refreshStartGen;
        this.notifyAll();
    }
    
    @Override
    public synchronized void close() {
        this.finish = true;
        this.reopenLock.lock();
        try {
            this.reopenCond.signal();
        }
        finally {
            this.reopenLock.unlock();
        }
        try {
            this.join();
        }
        catch (final InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
        this.searchingGen = Long.MAX_VALUE;
        this.notifyAll();
    }
    
    public void waitForGeneration(final long targetGen) throws InterruptedException {
        this.waitForGeneration(targetGen, -1);
    }
    
    public synchronized boolean waitForGeneration(final long targetGen, final int maxMS) throws InterruptedException {
        final long curGen = this.writer.getGeneration();
        if (targetGen > curGen) {
            throw new IllegalArgumentException("targetGen=" + targetGen + " was never returned by the ReferenceManager instance (current gen=" + curGen + ")");
        }
        if (targetGen > this.searchingGen) {
            this.reopenLock.lock();
            this.waitingGen = Math.max(this.waitingGen, targetGen);
            try {
                this.reopenCond.signal();
            }
            finally {
                this.reopenLock.unlock();
            }
            final long startMS = System.nanoTime() / 1000000L;
            while (targetGen > this.searchingGen) {
                if (maxMS < 0) {
                    this.wait();
                }
                else {
                    final long msLeft = startMS + maxMS - System.nanoTime() / 1000000L;
                    if (msLeft <= 0L) {
                        return false;
                    }
                    this.wait(msLeft);
                }
            }
        }
        return true;
    }
    
    @Override
    public void run() {
        long lastReopenStartNS = System.nanoTime();
        while (!this.finish) {
            while (!this.finish) {
                this.reopenLock.lock();
                try {
                    final boolean hasWaiting = this.waitingGen > this.searchingGen;
                    final long nextReopenStartNS = lastReopenStartNS + (hasWaiting ? this.targetMinStaleNS : this.targetMaxStaleNS);
                    final long sleepNS = nextReopenStartNS - System.nanoTime();
                    if (sleepNS <= 0L) {
                        break;
                    }
                    this.reopenCond.awaitNanos(sleepNS);
                }
                catch (final InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                finally {
                    this.reopenLock.unlock();
                }
            }
            if (this.finish) {
                break;
            }
            lastReopenStartNS = System.nanoTime();
            this.refreshStartGen = this.writer.getAndIncrementGeneration();
            try {
                this.manager.maybeRefreshBlocking();
                continue;
            }
            catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
            break;
        }
    }
    
    public long getSearchingGen() {
        return this.searchingGen;
    }
    
    private class HandleRefresh implements ReferenceManager.RefreshListener
    {
        @Override
        public void beforeRefresh() {
        }
        
        @Override
        public void afterRefresh(final boolean didRefresh) {
            ControlledRealTimeReopenThread.this.refreshDone();
        }
    }
}
