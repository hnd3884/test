package org.apache.lucene.index;

import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import org.apache.lucene.util.ThreadInterruptedException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.util.InfoStream;
import java.util.IdentityHashMap;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.lucene.util.Accountable;

final class DocumentsWriterFlushControl implements Accountable
{
    private final long hardMaxBytesPerDWPT;
    private long activeBytes;
    private long flushBytes;
    private volatile int numPending;
    private int numDocsSinceStalled;
    final AtomicBoolean flushDeletes;
    private boolean fullFlush;
    private final Queue<DocumentsWriterPerThread> flushQueue;
    private final Queue<BlockedFlush> blockedFlushes;
    private final IdentityHashMap<DocumentsWriterPerThread, Long> flushingWriters;
    double maxConfiguredRamBuffer;
    long peakActiveBytes;
    long peakFlushBytes;
    long peakNetBytes;
    long peakDelta;
    boolean flushByRAMWasDisabled;
    final DocumentsWriterStallControl stallControl;
    private final DocumentsWriterPerThreadPool perThreadPool;
    private final FlushPolicy flushPolicy;
    private boolean closed;
    private final DocumentsWriter documentsWriter;
    private final LiveIndexWriterConfig config;
    private final BufferedUpdatesStream bufferedUpdatesStream;
    private final InfoStream infoStream;
    private final List<DocumentsWriterPerThread> fullFlushBuffer;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    DocumentsWriterFlushControl(final DocumentsWriter documentsWriter, final LiveIndexWriterConfig config, final BufferedUpdatesStream bufferedUpdatesStream) {
        this.activeBytes = 0L;
        this.flushBytes = 0L;
        this.numPending = 0;
        this.numDocsSinceStalled = 0;
        this.flushDeletes = new AtomicBoolean(false);
        this.fullFlush = false;
        this.flushQueue = new LinkedList<DocumentsWriterPerThread>();
        this.blockedFlushes = new LinkedList<BlockedFlush>();
        this.flushingWriters = new IdentityHashMap<DocumentsWriterPerThread, Long>();
        this.maxConfiguredRamBuffer = 0.0;
        this.peakActiveBytes = 0L;
        this.peakFlushBytes = 0L;
        this.peakNetBytes = 0L;
        this.peakDelta = 0L;
        this.closed = false;
        this.fullFlushBuffer = new ArrayList<DocumentsWriterPerThread>();
        this.infoStream = config.getInfoStream();
        this.stallControl = new DocumentsWriterStallControl(config);
        this.perThreadPool = documentsWriter.perThreadPool;
        this.flushPolicy = documentsWriter.flushPolicy;
        this.config = config;
        this.hardMaxBytesPerDWPT = config.getRAMPerThreadHardLimitMB() * 1024 * 1024;
        this.documentsWriter = documentsWriter;
        this.bufferedUpdatesStream = bufferedUpdatesStream;
    }
    
    public synchronized long activeBytes() {
        return this.activeBytes;
    }
    
    public synchronized long flushBytes() {
        return this.flushBytes;
    }
    
    public synchronized long netBytes() {
        return this.flushBytes + this.activeBytes;
    }
    
    private long stallLimitBytes() {
        final double maxRamMB = this.config.getRAMBufferSizeMB();
        return (maxRamMB != -1.0) ? ((long)(2.0 * (maxRamMB * 1024.0 * 1024.0))) : Long.MAX_VALUE;
    }
    
    private boolean assertMemory() {
        final double maxRamMB = this.config.getRAMBufferSizeMB();
        if (maxRamMB != -1.0 && !this.flushByRAMWasDisabled) {
            this.maxConfiguredRamBuffer = Math.max(maxRamMB, this.maxConfiguredRamBuffer);
            final long ram = this.flushBytes + this.activeBytes;
            final long ramBufferBytes = (long)(this.maxConfiguredRamBuffer * 1024.0 * 1024.0);
            final long expected = 2L * ramBufferBytes + (this.numPending + this.numFlushingDWPT() + this.numBlockedFlushes()) * this.peakDelta + this.numDocsSinceStalled * this.peakDelta;
            if (this.peakDelta < ramBufferBytes >> 1 && !DocumentsWriterFlushControl.$assertionsDisabled && ram > expected) {
                throw new AssertionError((Object)("actual mem: " + ram + " byte, expected mem: " + expected + " byte, flush mem: " + this.flushBytes + ", active mem: " + this.activeBytes + ", pending DWPT: " + this.numPending + ", flushing DWPT: " + this.numFlushingDWPT() + ", blocked DWPT: " + this.numBlockedFlushes() + ", peakDelta mem: " + this.peakDelta + " bytes, ramBufferBytes=" + ramBufferBytes + ", maxConfiguredRamBuffer=" + this.maxConfiguredRamBuffer));
            }
        }
        else {
            this.flushByRAMWasDisabled = true;
        }
        return true;
    }
    
