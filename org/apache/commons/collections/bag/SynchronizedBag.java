package org.apache.commons.collections.bag;

import org.apache.commons.collections.set.SynchronizedSet;
import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.collection.SynchronizedCollection;

public class SynchronizedBag extends SynchronizedCollection implements Bag
{
    private static final long serialVersionUID = 8084674570753837109L;
    
    public static Bag decorate(final Bag bag) {
        return new SynchronizedBag(bag);
    }
    
    protected SynchronizedBag(final Bag bag) {
        super(bag);
    }
    
    protected SynchronizedBag(final Bag bag, final Object lock) {
        super(bag, lock);
    }
    
    protected Bag getBag() {
        return (Bag)this.collection;
    }
    
    public boolean add(final Object object, final int count) {
        synchronized (this.lock) {
            return this.getBag().add(object, count);
        }
    }
    
    public boolean remove(final Object object, final int count) {
        synchronized (this.lock) {
            return this.getBag().remove(object, count);
        }
    }
    
    public Set uniqueSet() {
        synchronized (this.lock) {
            final Set set = this.getBag().uniqueSet();
            return new SynchronizedBagSet(set, this.lock);
        }
    }
    
    public int getCount(final Object object) {
        synchronized (this.lock) {
            return this.getBag().getCount(object);
        }
    }
    
    class SynchronizedBagSet extends SynchronizedSet
    {
        SynchronizedBagSet(final Set set, final Object lock) {
            super(set, lock);
        }
    }
}
