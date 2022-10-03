package org.apache.lucene.util;

import java.util.concurrent.atomic.AtomicBoolean;

public final class SetOnce<T> implements Cloneable
{
    private volatile T obj;
    private final AtomicBoolean set;
    
    public SetOnce() {
        this.obj = null;
        this.set = new AtomicBoolean(false);
    }
    
    public SetOnce(final T obj) {
        this.obj = null;
        this.obj = obj;
        this.set = new AtomicBoolean(true);
    }
    
    public final void set(final T obj) {
        if (this.set.compareAndSet(false, true)) {
            this.obj = obj;
            return;
        }
        throw new AlreadySetException();
    }
    
    public final T get() {
        return this.obj;
    }
    
    public static final class AlreadySetException extends IllegalStateException
    {
        public AlreadySetException() {
            super("The object cannot be set twice!");
        }
    }
}
