package org.apache.commons.collections4.collection;

import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableCollection<E> extends AbstractCollectionDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = -239892006883819945L;
    
    public static <T> Collection<T> unmodifiableCollection(final Collection<? extends T> coll) {
        if (coll instanceof Unmodifiable) {
            final Collection<T> tmpColl = (Collection<T>)coll;
            return tmpColl;
        }
        return new UnmodifiableCollection<T>(coll);
    }
    
    private UnmodifiableCollection(final Collection<? extends E> coll) {
        super(coll);
    }
    
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator((Iterator<? extends E>)this.decorated().iterator());
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
}
