package org.apache.commons.collections.collection;

import org.apache.commons.collections.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections.BoundedCollection;

public final class UnmodifiableBoundedCollection extends AbstractSerializableCollectionDecorator implements BoundedCollection
{
    private static final long serialVersionUID = -7112672385450340330L;
    
    public static BoundedCollection decorate(final BoundedCollection coll) {
        return new UnmodifiableBoundedCollection(coll);
    }
    
    public static BoundedCollection decorateUsing(Collection coll) {
        if (coll == null) {
            throw new IllegalArgumentException("The collection must not be null");
        }
        for (int i = 0; i < 1000; ++i) {
            if (coll instanceof BoundedCollection) {
                break;
            }
            if (coll instanceof AbstractCollectionDecorator) {
                coll = ((AbstractCollectionDecorator)coll).collection;
            }
            else {
                if (!(coll instanceof SynchronizedCollection)) {
                    break;
                }
                coll = ((SynchronizedCollection)coll).collection;
            }
        }
        if (!(coll instanceof BoundedCollection)) {
            throw new IllegalArgumentException("The collection is not a bounded collection");
        }
        return new UnmodifiableBoundedCollection((BoundedCollection)coll);
    }
    
    private UnmodifiableBoundedCollection(final BoundedCollection coll) {
        super(coll);
    }
    
    public Iterator iterator() {
        return UnmodifiableIterator.decorate(this.getCollection().iterator());
    }
    
    public boolean add(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    public boolean addAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isFull() {
        return ((BoundedCollection)this.collection).isFull();
    }
    
    public int maxSize() {
        return ((BoundedCollection)this.collection).maxSize();
    }
}
