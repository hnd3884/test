package org.apache.lucene.util;

import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.lang.ref.WeakReference;
import java.io.Closeable;

public class CloseableThreadLocal<T> implements Closeable
{
    private ThreadLocal<WeakReference<T>> t;
    private Map<Thread, T> hardRefs;
    private static int PURGE_MULTIPLIER;
    private final AtomicInteger countUntilPurge;
    
    public CloseableThreadLocal() {
        this.t = new ThreadLocal<WeakReference<T>>();
        this.hardRefs = new WeakHashMap<Thread, T>();
        this.countUntilPurge = new AtomicInteger(CloseableThreadLocal.PURGE_MULTIPLIER);
    }
    
    protected T initialValue() {
        return null;
    }
    
    public T get() {
        final WeakReference<T> weakRef = this.t.get();
        if (weakRef != null) {
            this.maybePurge();
            return weakRef.get();
        }
        final T iv = this.initialValue();
        if (iv != null) {
            this.set(iv);
            return iv;
        }
        return null;
    }
    
    public void set(final T object) {
        this.t.set(new WeakReference<T>(object));
        synchronized (this.hardRefs) {
            this.hardRefs.put(Thread.currentThread(), object);
            this.maybePurge();
        }
    }
    
    private void maybePurge() {
        if (this.countUntilPurge.getAndDecrement() == 0) {
            this.purge();
        }
    }
    
    private void purge() {
        synchronized (this.hardRefs) {
            int stillAliveCount = 0;
            final Iterator<Thread> it = this.hardRefs.keySet().iterator();
            while (it.hasNext()) {
                final Thread t = it.next();
                if (!t.isAlive()) {
                    it.remove();
                }
                else {
                    ++stillAliveCount;
                }
            }
            int nextCount = (1 + stillAliveCount) * CloseableThreadLocal.PURGE_MULTIPLIER;
            if (nextCount <= 0) {
                nextCount = 1000000;
            }
            this.countUntilPurge.set(nextCount);
        }
    }
    
    @Override
    public void close() {
        this.hardRefs = null;
        if (this.t != null) {
            this.t.remove();
        }
        this.t = null;
    }
    
    static {
        CloseableThreadLocal.PURGE_MULTIPLIER = 20;
    }
}
