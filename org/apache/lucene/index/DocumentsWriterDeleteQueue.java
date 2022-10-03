package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.Query;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.apache.lucene.util.Accountable;

final class DocumentsWriterDeleteQueue implements Accountable
{
    private volatile Node<?> tail;
    private static final AtomicReferenceFieldUpdater<DocumentsWriterDeleteQueue, Node> tailUpdater;
    private final DeleteSlice globalSlice;
    private final BufferedUpdates globalBufferedUpdates;
    final ReentrantLock globalBufferLock;
    final long generation;
    
    DocumentsWriterDeleteQueue() {
        this(0L);
    }
    
    DocumentsWriterDeleteQueue(final long generation) {
        this(new BufferedUpdates(), generation);
    }
    
    DocumentsWriterDeleteQueue(final BufferedUpdates globalBufferedUpdates, final long generation) {
        this.globalBufferLock = new ReentrantLock();
        this.globalBufferedUpdates = globalBufferedUpdates;
        this.generation = generation;
        this.tail = new Node<Object>(null);
        this.globalSlice = new DeleteSlice(this.tail);
    }
    
    void addDelete(final Query... queries) {
        this.add(new QueryArrayNode(queries));
        this.tryApplyGlobalSlice();
    }
    
    void addDelete(final Term... terms) {
        this.add(new TermArrayNode(terms));
        this.tryApplyGlobalSlice();
    }
    
    void addDocValuesUpdates(final DocValuesUpdate... updates) {
        this.add(new DocValuesUpdatesNode(updates));
        this.tryApplyGlobalSlice();
    }
    
    void add(final Term term, final DeleteSlice slice) {
        final TermNode termNode = new TermNode(term);
        this.add(termNode);
        slice.sliceTail = termNode;
        assert slice.sliceHead != slice.sliceTail : "slice head and tail must differ after add";
        this.tryApplyGlobalSlice();
    }
    
    void add(final Node<?> item) {
        Node<?> currentTail;
        while (true) {
            currentTail = this.tail;
            final Node<?> tailNext = currentTail.next;
            if (this.tail == currentTail) {
                if (tailNext != null) {
                    DocumentsWriterDeleteQueue.tailUpdater.compareAndSet(this, currentTail, tailNext);
                }
                else {
                    if (currentTail.casNext(null, item)) {
                        break;
                    }
                    continue;
                }
            }
        }
        DocumentsWriterDeleteQueue.tailUpdater.compareAndSet(this, currentTail, item);
    }
    
