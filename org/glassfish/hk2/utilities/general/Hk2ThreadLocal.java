package org.glassfish.hk2.utilities.general;

import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Hk2ThreadLocal<T>
{
    private final ReentrantReadWriteLock readWriteLock;
    private final ReentrantReadWriteLock.WriteLock wLock;
    private final ReentrantReadWriteLock.ReadLock rLock;
    private final WeakHashMap<Thread, T> locals;
    
    public Hk2ThreadLocal() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.wLock = this.readWriteLock.writeLock();
        this.rLock = this.readWriteLock.readLock();
        this.locals = new WeakHashMap<Thread, T>();
    }
    
    protected T initialValue() {
        return null;
    }
    
    public T get() {
        final Thread id = Thread.currentThread();
        this.rLock.lock();
        try {
            if (this.locals.containsKey(id)) {
                return this.locals.get(id);
            }
        }
        finally {
            this.rLock.unlock();
        }
        this.wLock.lock();
        try {
            if (this.locals.containsKey(id)) {
                return this.locals.get(id);
            }
            final T initialValue = this.initialValue();
            this.locals.put(id, initialValue);
            return initialValue;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    public void set(final T value) {
        final Thread id = Thread.currentThread();
        this.wLock.lock();
        try {
            this.locals.put(id, value);
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    public void remove() {
        final Thread id = Thread.currentThread();
        this.wLock.lock();
        try {
            this.locals.remove(id);
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    public void removeAll() {
        this.wLock.lock();
        try {
            this.locals.clear();
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    public int getSize() {
        this.rLock.lock();
        try {
            return this.locals.size();
        }
        finally {
            this.rLock.unlock();
        }
    }
}
