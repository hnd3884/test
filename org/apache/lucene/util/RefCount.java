package org.apache.lucene.util;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RefCount<T>
{
    private final AtomicInteger refCount;
    protected final T object;
    
    public RefCount(final T object) {
        this.refCount = new AtomicInteger(1);
        this.object = object;
    }
    
    protected void release() throws IOException {
    }
    
    public final void decRef() throws IOException {
        final int rc = this.refCount.decrementAndGet();
        if (rc == 0) {
            boolean success = false;
            try {
                this.release();
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
    
    public final T get() {
        return this.object;
    }
    
    public final int getRefCount() {
        return this.refCount.get();
    }
    
    public final void incRef() {
        this.refCount.incrementAndGet();
    }
}
