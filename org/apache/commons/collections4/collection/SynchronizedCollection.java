package org.apache.commons.collections4.collection;

import java.util.Iterator;
import java.io.Serializable;
import java.util.Collection;

public class SynchronizedCollection<E> implements Collection<E>, Serializable
{
    private static final long serialVersionUID = 2412805092710877986L;
    private final Collection<E> collection;
    protected final Object lock;
    
    public static <T> SynchronizedCollection<T> synchronizedCollection(final Collection<T> coll) {
        return new SynchronizedCollection<T>(coll);
    }
    
    protected SynchronizedCollection(final Collection<E> collection) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        this.collection = collection;
        this.lock = this;
    }
    
    protected SynchronizedCollection(final Collection<E> collection, final Object lock) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        if (lock == null) {
            throw new NullPointerException("Lock must not be null.");
        }
        this.collection = collection;
        this.lock = lock;
    }
    
    protected Collection<E> decorated() {
        return this.collection;
    }
    
    @Override
    public boolean add(final E object) {
        synchronized (this.lock) {
            return this.decorated().add(object);
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        synchronized (this.lock) {
            return this.decorated().addAll(coll);
        }
    }
    
    @Override
    public void clear() {
        synchronized (this.lock) {
            this.decorated().clear();
        }
    }
    
    @Override
    public boolean contains(final Object object) {
        synchronized (this.lock) {
            return this.decorated().contains(object);
        }
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        synchronized (this.lock) {
            return this.decorated().containsAll(coll);
        }
    }
    
    @Override
    public boolean isEmpty() {
        synchronized (this.lock) {
            return this.decorated().isEmpty();
        }
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.decorated().iterator();
    }
    
    @Override
    public Object[] toArray() {
        synchronized (this.lock) {
            return this.decorated().toArray();
        }
    }
    
    @Override
    public <T> T[] toArray(final T[] object) {
        synchronized (this.lock) {
            return this.decorated().toArray(object);
        }
    }
    
    @Override
    public boolean remove(final Object object) {
        synchronized (this.lock) {
            return this.decorated().remove(object);
        }
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        synchronized (this.lock) {
            return this.decorated().removeAll(coll);
        }
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        synchronized (this.lock) {
            return this.decorated().retainAll(coll);
        }
    }
    
    @Override
    public int size() {
        synchronized (this.lock) {
            return this.decorated().size();
        }
    }
    
    @Override
    public boolean equals(final Object object) {
        synchronized (this.lock) {
            return object == this || object == this || this.decorated().equals(object);
        }
    }
    
    @Override
    public int hashCode() {
        synchronized (this.lock) {
            return this.decorated().hashCode();
        }
    }
    
    @Override
    public String toString() {
        synchronized (this.lock) {
            return this.decorated().toString();
        }
    }
}