    private void commitPerThreadBytes(final DocumentsWriterPerThreadPool.ThreadState perThread) {
        final long delta = perThread.dwpt.bytesUsed() - perThread.bytesUsed;
        perThread.bytesUsed += delta;
        if (perThread.flushPending) {
            this.flushBytes += delta;
        }
        else {
            this.activeBytes += delta;
        }
        assert this.updatePeaks(delta);
    }
    
    private boolean updatePeaks(final long delta) {
        this.peakActiveBytes = Math.max(this.peakActiveBytes, this.activeBytes);
        this.peakFlushBytes = Math.max(this.peakFlushBytes, this.flushBytes);
        this.peakNetBytes = Math.max(this.peakNetBytes, this.netBytes());
        this.peakDelta = Math.max(this.peakDelta, delta);
        return true;
    }
    
    synchronized DocumentsWriterPerThread doAfterDocument(final DocumentsWriterPerThreadPool.ThreadState perThread, final boolean isUpdate) {
        try {
            this.commitPerThreadBytes(perThread);
            if (!perThread.flushPending) {
                if (isUpdate) {
                    this.flushPolicy.onUpdate(this, perThread);
                }
                else {
                    this.flushPolicy.onInsert(this, perThread);
                }
                if (!perThread.flushPending && perThread.bytesUsed > this.hardMaxBytesPerDWPT) {
                    this.setFlushPending(perThread);
                }
            }
            DocumentsWriterPerThread flushingDWPT;
            if (this.fullFlush) {
                if (perThread.flushPending) {
                    this.checkoutAndBlock(perThread);
                    flushingDWPT = this.nextPendingFlush();
                }
                else {
                    flushingDWPT = null;
                }
            }
            else {
                flushingDWPT = this.tryCheckoutForFlush(perThread);
            }
            return flushingDWPT;
        }
        finally {
            final boolean stalled = this.updateStallState();
            assert this.assertNumDocsSinceStalled(stalled) && this.assertMemory();
        }
    }
    
    private boolean assertNumDocsSinceStalled(final boolean stalled) {
        if (stalled) {
            ++this.numDocsSinceStalled;
        }
        else {
            this.numDocsSinceStalled = 0;
        }
        return true;
    }
    
    synchronized void doAfterFlush(final DocumentsWriterPerThread dwpt) {
        assert this.flushingWriters.containsKey(dwpt);
        try {
            final Long bytes = this.flushingWriters.remove(dwpt);
            this.flushBytes -= bytes;
            this.perThreadPool.recycle(dwpt);
            assert this.assertMemory();
        }
        finally {
            try {
                this.updateStallState();
            }
            finally {
                this.notifyAll();
            }
        }
    }
    
    private boolean updateStallState() {
        assert Thread.holdsLock(this);
        final long limit = this.stallLimitBytes();
        final boolean stall = this.activeBytes + this.flushBytes > limit && this.activeBytes < limit && !this.closed;
        this.stallControl.updateStalled(stall);
        return stall;
    }
    
    public synchronized void waitForFlush() {
        while (this.flushingWriters.size() != 0) {
            try {
                this.wait();
                continue;
            }
            catch (final InterruptedException e) {
                throw new ThreadInterruptedException(e);
            }
            break;
        }
    }
    
    public synchronized void setFlushPending(final DocumentsWriterPerThreadPool.ThreadState perThread) {
        assert !perThread.flushPending;
        if (perThread.dwpt.getNumDocsInRAM() > 0) {
            perThread.flushPending = true;
            final long bytes = perThread.bytesUsed;
            this.flushBytes += bytes;
            this.activeBytes -= bytes;
            ++this.numPending;
            assert this.assertMemory();
        }
    }
    
