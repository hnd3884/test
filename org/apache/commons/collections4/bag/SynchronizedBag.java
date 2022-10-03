package org.apache.commons.collections4.bag;

import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.collection.SynchronizedCollection;

public class SynchronizedBag<E> extends SynchronizedCollection<E> implements Bag<E>
{
    private static final long serialVersionUID = 8084674570753837109L;
    
    public static <E> SynchronizedBag<E> synchronizedBag(final Bag<E> bag) {
        return new SynchronizedBag<E>(bag);
    }
    
    protected SynchronizedBag(final Bag<E> bag) {
        super(bag);
    }
    
    protected SynchronizedBag(final Bag<E> bag, final Object lock) {
        super(bag, lock);
    }
    
    protected Bag<E> getBag() {
        return (Bag)this.decorated();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        synchronized (this.lock) {
            return this.getBag().equals(object);
        }
    }
    
    @Override
    public int hashCode() {
        synchronized (this.lock) {
            return this.getBag().hashCode();
        }
    }
    
    @Override
    public boolean add(final E object, final int count) {
        synchronized (this.lock) {
            return this.getBag().add(object, count);
        }
    }
    
    @Override
    public boolean remove(final Object object, final int count) {
        synchronized (this.lock) {
            return this.getBag().remove(object, count);
        }
    }
    
    @Override
    public Set<E> uniqueSet() {
        synchronized (this.lock) {
            final Set<E> set = this.getBag().uniqueSet();
            return new SynchronizedBagSet(set, this.lock);
        }
    }
    
    @Override
    public int getCount(final Object object) {
        synchronized (this.lock) {
            return this.getBag().getCount(object);
        }
    }
    
    class SynchronizedBagSet extends SynchronizedCollection<E> implements Set<E>
    {
        private static final long serialVersionUID = 2990565892366827855L;
        
        SynchronizedBagSet(final Set<E> set, final Object lock) {
            super(set, lock);
        }
    }
}
