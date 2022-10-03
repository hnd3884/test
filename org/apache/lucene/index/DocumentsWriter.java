package org.apache.lucene.index;

import java.util.Collections;
import java.util.Locale;
import java.util.Collection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.AlreadyClosedException;
import java.io.IOException;
import org.apache.lucene.search.Query;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;

final class DocumentsWriter implements Closeable, Accountable
{
    private final Directory directoryOrig;
    private final Directory directory;
    private volatile boolean closed;
    private final InfoStream infoStream;
    private final LiveIndexWriterConfig config;
    private final AtomicInteger numDocsInRAM;
    volatile DocumentsWriterDeleteQueue deleteQueue;
    private final DocumentsWriterFlushQueue ticketQueue;
    private volatile boolean pendingChangesInCurrentFullFlush;
    final DocumentsWriterPerThreadPool perThreadPool;
    final FlushPolicy flushPolicy;
    final DocumentsWriterFlushControl flushControl;
    private final IndexWriter writer;
    private final Queue<IndexWriter.Event> events;
    private volatile DocumentsWriterDeleteQueue currentFullFlushDelQueue;
    
    DocumentsWriter(final IndexWriter writer, final LiveIndexWriterConfig config, final Directory directoryOrig, final Directory directory) {
        this.numDocsInRAM = new AtomicInteger(0);
        this.deleteQueue = new DocumentsWriterDeleteQueue();
        this.ticketQueue = new DocumentsWriterFlushQueue();
        this.currentFullFlushDelQueue = null;
        this.directoryOrig = directoryOrig;
        this.directory = directory;
        this.config = config;
        this.infoStream = config.getInfoStream();
        this.perThreadPool = config.getIndexerThreadPool();
        this.flushPolicy = config.getFlushPolicy();
        this.writer = writer;
        this.events = new ConcurrentLinkedQueue<IndexWriter.Event>();
        this.flushControl = new DocumentsWriterFlushControl(this, config, writer.bufferedUpdatesStream);
    }
    
    synchronized boolean deleteQueries(final Query... queries) throws IOException {
        final DocumentsWriterDeleteQueue deleteQueue = this.deleteQueue;
        deleteQueue.addDelete(queries);
        this.flushControl.doOnDelete();
        return this.applyAllDeletes(deleteQueue);
    }
    
    synchronized boolean deleteTerms(final Term... terms) throws IOException {
        final DocumentsWriterDeleteQueue deleteQueue = this.deleteQueue;
        deleteQueue.addDelete(terms);
        this.flushControl.doOnDelete();
        return this.applyAllDeletes(deleteQueue);
    }
    
    synchronized boolean updateDocValues(final DocValuesUpdate... updates) throws IOException {
        final DocumentsWriterDeleteQueue deleteQueue = this.deleteQueue;
        deleteQueue.addDocValuesUpdates(updates);
        this.flushControl.doOnDelete();
        return this.applyAllDeletes(deleteQueue);
    }
    
    DocumentsWriterDeleteQueue currentDeleteSession() {
        return this.deleteQueue;
    }
    
    private boolean applyAllDeletes(final DocumentsWriterDeleteQueue deleteQueue) throws IOException {
        if (this.flushControl.getAndResetApplyAllDeletes()) {
            if (deleteQueue != null && !this.flushControl.isFullFlush()) {
                this.ticketQueue.addDeletes(deleteQueue);
            }
            this.putEvent(ApplyDeletesEvent.INSTANCE);
            return true;
        }
        return false;
    }
    
    int purgeBuffer(final IndexWriter writer, final boolean forced) throws IOException {
        if (forced) {
            return this.ticketQueue.forcePurge(writer);
        }
        return this.ticketQueue.tryPurge(writer);
    }
    
