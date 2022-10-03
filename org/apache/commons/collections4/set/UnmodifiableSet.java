package org.apache.commons.collections4.set;

import java.util.Collection;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableSet<E> extends AbstractSerializableSetDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = 6499119872185240161L;
    
    public static <E> Set<E> unmodifiableSet(final Set<? extends E> set) {
        if (set instanceof Unmodifiable) {
            final Set<E> tmpSet = (Set<E>)set;
            return tmpSet;
        }
        return new UnmodifiableSet<E>(set);
    }
    
    private UnmodifiableSet(final Set<? extends E> set) {
        super(set);
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