    synchronized void doOnAbort(final DocumentsWriterPerThreadPool.ThreadState state) {
        try {
            if (state.flushPending) {
                this.flushBytes -= state.bytesUsed;
            }
            else {
                this.activeBytes -= state.bytesUsed;
            }
            assert this.assertMemory();
            this.perThreadPool.reset(state);
        }
        finally {
            this.updateStallState();
        }
    }
    
    synchronized DocumentsWriterPerThread tryCheckoutForFlush(final DocumentsWriterPerThreadPool.ThreadState perThread) {
        return perThread.flushPending ? this.internalTryCheckOutForFlush(perThread) : null;
    }
    
    private void checkoutAndBlock(final DocumentsWriterPerThreadPool.ThreadState perThread) {
        perThread.lock();
        try {
            assert perThread.flushPending : "can not block non-pending threadstate";
            assert this.fullFlush : "can not block if fullFlush == false";
            final long bytes = perThread.bytesUsed;
            final DocumentsWriterPerThread dwpt = this.perThreadPool.reset(perThread);
            --this.numPending;
            this.blockedFlushes.add(new BlockedFlush(dwpt, bytes));
        }
        finally {
            perThread.unlock();
        }
    }
    
    private DocumentsWriterPerThread internalTryCheckOutForFlush(final DocumentsWriterPerThreadPool.ThreadState perThread) {
        assert Thread.holdsLock(this);
        assert perThread.flushPending;
        try {
            if (perThread.tryLock()) {
                try {
                    if (perThread.isInitialized()) {
                        assert perThread.isHeldByCurrentThread();
                        final long bytes = perThread.bytesUsed;
                        final DocumentsWriterPerThread dwpt = this.perThreadPool.reset(perThread);
                        assert !this.flushingWriters.containsKey(dwpt) : "DWPT is already flushing";
                        this.flushingWriters.put(dwpt, bytes);
                        --this.numPending;
                        return dwpt;
                    }
                }
                finally {
                    perThread.unlock();
                }
            }
            return null;
        }
        finally {
            this.updateStallState();
        }
    }
    
    @Override
    public String toString() {
        return "DocumentsWriterFlushControl [activeBytes=" + this.activeBytes + ", flushBytes=" + this.flushBytes + "]";
    }
    
    DocumentsWriterPerThread nextPendingFlush() {
        final boolean fullFlush;
        final int numPending;
        synchronized (this) {
            final DocumentsWriterPerThread poll;
            if ((poll = this.flushQueue.poll()) != null) {
                this.updateStallState();
                return poll;
            }
            fullFlush = this.fullFlush;
            numPending = this.numPending;
        }
        if (numPending > 0 && !fullFlush) {
            for (int limit = this.perThreadPool.getActiveThreadStateCount(), i = 0; i < limit && numPending > 0; ++i) {
                final DocumentsWriterPerThreadPool.ThreadState next = this.perThreadPool.getThreadState(i);
                if (next.flushPending) {
                    final DocumentsWriterPerThread dwpt = this.tryCheckoutForFlush(next);
                    if (dwpt != null) {
                        return dwpt;
                    }
                }
            }
        }
        return null;
    }
    
    synchronized void setClosed() {
        this.closed = true;
    }
    
    public Iterator<DocumentsWriterPerThreadPool.ThreadState> allActiveThreadStates() {
        return this.getPerThreadsIterator(this.perThreadPool.getActiveThreadStateCount());
    }
    
    private Iterator<DocumentsWriterPerThreadPool.ThreadState> getPerThreadsIterator(final int upto) {
        return new Iterator<DocumentsWriterPerThreadPool.ThreadState>() {
            int i = 0;
            
            @Override
            public boolean hasNext() {
                return this.i < upto;
            }
            
            @Override
            public DocumentsWriterPerThreadPool.ThreadState next() {
                return DocumentsWriterFlushControl.this.perThreadPool.getThreadState(this.i++);
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported.");
            }
        };
    }
    
    synchronized void doOnDelete() {
        this.flushPolicy.onDelete(this, null);
    }
    
    public int getNumGlobalTermDeletes() {
        return this.documentsWriter.deleteQueue.numGlobalTermDeletes() + this.bufferedUpdatesStream.numTerms();
    }
    