    int getNumDocs() {
        return this.numDocsInRAM.get();
    }
    
    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this IndexWriter is closed");
        }
    }
    
    synchronized void abort(final IndexWriter writer) {
        assert !Thread.holdsLock(writer) : "IndexWriter lock should never be hold when aborting";
        boolean success = false;
        try {
            this.deleteQueue.clear();
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "abort");
            }
            for (int limit = this.perThreadPool.getActiveThreadStateCount(), i = 0; i < limit; ++i) {
                final DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getThreadState(i);
                perThread.lock();
                try {
                    this.abortThreadState(perThread);
                }
                finally {
                    perThread.unlock();
                }
            }
            this.flushControl.abortPendingFlushes();
            this.flushControl.waitForFlush();
            success = true;
        }
        finally {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "done abort success=" + success);
            }
        }
    }
    
    synchronized long lockAndAbortAll(final IndexWriter indexWriter) {
        assert indexWriter.holdsFullFlushLock();
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "lockAndAbortAll");
        }
        long abortedDocCount = 0L;
        boolean success = false;
        try {
            this.deleteQueue.clear();
            final int limit = this.perThreadPool.getMaxThreadStates();
            this.perThreadPool.setAbort();
            for (int i = 0; i < limit; ++i) {
                final DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getThreadState(i);
                perThread.lock();
                abortedDocCount += this.abortThreadState(perThread);
            }
            this.deleteQueue.clear();
            this.flushControl.abortPendingFlushes();
            this.flushControl.waitForFlush();
            success = true;
            return abortedDocCount;
        }
        finally {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "finished lockAndAbortAll success=" + success);
            }
            if (!success) {
                this.unlockAllAfterAbortAll(indexWriter);
            }
        }
    }
    
    private int abortThreadState(final DocumentsWriterPerThreadPool.ThreadState perThread) {
        assert perThread.isHeldByCurrentThread();
        if (perThread.isInitialized()) {
            try {
                final int abortedDocCount = perThread.dwpt.getNumDocsInRAM();
                this.subtractFlushedNumDocs(abortedDocCount);
                perThread.dwpt.abort();
                return abortedDocCount;
            }
            finally {
                this.flushControl.doOnAbort(perThread);
            }
        }
        this.flushControl.doOnAbort(perThread);
        return 0;
    }
    
    synchronized void unlockAllAfterAbortAll(final IndexWriter indexWriter) {
        assert indexWriter.holdsFullFlushLock();
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "unlockAll");
        }
        final int limit = this.perThreadPool.getMaxThreadStates();
        this.perThreadPool.clearAbort();
        for (int i = 0; i < limit; ++i) {
            try {
                final DocumentsWriterPerThreadPool.ThreadState perThread = this.perThreadPool.getThreadState(i);
                if (perThread.isHeldByCurrentThread()) {
                    perThread.unlock();
                }
            }
            catch (final Throwable e) {
                if (this.infoStream.isEnabled("DW")) {
                    this.infoStream.message("DW", "unlockAll: could not unlock state: " + i + " msg:" + e.getMessage());
                }
            }
        }
    }
    
    boolean anyChanges() {
        final boolean anyChanges = this.numDocsInRAM.get() != 0 || this.anyDeletions() || this.ticketQueue.hasTickets() || this.pendingChangesInCurrentFullFlush;
        if (this.infoStream.isEnabled("DW") && anyChanges) {
            this.infoStream.message("DW", "anyChanges? numDocsInRam=" + this.numDocsInRAM.get() + " deletes=" + this.anyDeletions() + " hasTickets:" + this.ticketQueue.hasTickets() + " pendingChangesInFullFlush: " + this.pendingChangesInCurrentFullFlush);
        }
        return anyChanges;
    }
    
    public int getBufferedDeleteTermsSize() {
        return this.deleteQueue.getBufferedUpdatesTermsSize();
    }
    
    public int getNumBufferedDeleteTerms() {
        return this.deleteQueue.numGlobalTermDeletes();
    }
    
    public boolean anyDeletions() {
        return this.deleteQueue.anyChanges();
    }
    
    @Override
    public void close() {
        this.closed = true;
        this.flushControl.setClosed();
    }
    
    private boolean preUpdate() throws IOException, AbortingException {
        this.ensureOpen();
        boolean hasEvents = false;
        if (this.flushControl.anyStalledThreads() || this.flushControl.numQueuedFlushes() > 0) {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "DocumentsWriter has queued dwpt; will hijack this thread to flush pending segment(s)");
            }
            while (true) {
                final DocumentsWriterPerThread flushingDWPT;
                if ((flushingDWPT = this.flushControl.nextPendingFlush()) != null) {
                    hasEvents |= this.doFlush(flushingDWPT);
                }
                else {
                    if (this.infoStream.isEnabled("DW") && this.flushControl.anyStalledThreads()) {
                        this.infoStream.message("DW", "WARNING DocumentsWriter has stalled threads; waiting");
                    }
                    this.flushControl.waitIfStalled();
                    if (this.flushControl.numQueuedFlushes() == 0) {
                        break;
                    }
                    continue;
                }
            }
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", "continue indexing after helping out flushing DocumentsWriter is healthy");
            }
        }
        return hasEvents;
    }
    
    private boolean postUpdate(final DocumentsWriterPerThread flushingDWPT, boolean hasEvents) throws IOException, AbortingException {
        hasEvents |= this.applyAllDeletes(this.deleteQueue);
        if (flushingDWPT != null) {
            hasEvents |= this.doFlush(flushingDWPT);
        }
        else {
            final DocumentsWriterPerThread nextPendingFlush = this.flushControl.nextPendingFlush();
            if (nextPendingFlush != null) {
                hasEvents |= this.doFlush(nextPendingFlush);
            }
        }
        return hasEvents;
    }
    
    private void ensureInitialized(final DocumentsWriterPerThreadPool.ThreadState state) throws IOException {
        if (state.dwpt == null) {
            final FieldInfos.Builder infos = new FieldInfos.Builder(this.writer.globalFieldNumberMap);
            state.dwpt = new DocumentsWriterPerThread(this.writer, this.writer.newSegmentName(), this.directoryOrig, this.directory, this.config, this.infoStream, this.deleteQueue, infos, this.writer.pendingNumDocs, this.writer.enableTestPoints);
        }
    }
    
    boolean updateDocuments(final Iterable<? extends Iterable<? extends IndexableField>> docs, final Analyzer analyzer, final Term delTerm) throws IOException, AbortingException {
        final boolean hasEvents = this.preUpdate();
        final DocumentsWriterPerThreadPool.ThreadState perThread = this.flushControl.obtainAndLock();
        DocumentsWriterPerThread flushingDWPT;
        try {
            this.ensureOpen();
            this.ensureInitialized(perThread);
            assert perThread.isInitialized();
            final DocumentsWriterPerThread dwpt = perThread.dwpt;
            final int dwptNumDocs = dwpt.getNumDocsInRAM();
            try {
                dwpt.updateDocuments(docs, analyzer, delTerm);
            }
            catch (final AbortingException ae) {
                this.flushControl.doOnAbort(perThread);
                dwpt.abort();
                throw ae;
            }
            finally {
                this.numDocsInRAM.addAndGet(dwpt.getNumDocsInRAM() - dwptNumDocs);
            }
            final boolean isUpdate = delTerm != null;
            flushingDWPT = this.flushControl.doAfterDocument(perThread, isUpdate);
        }
        finally {
            this.perThreadPool.release(perThread);
        }
        return this.postUpdate(flushingDWPT, hasEvents);
    }
    
    boolean updateDocument(final Iterable<? extends IndexableField> doc, final Analyzer analyzer, final Term delTerm) throws IOException, AbortingException {
        final boolean hasEvents = this.preUpdate();
        final DocumentsWriterPerThreadPool.ThreadState perThread = this.flushControl.obtainAndLock();
        DocumentsWriterPerThread flushingDWPT;
        try {
            this.ensureOpen();
            this.ensureInitialized(perThread);
            assert perThread.isInitialized();
            final DocumentsWriterPerThread dwpt = perThread.dwpt;
            final int dwptNumDocs = dwpt.getNumDocsInRAM();
            try {
                dwpt.updateDocument(doc, analyzer, delTerm);
            }
            catch (final AbortingException ae) {
                this.flushControl.doOnAbort(perThread);
                dwpt.abort();
                throw ae;
            }
            finally {
                this.numDocsInRAM.addAndGet(dwpt.getNumDocsInRAM() - dwptNumDocs);
            }
            final boolean isUpdate = delTerm != null;
            flushingDWPT = this.flushControl.doAfterDocument(perThread, isUpdate);
        }
        finally {
            this.perThreadPool.release(perThread);
        }
        return this.postUpdate(flushingDWPT, hasEvents);
    }
    
    private boolean doFlush(DocumentsWriterPerThread flushingDWPT) throws IOException, AbortingException {
        boolean hasEvents = false;
        while (flushingDWPT != null) {
            hasEvents = true;
            boolean success = false;
            DocumentsWriterFlushQueue.SegmentFlushTicket ticket = null;
            try {
                assert flushingDWPT.deleteQueue == this.currentFullFlushDelQueue : "expected: " + this.currentFullFlushDelQueue + "but was: " + flushingDWPT.deleteQueue + " " + this.flushControl.isFullFlush();
                try {
                    ticket = this.ticketQueue.addFlushTicket(flushingDWPT);
                    final int flushingDocsInRam = flushingDWPT.getNumDocsInRAM();
                    boolean dwptSuccess = false;
                    try {
                        final DocumentsWriterPerThread.FlushedSegment newSegment = flushingDWPT.flush();
                        this.ticketQueue.addSegment(ticket, newSegment);
                        dwptSuccess = true;
                    }
                    finally {
                        this.subtractFlushedNumDocs(flushingDocsInRam);
                        if (!flushingDWPT.pendingFilesToDelete().isEmpty()) {
                            this.putEvent(new DeleteNewFilesEvent(flushingDWPT.pendingFilesToDelete()));
                            hasEvents = true;
                        }
                        if (!dwptSuccess) {
                            this.putEvent(new FlushFailedEvent(flushingDWPT.getSegmentInfo()));
                            hasEvents = true;
                        }
                    }
                    success = true;
                }
                finally {
                    if (!success && ticket != null) {
                        this.ticketQueue.markTicketFailed(ticket);
                    }
                }
                if (this.ticketQueue.getTicketCount() >= this.perThreadPool.getActiveThreadStateCount()) {
                    this.putEvent(ForcedPurgeEvent.INSTANCE);
                    break;
                }
            }
            finally {
                this.flushControl.doAfterFlush(flushingDWPT);
            }
            flushingDWPT = this.flushControl.nextPendingFlush();
        }
        if (hasEvents) {
            this.putEvent(MergePendingEvent.INSTANCE);
        }
        final double ramBufferSizeMB = this.config.getRAMBufferSizeMB();
        if (ramBufferSizeMB != -1.0 && this.flushControl.getDeleteBytesUsed() > 1048576.0 * ramBufferSizeMB / 2.0) {
            hasEvents = true;
            if (!this.applyAllDeletes(this.deleteQueue)) {
                if (this.infoStream.isEnabled("DW")) {
                    this.infoStream.message("DW", String.format(Locale.ROOT, "force apply deletes bytesUsed=%.1f MB vs ramBuffer=%.1f MB", this.flushControl.getDeleteBytesUsed() / 1048576.0, ramBufferSizeMB));
                }
                this.putEvent(ApplyDeletesEvent.INSTANCE);
            }
        }
        return hasEvents;
    }
    
    void subtractFlushedNumDocs(final int numFlushed) {
        for (int oldValue = this.numDocsInRAM.get(); !this.numDocsInRAM.compareAndSet(oldValue, oldValue - numFlushed); oldValue = this.numDocsInRAM.get()) {}
        assert this.numDocsInRAM.get() >= 0;
    }
    
    private synchronized boolean setFlushingDeleteQueue(final DocumentsWriterDeleteQueue session) {
        this.currentFullFlushDelQueue = session;
        return true;
    }
    
    boolean flushAllThreads() throws IOException, AbortingException {
        if (this.infoStream.isEnabled("DW")) {
            this.infoStream.message("DW", "startFullFlush");
        }
        final DocumentsWriterDeleteQueue flushingDeleteQueue;
        synchronized (this) {
            this.pendingChangesInCurrentFullFlush = this.anyChanges();
            flushingDeleteQueue = this.deleteQueue;
            this.flushControl.markForFullFlush();
            assert this.setFlushingDeleteQueue(flushingDeleteQueue);
        }
        assert this.currentFullFlushDelQueue != null;
        assert this.currentFullFlushDelQueue != this.deleteQueue;
        boolean anythingFlushed = false;
        try {
            DocumentsWriterPerThread flushingDWPT;
            while ((flushingDWPT = this.flushControl.nextPendingFlush()) != null) {
                anythingFlushed |= this.doFlush(flushingDWPT);
            }
            this.flushControl.waitForFlush();
            if (!anythingFlushed && flushingDeleteQueue.anyChanges()) {
                if (this.infoStream.isEnabled("DW")) {
                    this.infoStream.message("DW", Thread.currentThread().getName() + ": flush naked frozen global deletes");
                }
                this.ticketQueue.addDeletes(flushingDeleteQueue);
            }
            this.ticketQueue.forcePurge(this.writer);
            assert !flushingDeleteQueue.anyChanges() && !this.ticketQueue.hasTickets();
        }
        finally {
            assert flushingDeleteQueue == this.currentFullFlushDelQueue;
        }
        return anythingFlushed;
    }
    
    void finishFullFlush(final IndexWriter indexWriter, final boolean success) {
        assert indexWriter.holdsFullFlushLock();
        try {
            if (this.infoStream.isEnabled("DW")) {
                this.infoStream.message("DW", Thread.currentThread().getName() + " finishFullFlush success=" + success);
            }
            assert this.setFlushingDeleteQueue(null);
            if (success) {
                this.flushControl.finishFullFlush();
            }
            else {
                this.flushControl.abortFullFlushes();
            }
        }
        finally {
            this.pendingChangesInCurrentFullFlush = false;
        }
    }
    
    public LiveIndexWriterConfig getIndexWriterConfig() {
        return this.config;
    }
    
    private void putEvent(final IndexWriter.Event event) {
        this.events.add(event);
    }
    
    @Override
    public long ramBytesUsed() {
        return this.flushControl.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public Queue<IndexWriter.Event> eventQueue() {
        return this.events;
    }
    
    static final class ApplyDeletesEvent implements IndexWriter.Event
    {
        static final IndexWriter.Event INSTANCE;
        private int instCount;
        
        private ApplyDeletesEvent() {
            this.instCount = 0;
            assert this.instCount == 0;
            ++this.instCount;
        }
        
        @Override
        public void process(final IndexWriter writer, final boolean triggerMerge, final boolean forcePurge) throws IOException {
            writer.applyDeletesAndPurge(true);
        }
        
        static {
            INSTANCE = new ApplyDeletesEvent();
        }
    }
    
    static final class MergePendingEvent implements IndexWriter.Event
    {
        static final IndexWriter.Event INSTANCE;
        private int instCount;
        
        private MergePendingEvent() {
            this.instCount = 0;
            assert this.instCount == 0;
            ++this.instCount;
        }
        
        @Override
        public void process(final IndexWriter writer, final boolean triggerMerge, final boolean forcePurge) throws IOException {
            writer.doAfterSegmentFlushed(triggerMerge, forcePurge);
        }
        
        static {
            INSTANCE = new MergePendingEvent();
        }
    }
    
    static final class ForcedPurgeEvent implements IndexWriter.Event
    {
        static final IndexWriter.Event INSTANCE;
        private int instCount;
        
        private ForcedPurgeEvent() {
            this.instCount = 0;
            assert this.instCount == 0;
            ++this.instCount;
        }
        
        @Override
        public void process(final IndexWriter writer, final boolean triggerMerge, final boolean forcePurge) throws IOException {
            writer.purge(true);
        }
        
        static {
            INSTANCE = new ForcedPurgeEvent();
        }
    }
    
    static class FlushFailedEvent implements IndexWriter.Event
    {
        private final SegmentInfo info;
        
        public FlushFailedEvent(final SegmentInfo info) {
            this.info = info;
        }
        
        @Override
        public void process(final IndexWriter writer, final boolean triggerMerge, final boolean forcePurge) throws IOException {
            writer.flushFailed(this.info);
        }
    }
    
    static class DeleteNewFilesEvent implements IndexWriter.Event
    {
        private final Collection<String> files;
        
        public DeleteNewFilesEvent(final Collection<String> files) {
            this.files = files;
        }
        
        @Override
        public void process(final IndexWriter writer, final boolean triggerMerge, final boolean forcePurge) throws IOException {
            writer.deleteNewFiles(this.files);
        }
    }
}
