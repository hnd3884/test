package org.apache.commons.collections4.bag;

import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.SortedBag;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableSortedBag<E> extends AbstractSortedBagDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = -3190437252665717841L;
    
    public static <E> SortedBag<E> unmodifiableSortedBag(final SortedBag<E> bag) {
        if (bag instanceof Unmodifiable) {
            return bag;
        }
        return new UnmodifiableSortedBag<E>(bag);
    }
    
    private UnmodifiableSortedBag(final SortedBag<E> bag) {
        super(bag);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.decorated());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setCollection((Collection<E>)in.readObject());
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
    public boolean add(final E object, final int count) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object object, final int count) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<E> uniqueSet() {
        final Set<E> set = this.decorated().uniqueSet();
        return UnmodifiableSet.unmodifiableSet((Set<? extends E>)set);
    }
}
