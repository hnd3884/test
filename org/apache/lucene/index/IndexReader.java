package org.apache.lucene.index;

import java.util.List;
import org.apache.lucene.document.DocumentStoredFieldVisitor;
import org.apache.lucene.document.Document;
import java.io.IOException;
import org.apache.lucene.store.AlreadyClosedException;
import java.util.Iterator;
import org.apache.lucene.util.IOUtils;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Closeable;

public abstract class IndexReader implements Closeable
{
    private boolean closed;
    private boolean closedByChild;
    private final AtomicInteger refCount;
    private final Set<ReaderClosedListener> readerClosedListeners;
    private final Set<IndexReader> parentReaders;
    
    IndexReader() {
        this.closed = false;
        this.closedByChild = false;
        this.refCount = new AtomicInteger(1);
        this.readerClosedListeners = Collections.synchronizedSet(new LinkedHashSet<ReaderClosedListener>());
        this.parentReaders = Collections.synchronizedSet((Set<IndexReader>)Collections.newSetFromMap((Map<T, Boolean>)new WeakHashMap<Object, Boolean>()));
        if (!(this instanceof CompositeReader) && !(this instanceof LeafReader)) {
            throw new Error("IndexReader should never be directly extended, subclass LeafReader or CompositeReader instead.");
        }
    }
    
    public final void addReaderClosedListener(final ReaderClosedListener listener) {
        this.ensureOpen();
        this.readerClosedListeners.add(listener);
    }
    
    public final void removeReaderClosedListener(final ReaderClosedListener listener) {
        this.ensureOpen();
        this.readerClosedListeners.remove(listener);
    }
    
    public final void registerParentReader(final IndexReader reader) {
        this.ensureOpen();
        this.parentReaders.add(reader);
    }
    
    private void notifyReaderClosedListeners(Throwable th) {
        synchronized (this.readerClosedListeners) {
            for (final ReaderClosedListener listener : this.readerClosedListeners) {
                try {
                    listener.onClose(this);
                }
                catch (final Throwable t) {
                    if (th == null) {
                        th = t;
                    }
                    else {
                        th.addSuppressed(t);
                    }
                }
            }
            IOUtils.reThrowUnchecked(th);
        }
    }
    
    private void reportCloseToParentReaders() {
        synchronized (this.parentReaders) {
            for (final IndexReader parent : this.parentReaders) {
                parent.closedByChild = true;
                parent.refCount.addAndGet(0);
                parent.reportCloseToParentReaders();
            }
        }
    }
    
    public final int getRefCount() {
        return this.refCount.get();
    }
    
    public final void incRef() {
        if (!this.tryIncRef()) {
            this.ensureOpen();
        }
    }
    
    public final boolean tryIncRef() {
        int count;
        while ((count = this.refCount.get()) > 0) {
            if (this.refCount.compareAndSet(count, count + 1)) {
                return true;
            }
        }
        return false;
    }
    
    public final void decRef() throws IOException {
        if (this.refCount.get() <= 0) {
            throw new AlreadyClosedException("this IndexReader is closed");
        }
        final int rc = this.refCount.decrementAndGet();
        if (rc == 0) {
            this.closed = true;
            Throwable throwable = null;
            try {
                this.doClose();
            }
            catch (final Throwable th) {
                throwable = th;
                try {
                    this.reportCloseToParentReaders();
                }
                finally {
                    this.notifyReaderClosedListeners(throwable);
                }
            }
            finally {
                try {
                    this.reportCloseToParentReaders();
                }
                finally {
                    this.notifyReaderClosedListeners(throwable);
                }
            }
        }
        else if (rc < 0) {
            throw new IllegalStateException("too many decRef calls: refCount is " + rc + " after decrement");
        }
    }
    
    protected final void ensureOpen() throws AlreadyClosedException {
        if (this.refCount.get() <= 0) {
            throw new AlreadyClosedException("this IndexReader is closed");
        }
        if (this.closedByChild) {
            throw new AlreadyClosedException("this IndexReader cannot be used anymore as one of its child readers was closed");
        }
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return this == obj;
    }
    
    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }
    
    public abstract Fields getTermVectors(final int p0) throws IOException;
    
    public final Terms getTermVector(final int docID, final String field) throws IOException {
        final Fields vectors = this.getTermVectors(docID);
        if (vectors == null) {
            return null;
        }
        return vectors.terms(field);
    }
    
    public abstract int numDocs();
    
    public abstract int maxDoc();
    
    public final int numDeletedDocs() {
        return this.maxDoc() - this.numDocs();
    }
    
    public abstract void document(final int p0, final StoredFieldVisitor p1) throws IOException;
    
    public final Document document(final int docID) throws IOException {
        final DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor();
        this.document(docID, visitor);
        return visitor.getDocument();
    }
    
    public final Document document(final int docID, final Set<String> fieldsToLoad) throws IOException {
        final DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor(fieldsToLoad);
        this.document(docID, visitor);
        return visitor.getDocument();
    }
    
    public boolean hasDeletions() {
        return this.numDeletedDocs() > 0;
    }
    
    @Override
    public final synchronized void close() throws IOException {
        if (!this.closed) {
            this.decRef();
            this.closed = true;
        }
    }
    
    protected abstract void doClose() throws IOException;
    
    public abstract IndexReaderContext getContext();
    
    public final List<LeafReaderContext> leaves() {
        return this.getContext().leaves();
    }
    
    public Object getCoreCacheKey() {
        return this;
    }
    
    public Object getCombinedCoreAndDeletesKey() {
        return this;
    }
    
    public abstract int docFreq(final Term p0) throws IOException;
    
    public abstract long totalTermFreq(final Term p0) throws IOException;
    
    public abstract long getSumDocFreq(final String p0) throws IOException;
    
    public abstract int getDocCount(final String p0) throws IOException;
    
    public abstract long getSumTotalTermFreq(final String p0) throws IOException;
    
    public interface ReaderClosedListener
    {
        void onClose(final IndexReader p0) throws IOException;
    }
}
