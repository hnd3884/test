package org.apache.lucene.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Queue;

class DocumentsWriterFlushQueue
{
    private final Queue<FlushTicket> queue;
    private final AtomicInteger ticketCount;
    private final ReentrantLock purgeLock;
    
    DocumentsWriterFlushQueue() {
        this.queue = new LinkedList<FlushTicket>();
        this.ticketCount = new AtomicInteger();
        this.purgeLock = new ReentrantLock();
    }
    
    void addDeletes(final DocumentsWriterDeleteQueue deleteQueue) throws IOException {
        synchronized (this) {
            this.incTickets();
            boolean success = false;
            try {
                this.queue.add(new GlobalDeletesTicket(deleteQueue.freezeGlobalBuffer(null)));
                success = true;
            }
            finally {
                if (!success) {
                    this.decTickets();
                }
            }
        }
    }
    
    private void incTickets() {
        final int numTickets = this.ticketCount.incrementAndGet();
        assert numTickets > 0;
    }
    
    private void decTickets() {
        final int numTickets = this.ticketCount.decrementAndGet();
        assert numTickets >= 0;
    }
    
    synchronized SegmentFlushTicket addFlushTicket(final DocumentsWriterPerThread dwpt) {
        this.incTickets();
        boolean success = false;
        try {
            final SegmentFlushTicket ticket = new SegmentFlushTicket(dwpt.prepareFlush());
            this.queue.add(ticket);
            success = true;
            return ticket;
        }
        finally {
            if (!success) {
                this.decTickets();
            }
        }
    }
    
    synchronized void addSegment(final SegmentFlushTicket ticket, final DocumentsWriterPerThread.FlushedSegment segment) {
        ticket.setSegment(segment);
    }
    
    synchronized void markTicketFailed(final SegmentFlushTicket ticket) {
        ticket.setFailed();
    }
    
    boolean hasTickets() {
        assert this.ticketCount.get() >= 0 : "ticketCount should be >= 0 but was: " + this.ticketCount.get();
        return this.ticketCount.get() != 0;
    }
    
    private int innerPurge(final IndexWriter writer) throws IOException {
        assert this.purgeLock.isHeldByCurrentThread();
        int numPurged = 0;
        while (true) {
            final FlushTicket head;
            final boolean canPublish;
            synchronized (this) {
                head = this.queue.peek();
                canPublish = (head != null && head.canPublish());
            }
            if (!canPublish) {
                break;
            }
            ++numPurged;
            try {
                head.publish(writer);
            }
            finally {
                synchronized (this) {
                    final FlushTicket poll = this.queue.poll();
                    this.ticketCount.decrementAndGet();
                    assert poll == head;
                }
            }
        }
        return numPurged;
    }
    
    int forcePurge(final IndexWriter writer) throws IOException {
        assert !Thread.holdsLock(this);
        assert !Thread.holdsLock(writer);
        this.purgeLock.lock();
        try {
            return this.innerPurge(writer);
        }
        finally {
            this.purgeLock.unlock();
        }
    }
    
    int tryPurge(final IndexWriter writer) throws IOException {
        assert !Thread.holdsLock(this);
        assert !Thread.holdsLock(writer);
        if (this.purgeLock.tryLock()) {
            try {
                return this.innerPurge(writer);
            }
            finally {
                this.purgeLock.unlock();
            }
        }
        return 0;
    }
    
    public int getTicketCount() {
        return this.ticketCount.get();
    }
    
    synchronized void clear() {
        this.queue.clear();
        this.ticketCount.set(0);
    }
    
    abstract static class FlushTicket
    {
        protected FrozenBufferedUpdates frozenUpdates;
        protected boolean published;
        
        protected FlushTicket(final FrozenBufferedUpdates frozenUpdates) {
            this.published = false;
            assert frozenUpdates != null;
            this.frozenUpdates = frozenUpdates;
        }
        
        protected abstract void publish(final IndexWriter p0) throws IOException;
        
        protected abstract boolean canPublish();
        
        protected final void publishFlushedSegment(final IndexWriter indexWriter, final DocumentsWriterPerThread.FlushedSegment newSegment, final FrozenBufferedUpdates globalPacket) throws IOException {
            assert newSegment != null;
            assert newSegment.segmentInfo != null;
            final FrozenBufferedUpdates segmentUpdates = newSegment.segmentUpdates;
            if (indexWriter.infoStream.isEnabled("DW")) {
                indexWriter.infoStream.message("DW", "publishFlushedSegment seg-private updates=" + segmentUpdates);
            }
            if (segmentUpdates != null && indexWriter.infoStream.isEnabled("DW")) {
                indexWriter.infoStream.message("DW", "flush: push buffered seg private updates: " + segmentUpdates);
            }
            indexWriter.publishFlushedSegment(newSegment.segmentInfo, segmentUpdates, globalPacket);
        }
        
        protected final void finishFlush(final IndexWriter indexWriter, final DocumentsWriterPerThread.FlushedSegment newSegment, final FrozenBufferedUpdates bufferedUpdates) throws IOException {
            if (newSegment == null) {
                assert bufferedUpdates != null;
                if (bufferedUpdates != null && bufferedUpdates.any()) {
                    indexWriter.publishFrozenUpdates(bufferedUpdates);
                    if (indexWriter.infoStream.isEnabled("DW")) {
                        indexWriter.infoStream.message("DW", "flush: push buffered updates: " + bufferedUpdates);
                    }
                }
            }
            else {
                this.publishFlushedSegment(indexWriter, newSegment, bufferedUpdates);
            }
        }
    }
    
    static final class GlobalDeletesTicket extends FlushTicket
    {
        protected GlobalDeletesTicket(final FrozenBufferedUpdates frozenUpdates) {
            super(frozenUpdates);
        }
        
        @Override
        protected void publish(final IndexWriter writer) throws IOException {
            assert !this.published : "ticket was already publised - can not publish twice";
            this.published = true;
            this.finishFlush(writer, null, this.frozenUpdates);
        }
        
        @Override
        protected boolean canPublish() {
            return true;
        }
    }
    
    static final class SegmentFlushTicket extends FlushTicket
    {
        private DocumentsWriterPerThread.FlushedSegment segment;
        private boolean failed;
        
        protected SegmentFlushTicket(final FrozenBufferedUpdates frozenDeletes) {
            super(frozenDeletes);
            this.failed = false;
        }
        
        @Override
        protected void publish(final IndexWriter writer) throws IOException {
            assert !this.published : "ticket was already publised - can not publish twice";
            this.published = true;
            this.finishFlush(writer, this.segment, this.frozenUpdates);
        }
        
        protected void setSegment(final DocumentsWriterPerThread.FlushedSegment segment) {
            assert !this.failed;
            this.segment = segment;
        }
        
        protected void setFailed() {
            assert this.segment == null;
            this.failed = true;
        }
        
        @Override
        protected boolean canPublish() {
            return this.segment != null || this.failed;
        }
    }
}
