package com.sun.org.apache.xml.internal.security.utils;

import java.util.Collections;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Map;
import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;

@Deprecated
public abstract class WeakObjectPool<T, E extends Throwable>
{
    private static final Integer MARKER_VALUE;
    private final BlockingQueue<WeakReference<T>> available;
    private final Map<T, Integer> onLoan;
    
    protected WeakObjectPool() {
        this.available = new LinkedBlockingDeque<WeakReference<T>>();
        this.onLoan = Collections.synchronizedMap(new WeakHashMap<T, Integer>());
    }
    
    protected abstract T createObject() throws E, Throwable;
    
    public T getObject() throws E, Throwable {
        Object o = null;
        WeakReference weakReference;
        do {
            weakReference = this.available.poll();
        } while (weakReference != null && (o = weakReference.get()) == null);
        if (o == null) {
            o = this.createObject();
        }
        this.onLoan.put((T)o, WeakObjectPool.MARKER_VALUE);
        return (T)o;
    }
    
    public boolean repool(final T t) {
        if (t != null && this.onLoan.containsKey(t)) {
            synchronized (t) {
                if (this.onLoan.remove(t) != null) {
                    return this.available.offer(new WeakReference<T>(t));
                }
            }
        }
        return false;
    }
    
    static {
        MARKER_VALUE = Integer.MAX_VALUE;
    }
}
