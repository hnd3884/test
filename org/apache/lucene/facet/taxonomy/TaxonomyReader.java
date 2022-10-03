package org.apache.lucene.facet.taxonomy;

import java.util.Map;
import org.apache.lucene.store.AlreadyClosedException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Closeable;

public abstract class TaxonomyReader implements Closeable
{
    public static final int ROOT_ORDINAL = 0;
    public static final int INVALID_ORDINAL = -1;
    private volatile boolean closed;
    private final AtomicInteger refCount;
    
    public TaxonomyReader() {
        this.closed = false;
        this.refCount = new AtomicInteger(1);
    }
    
    public static <T extends TaxonomyReader> T openIfChanged(final T oldTaxoReader) throws IOException {
        final T newTaxoReader = (T)oldTaxoReader.doOpenIfChanged();
        assert newTaxoReader != oldTaxoReader;
        return newTaxoReader;
    }
    
    protected abstract void doClose() throws IOException;
    
    protected abstract TaxonomyReader doOpenIfChanged() throws IOException;
    
    protected final void ensureOpen() throws AlreadyClosedException {
        if (this.getRefCount() <= 0) {
            throw new AlreadyClosedException("this TaxonomyReader is closed");
        }
    }
    
    @Override
    public final void close() throws IOException {
        if (!this.closed) {
            synchronized (this) {
                if (!this.closed) {
                    this.decRef();
                    this.closed = true;
                }
            }
        }
    }
    
    public final void decRef() throws IOException {
        this.ensureOpen();
        final int rc = this.refCount.decrementAndGet();
        if (rc == 0) {
            boolean success = false;
            try {
                this.doClose();
                this.closed = true;
                success = true;
            }
            finally {
                if (!success) {
                    this.refCount.incrementAndGet();
                }
            }
        }
        else if (rc < 0) {
            throw new IllegalStateException("too many decRef calls: refCount is " + rc + " after decrement");
        }
    }
    
    public abstract ParallelTaxonomyArrays getParallelTaxonomyArrays() throws IOException;
    
    public ChildrenIterator getChildren(final int ordinal) throws IOException {
        final ParallelTaxonomyArrays arrays = this.getParallelTaxonomyArrays();
        final int child = (ordinal >= 0) ? arrays.children()[ordinal] : -1;
        return new ChildrenIterator(child, arrays.siblings());
    }
    
    public abstract Map<String, String> getCommitUserData() throws IOException;
    
    public abstract int getOrdinal(final FacetLabel p0) throws IOException;
    
    public int getOrdinal(final String dim, final String[] path) throws IOException {
        final String[] fullPath = new String[path.length + 1];
        fullPath[0] = dim;
        System.arraycopy(path, 0, fullPath, 1, path.length);
        return this.getOrdinal(new FacetLabel(fullPath));
    }
    
    public abstract FacetLabel getPath(final int p0) throws IOException;
    
    public final int getRefCount() {
        return this.refCount.get();
    }
    
    public abstract int getSize();
    
    public final void incRef() {
        this.ensureOpen();
        this.refCount.incrementAndGet();
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
    
    public static class ChildrenIterator
    {
        private final int[] siblings;
        private int child;
        
        ChildrenIterator(final int child, final int[] siblings) {
            this.siblings = siblings;
            this.child = child;
        }
        
        public int next() {
            final int res = this.child;
            if (this.child != -1) {
                this.child = this.siblings[this.child];
            }
            return res;
        }
    }
}
