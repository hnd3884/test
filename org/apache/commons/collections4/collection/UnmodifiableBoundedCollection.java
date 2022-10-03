package org.apache.commons.collections4.collection;

import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.BoundedCollection;

public final class UnmodifiableBoundedCollection<E> extends AbstractCollectionDecorator<E> implements BoundedCollection<E>, Unmodifiable
{
    private static final long serialVersionUID = -7112672385450340330L;
    
    public static <E> BoundedCollection<E> unmodifiableBoundedCollection(final BoundedCollection<? extends E> coll) {
        if (coll instanceof Unmodifiable) {
            final BoundedCollection<E> tmpColl = (BoundedCollection<E>)coll;
            return tmpColl;
        }
        return new UnmodifiableBoundedCollection<E>(coll);
    }
    
    public static <E> BoundedCollection<E> unmodifiableBoundedCollection(Collection<? extends E> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        for (int i = 0; i < 1000 && !(coll instanceof BoundedCollection); ++i) {
            if (coll instanceof AbstractCollectionDecorator) {
                coll = ((AbstractCollectionDecorator)coll).decorated();
            }
            else if (coll instanceof SynchronizedCollection) {
                coll = ((SynchronizedCollection)coll).decorated();
            }
        }
        if (!(coll instanceof BoundedCollection)) {
            throw new IllegalArgumentException("Collection is not a bounded collection.");
        }
        return new UnmodifiableBoundedCollection<E>((BoundedCollection<? extends E>)(BoundedCollection)coll);
    }
    
    private UnmodifiableBoundedCollection(final BoundedCollection<? extends E> coll) {
        super(coll);
    }
    
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(this.decorated().iterator());
    }
    
    @Override
    public boolean add(final E object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isFull() {
        return this.decorated().isFull();
    }
    
    @Override
    public int maxSize() {
        return this.decorated().maxSize();
    }
    
    @Override
    protected BoundedCollection<E> decorated() {
        return (BoundedCollection)super.decorated();
    }
}