    public long getDeleteBytesUsed() {
        return this.documentsWriter.deleteQueue.ramBytesUsed() + this.bufferedUpdatesStream.ramBytesUsed();
    }
    
    @Override
    public long ramBytesUsed() {
        return this.getDeleteBytesUsed() + this.netBytes();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    synchronized int numFlushingDWPT() {
        return this.flushingWriters.size();
    }
    
    public boolean getAndResetApplyAllDeletes() {
        return this.flushDeletes.getAndSet(false);
    }
    
    public void setApplyAllDeletes() {
        this.flushDeletes.set(true);
    }
    
    int numActiveDWPT() {
        return this.perThreadPool.getActiveThreadStateCount();
    }
    
    DocumentsWriterPerThreadPool.ThreadState obtainAndLock() {
        final DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getAndLock(Thread.currentThread(), this.documentsWriter);
        boolean success = false;
        try {
            if (perThread.isInitialized() && perThread.dwpt.deleteQueue != this.documentsWriter.deleteQueue) {
                this.addFlushableState(perThread);
            }
            success = true;
            return perThread;
        }
        finally {
            if (!success) {
                this.perThreadPool.release(perThread);
            }
        }
    }
    
    void markForFullFlush() {
        final DocumentsWriterDeleteQueue flushingQueue;
        synchronized (this) {
            assert !this.fullFlush : "called DWFC#markForFullFlush() while full flush is still running";
            assert this.fullFlushBuffer.isEmpty() : "full flush buffer should be empty: " + this.fullFlushBuffer;
            this.fullFlush = true;
            flushingQueue = this.documentsWriter.deleteQueue;
            final DocumentsWriterDeleteQueue newQueue = new DocumentsWriterDeleteQueue(flushingQueue.generation + 1L);
            this.documentsWriter.deleteQueue = newQueue;
        }
        for (int limit = this.perThreadPool.getActiveThreadStateCount(), i = 0; i < limit; ++i) {
            final DocumentsWriterPerThreadPool.ThreadState next = this.perThreadPool.getThreadState(i);
            next.lock();
            try {
                if (next.isInitialized()) {
                    assert next.dwpt.deleteQueue == this.documentsWriter.deleteQueue : " flushingQueue: " + flushingQueue + " currentqueue: " + this.documentsWriter.deleteQueue + " perThread queue: " + next.dwpt.deleteQueue + " numDocsInRam: " + next.dwpt.getNumDocsInRAM();
                    if (next.dwpt.deleteQueue == flushingQueue) {
                        this.addFlushableState(next);
                    }
                }
            }
            finally {
                next.unlock();
            }
        }
        synchronized (this) {
            this.pruneBlockedQueue(flushingQueue);
            assert this.assertBlockedFlushes(this.documentsWriter.deleteQueue);
            this.flushQueue.addAll((Collection<?>)this.fullFlushBuffer);
            this.fullFlushBuffer.clear();
            this.updateStallState();
        }
        assert this.assertActiveDeleteQueue(this.documentsWriter.deleteQueue);
    }
    
    private boolean assertActiveDeleteQueue(final DocumentsWriterDeleteQueue queue) {
        for (int limit = this.perThreadPool.getActiveThreadStateCount(), i = 0; i < limit; ++i) {
            final DocumentsWriterPerThreadPool.ThreadState next = this.perThreadPool.getThreadState(i);
            next.lock();
            try {
                assert next.dwpt.deleteQueue == queue : "isInitialized: " + next.isInitialized() + " numDocs: " + (next.isInitialized() ? next.dwpt.getNumDocsInRAM() : 0);
            }
            finally {
                next.unlock();
            }
        }
        return true;
    }
    
    void addFlushableState(final DocumentsWriterPerThreadPool.ThreadState perThread) {
        if (this.infoStream.isEnabled("DWFC")) {
            this.infoStream.message("DWFC", "addFlushableState " + perThread.dwpt);
        }
        final DocumentsWriterPerThread dwpt = perThread.dwpt;
        assert perThread.isHeldByCurrentThread();
        assert perThread.isInitialized();
        assert this.fullFlush;
        assert dwpt.deleteQueue != this.documentsWriter.deleteQueue;
        if (dwpt.getNumDocsInRAM() > 0) {
            synchronized (this) {
                if (!perThread.flushPending) {
                    this.setFlushPending(perThread);
                }
                final DocumentsWriterPerThread flushingDWPT = this.internalTryCheckOutForFlush(perThread);
                assert flushingDWPT != null : "DWPT must never be null here since we hold the lock and it holds documents";
                assert dwpt == flushingDWPT : "flushControl returned different DWPT";
                this.fullFlushBuffer.add(flushingDWPT);
            }
        }
        else {
            this.perThreadPool.reset(perThread);
        }
    }
    
    private void pruneBlockedQueue(final DocumentsWriterDeleteQueue flushingQueue) {
        final Iterator<BlockedFlush> iterator = this.blockedFlushes.iterator();
        while (iterator.hasNext()) {
            final BlockedFlush blockedFlush = iterator.next();
            if (blockedFlush.dwpt.deleteQueue == flushingQueue) {
                iterator.remove();
                assert !this.flushingWriters.containsKey(blockedFlush.dwpt) : "DWPT is already flushing";
                this.flushingWriters.put(blockedFlush.dwpt, blockedFlush.bytes);
                this.flushQueue.add(blockedFlush.dwpt);
            }
        }
    }
    
    synchronized void finishFullFlush() {
        assert this.fullFlush;
        assert this.flushQueue.isEmpty();
        assert this.flushingWriters.isEmpty();
        try {
            if (!this.blockedFlushes.isEmpty()) {
                assert this.assertBlockedFlushes(this.documentsWriter.deleteQueue);
                this.pruneBlockedQueue(this.documentsWriter.deleteQueue);
                assert this.blockedFlushes.isEmpty();
            }
        }
        finally {
            this.fullFlush = false;
            this.updateStallState();
        }
    }
    
    boolean assertBlockedFlushes(final DocumentsWriterDeleteQueue flushingQueue) {
        for (final BlockedFlush blockedFlush : this.blockedFlushes) {
            assert blockedFlush.dwpt.deleteQueue == flushingQueue;
        }
        return true;
    }
    
    synchronized void abortFullFlushes() {
        try {
            this.abortPendingFlushes();
        }
        finally {
            this.fullFlush = false;
        }
    }
    
    synchronized void abortPendingFlushes() {
        try {
            for (final DocumentsWriterPerThread dwpt : this.flushQueue) {
                try {
                    this.documentsWriter.subtractFlushedNumDocs(dwpt.getNumDocsInRAM());
                    dwpt.abort();
                }
                catch (final Throwable ex) {}
                finally {
                    this.doAfterFlush(dwpt);
                }
            }
            for (final BlockedFlush blockedFlush : this.blockedFlushes) {
                try {
                    this.flushingWriters.put(blockedFlush.dwpt, blockedFlush.bytes);
                    this.documentsWriter.subtractFlushedNumDocs(blockedFlush.dwpt.getNumDocsInRAM());
                    blockedFlush.dwpt.abort();
                }
                catch (final Throwable ex) {}
                finally {
                    this.doAfterFlush(blockedFlush.dwpt);
                }
            }
        }
        finally {
            this.flushQueue.clear();
            this.blockedFlushes.clear();
            this.updateStallState();
        }
    }
    
    synchronized boolean isFullFlush() {
        return this.fullFlush;
    }
    
    synchronized int numQueuedFlushes() {
        return this.flushQueue.size();
    }
    
    synchronized int numBlockedFlushes() {
        return this.blockedFlushes.size();
    }
    
    void waitIfStalled() {
        if (this.infoStream.isEnabled("DWFC")) {
            this.infoStream.message("DWFC", "waitIfStalled: numFlushesPending: " + this.flushQueue.size() + " netBytes: " + this.netBytes() + " flushBytes: " + this.flushBytes() + " fullFlush: " + this.fullFlush);
        }
        this.stallControl.waitIfStalled();
    }
    
    boolean anyStalledThreads() {
        return this.stallControl.anyStalledThreads();
    }
    
    public InfoStream getInfoStream() {
        return this.infoStream;
    }
    
    private static class BlockedFlush
    {
        final DocumentsWriterPerThread dwpt;
        final long bytes;
        
        BlockedFlush(final DocumentsWriterPerThread dwpt, final long bytes) {
            this.dwpt = dwpt;
            this.bytes = bytes;
        }
    }
}
