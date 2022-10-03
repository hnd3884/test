package org.apache.commons.collections.buffer;

import java.util.Collection;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.collection.SynchronizedCollection;

public class SynchronizedBuffer extends SynchronizedCollection implements Buffer
{
    private static final long serialVersionUID = -6859936183953626253L;
    
    public static Buffer decorate(final Buffer buffer) {
        return new SynchronizedBuffer(buffer);
    }
    
    protected SynchronizedBuffer(final Buffer buffer) {
        super(buffer);
    }
    
    protected SynchronizedBuffer(final Buffer buffer, final Object lock) {
        super(buffer, lock);
    }
    
    protected Buffer getBuffer() {
        return (Buffer)this.collection;
    }
    
    public Object get() {
        synchronized (this.lock) {
            return this.getBuffer().get();
        }
    }
    
    public Object remove() {
        synchronized (this.lock) {
            return this.getBuffer().remove();
        }
    }
}
