package org.apache.lucene.index;

import java.util.concurrent.locks.ReentrantLock;
import java.util.Iterator;
import org.apache.lucene.util.ThreadInterruptedException;
import java.util.ArrayList;
import java.util.List;

final class DocumentsWriterPerThreadPool
{
    private final List<ThreadState> threadStates;
    private final List<ThreadState> freeList;
    private boolean aborted;
    
    DocumentsWriterPerThreadPool() {
        this.threadStates = new ArrayList<ThreadState>();
        this.freeList = new ArrayList<ThreadState>();
    }
    
    synchronized int getActiveThreadStateCount() {
        return this.threadStates.size();
    }
    
    synchronized void setAbort() {
        this.aborted = true;
    }
    
    synchronized void clearAbort() {
        this.aborted = false;
        this.notifyAll();
    }
    
    private synchronized ThreadState newThreadState() {
        while (this.aborted) {
            try {
                this.wait();
                continue;
            }
            catch (final InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
            }
            break;
        }
        final ThreadState threadState = new ThreadState(null);
        threadState.lock();
        this.threadStates.add(threadState);
        return threadState;
    }
    
    DocumentsWriterPerThread reset(final ThreadState threadState) {
        assert threadState.isHeldByCurrentThread();
        final DocumentsWriterPerThread dwpt = threadState.dwpt;
        threadState.reset();
        return dwpt;
    }
    
    void recycle(final DocumentsWriterPerThread dwpt) {
    }
    
    ThreadState getAndLock(final Thread requestingThread, final DocumentsWriter documentsWriter) {
        ThreadState threadState = null;
        synchronized (this) {
            if (this.freeList.isEmpty()) {
                return this.newThreadState();
            }
            threadState = this.freeList.remove(this.freeList.size() - 1);
            if (threadState.dwpt == null) {
                for (int i = 0; i < this.freeList.size(); ++i) {
                    final ThreadState ts = this.freeList.get(i);
                    if (ts.dwpt != null) {
                        this.freeList.set(i, threadState);
                        threadState = ts;
                        break;
                    }
                }
            }
        }
        threadState.lock();
        return threadState;
    }
    
    void release(final ThreadState state) {
        state.unlock();
        synchronized (this) {
            this.freeList.add(state);
            this.notifyAll();
        }
    }
    
    synchronized ThreadState getThreadState(final int ord) {
        return this.threadStates.get(ord);
    }
    
    synchronized int getMaxThreadStates() {
        return this.threadStates.size();
    }
    
    ThreadState minContendedThreadState() {
        ThreadState minThreadState = null;
        for (final ThreadState state : this.threadStates) {
            if (minThreadState == null || state.getQueueLength() < minThreadState.getQueueLength()) {
                minThreadState = state;
            }
        }
        return minThreadState;
    }
    
    static final class ThreadState extends ReentrantLock
    {
        DocumentsWriterPerThread dwpt;
        volatile boolean flushPending;
        long bytesUsed;
        
        ThreadState(final DocumentsWriterPerThread dpwt) {
            this.flushPending = false;
            this.bytesUsed = 0L;
            this.dwpt = dpwt;
        }
        
        private void reset() {
            assert this.isHeldByCurrentThread();
            this.dwpt = null;
            this.bytesUsed = 0L;
            this.flushPending = false;
        }
        
        boolean isInitialized() {
            assert this.isHeldByCurrentThread();
            return this.dwpt != null;
        }
        
        public long getBytesUsedPerThread() {
            assert this.isHeldByCurrentThread();
            return this.bytesUsed;
        }
        
        public DocumentsWriterPerThread getDocumentsWriterPerThread() {
            assert this.isHeldByCurrentThread();
            return this.dwpt;
        }
        
        public boolean isFlushPending() {
            return this.flushPending;
        }
    }
}