    boolean anyChanges() {
        this.globalBufferLock.lock();
        try {
            return this.globalBufferedUpdates.any() || !this.globalSlice.isEmpty() || this.globalSlice.sliceTail != this.tail || this.tail.next != null;
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }
    
    void tryApplyGlobalSlice() {
        if (this.globalBufferLock.tryLock()) {
            try {
                if (this.updateSlice(this.globalSlice)) {
                    this.globalSlice.apply(this.globalBufferedUpdates, BufferedUpdates.MAX_INT);
                }
            }
            finally {
                this.globalBufferLock.unlock();
            }
        }
    }
    
    FrozenBufferedUpdates freezeGlobalBuffer(final DeleteSlice callerSlice) {
        this.globalBufferLock.lock();
        final Node<?> currentTail = this.tail;
        if (callerSlice != null) {
            callerSlice.sliceTail = currentTail;
        }
        try {
            if (this.globalSlice.sliceTail != currentTail) {
                this.globalSlice.sliceTail = currentTail;
                this.globalSlice.apply(this.globalBufferedUpdates, BufferedUpdates.MAX_INT);
            }
            final FrozenBufferedUpdates packet = new FrozenBufferedUpdates(this.globalBufferedUpdates, false);
            this.globalBufferedUpdates.clear();
            return packet;
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }
    
    DeleteSlice newSlice() {
        return new DeleteSlice(this.tail);
    }
    
    boolean updateSlice(final DeleteSlice slice) {
        if (slice.sliceTail != this.tail) {
            slice.sliceTail = this.tail;
            return true;
        }
        return false;
    }
    
    public int numGlobalTermDeletes() {
        return this.globalBufferedUpdates.numTermDeletes.get();
    }
    
    void clear() {
        this.globalBufferLock.lock();
        try {
            final Node<?> currentTail = this.tail;
            final DeleteSlice globalSlice = this.globalSlice;
            final DeleteSlice globalSlice2 = this.globalSlice;
            final Node<?> node = currentTail;
            globalSlice2.sliceTail = node;
            globalSlice.sliceHead = node;
            this.globalBufferedUpdates.clear();
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }
    
    private boolean forceApplyGlobalSlice() {
        this.globalBufferLock.lock();
        final Node<?> currentTail = this.tail;
        try {
            if (this.globalSlice.sliceTail != currentTail) {
                this.globalSlice.sliceTail = currentTail;
                this.globalSlice.apply(this.globalBufferedUpdates, BufferedUpdates.MAX_INT);
            }
            return this.globalBufferedUpdates.any();
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }
    
    public int getBufferedUpdatesTermsSize() {
        this.globalBufferLock.lock();
        try {
            this.forceApplyGlobalSlice();
            return this.globalBufferedUpdates.terms.size();
        }
        finally {
            this.globalBufferLock.unlock();
        }
    }
    
    @Override
    public long ramBytesUsed() {
        return this.globalBufferedUpdates.bytesUsed.get();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    @Override
    public String toString() {
        return "DWDQ: [ generation: " + this.generation + " ]";
    }
    
    static {
        tailUpdater = AtomicReferenceFieldUpdater.newUpdater(DocumentsWriterDeleteQueue.class, Node.class, "tail");
    }
    
    static class DeleteSlice
    {
        Node<?> sliceHead;
        Node<?> sliceTail;
        
        DeleteSlice(final Node<?> currentTail) {
            assert currentTail != null;
            this.sliceTail = currentTail;
            this.sliceHead = currentTail;
        }
        
        void apply(final BufferedUpdates del, final int docIDUpto) {
            if (this.sliceHead == this.sliceTail) {
                return;
            }
            Node<?> current = this.sliceHead;
            do {
                current = current.next;
                assert current != null : "slice property violated between the head on the tail must not be a null node";
                current.apply(del, docIDUpto);
            } while (current != this.sliceTail);
            this.reset();
        }
        
        void reset() {
            this.sliceHead = this.sliceTail;
        }
        
        boolean isTailItem(final Object item) {
            return this.sliceTail.item == item;
        }
        
        boolean isEmpty() {
            return this.sliceHead == this.sliceTail;
        }
    }
    
    private static class Node<T>
    {
        volatile Node<?> next;
        final T item;
        static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater;
        
        Node(final T item) {
            this.item = item;
        }
        
        void apply(final BufferedUpdates bufferedDeletes, final int docIDUpto) {
            throw new IllegalStateException("sentinel item must never be applied");
        }
        
        boolean casNext(final Node<?> cmp, final Node<?> val) {
            return Node.nextUpdater.compareAndSet(this, cmp, val);
        }
        
        static {
            nextUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
        }
    }
    
    private static final class TermNode extends Node<Term>
    {
        TermNode(final Term term) {
            super(term);
        }
        
        @Override
        void apply(final BufferedUpdates bufferedDeletes, final int docIDUpto) {
            bufferedDeletes.addTerm((Term)this.item, docIDUpto);
        }
        
        @Override
        public String toString() {
            return "del=" + this.item;
        }
    }
    
    private static final class QueryArrayNode extends Node<Query[]>
    {
        QueryArrayNode(final Query[] query) {
            super(query);
        }
        
        @Override
        void apply(final BufferedUpdates bufferedUpdates, final int docIDUpto) {
            for (final Query query : (Query[])(Object)this.item) {
                bufferedUpdates.addQuery(query, docIDUpto);
            }
        }
    }
    
    private static final class TermArrayNode extends Node<Term[]>
    {
        TermArrayNode(final Term[] term) {
            super(term);
        }
        
        @Override
        void apply(final BufferedUpdates bufferedUpdates, final int docIDUpto) {
            for (final Term term : (Term[])(Object)this.item) {
                bufferedUpdates.addTerm(term, docIDUpto);
            }
        }
        
        @Override
        public String toString() {
            return "dels=" + Arrays.toString((Object[])(Object)this.item);
        }
    }
    
    private static final class DocValuesUpdatesNode extends Node<DocValuesUpdate[]>
    {
        DocValuesUpdatesNode(final DocValuesUpdate... updates) {
            super(updates);
        }
        
        @Override
        void apply(final BufferedUpdates bufferedUpdates, final int docIDUpto) {
            for (final DocValuesUpdate update : (DocValuesUpdate[])(Object)this.item) {
                switch (update.type) {
                    case NUMERIC: {
                        bufferedUpdates.addNumericUpdate(new DocValuesUpdate.NumericDocValuesUpdate(update.term, update.field, (Long)update.value), docIDUpto);
                        break;
                    }
                    case BINARY: {
                        bufferedUpdates.addBinaryUpdate(new DocValuesUpdate.BinaryDocValuesUpdate(update.term, update.field, (BytesRef)update.value), docIDUpto);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException(update.type + " DocValues updates not supported yet!");
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("docValuesUpdates: ");
            if (((DocValuesUpdate[])(Object)this.item).length > 0) {
                sb.append("term=").append(((DocValuesUpdate[])(Object)this.item)[0].term).append("; updates: [");
                for (final DocValuesUpdate update : (DocValuesUpdate[])(Object)this.item) {
                    sb.append(update.field).append(':').append(update.value).append(',');
                }
                sb.setCharAt(sb.length() - 1, ']');
            }
            return sb.toString();
        }
    }
}
