package org.apache.commons.collections4.multiset;

import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.collection.SynchronizedCollection;

public class SynchronizedMultiSet<E> extends SynchronizedCollection<E> implements MultiSet<E>
{
    private static final long serialVersionUID = 20150629L;
    
    public static <E> SynchronizedMultiSet<E> synchronizedMultiSet(final MultiSet<E> multiset) {
        return new SynchronizedMultiSet<E>(multiset);
    }
    
    protected SynchronizedMultiSet(final MultiSet<E> multiset) {
        super(multiset);
    }
    
    protected SynchronizedMultiSet(final MultiSet<E> multiset, final Object lock) {
        super(multiset, lock);
    }
    
    @Override
    protected MultiSet<E> decorated() {
        return (MultiSet)super.decorated();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        synchronized (this.lock) {
            return this.decorated().equals(object);
        }
    }
    
    @Override
    public int hashCode() {
        synchronized (this.lock) {
            return this.decorated().hashCode();
        }
    }
    
    @Override
    public int add(final E object, final int count) {
        synchronized (this.lock) {
            return this.decorated().add(object, count);
        }
    }
    
    @Override
    public int remove(final Object object, final int count) {
        synchronized (this.lock) {
            return this.decorated().remove(object, count);
        }
    }
    
    @Override
    public int getCount(final Object object) {
        synchronized (this.lock) {
            return this.decorated().getCount(object);
        }
    }
    
    @Override
    public int setCount(final E object, final int count) {
        synchronized (this.lock) {
            return this.decorated().setCount(object, count);
        }
    }
    
    @Override
    public Set<E> uniqueSet() {
        synchronized (this.lock) {
            final Set<E> set = this.decorated().uniqueSet();
            return new SynchronizedSet<E>(set, this.lock);
        }
    }
    
    @Override
    public Set<Entry<E>> entrySet() {
        synchronized (this.lock) {
            final Set<Entry<E>> set = this.decorated().entrySet();
            return new SynchronizedSet<Entry<E>>(set, this.lock);
        }
    }
    
    static class SynchronizedSet<T> extends SynchronizedCollection<T> implements Set<T>
    {
        private static final long serialVersionUID = 20150629L;
        
        SynchronizedSet(final Set<T> set, final Object lock) {
            super(set, lock);
        }
    }
}
