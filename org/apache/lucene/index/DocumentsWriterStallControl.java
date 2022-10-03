package org.apache.lucene.index;

import org.apache.lucene.util.ThreadInterruptedException;
import java.util.IdentityHashMap;
import org.apache.lucene.util.InfoStream;
import java.util.Map;

final class DocumentsWriterStallControl
{
    private volatile boolean stalled;
    private int numWaiting;
    private boolean wasStalled;
    private final Map<Thread, Boolean> waiting;
    private final InfoStream infoStream;
    long stallStartNS;
    
    DocumentsWriterStallControl(final LiveIndexWriterConfig iwc) {
        this.waiting = new IdentityHashMap<Thread, Boolean>();
        this.infoStream = iwc.getInfoStream();
    }
    
    synchronized void updateStalled(final boolean stalled) {
        this.stalled = stalled;
        if (stalled) {
            this.wasStalled = true;
        }
        this.notifyAll();
    }
    
    void waitIfStalled() {
        if (this.stalled) {
            synchronized (this) {
                if (this.stalled) {
                    try {
                        this.incWaiters();
                        this.wait(1000L);
                        this.decrWaiters();
                    }
                    catch (final InterruptedException e) {
                        throw new ThreadInterruptedException(e);
                    }
                }
            }
        }
    }
    
    boolean anyStalledThreads() {
        return this.stalled;
    }
    
    private void incWaiters() {
        this.stallStartNS = System.nanoTime();
        if (this.infoStream.isEnabled("DW") && this.numWaiting == 0) {
            this.infoStream.message("DW", "now stalling flushes");
        }
        ++this.numWaiting;
        assert this.waiting.put(Thread.currentThread(), Boolean.TRUE) == null;
        assert this.numWaiting > 0;
    }
    
    private void decrWaiters() {
        --this.numWaiting;
        assert this.waiting.remove(Thread.currentThread()) != null;
        assert this.numWaiting >= 0;
        if (this.infoStream.isEnabled("DW") && this.numWaiting == 0) {
            final long stallEndNS = System.nanoTime();
            this.infoStream.message("DW", "done stalling flushes for " + (stallEndNS - this.stallStartNS) / 1000000.0 + " ms");
        }
    }
    
    synchronized boolean hasBlocked() {
        return this.numWaiting > 0;
    }
    
    boolean isHealthy() {
        return !this.stalled;
    }
    
    synchronized boolean isThreadQueued(final Thread t) {
        return this.waiting.containsKey(t);
    }
    
    synchronized boolean wasStalled() {
        return this.wasStalled;
    }
}
