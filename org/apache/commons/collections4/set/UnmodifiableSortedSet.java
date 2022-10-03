package org.apache.commons.collections4.set;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableSortedSet<E> extends AbstractSortedSetDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = -725356885467962424L;
    
    public static <E> SortedSet<E> unmodifiableSortedSet(final SortedSet<E> set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableSortedSet<E>(set);
    }
    
    private UnmodifiableSortedSet(final SortedSet<E> set) {
        super(set);
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
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        final SortedSet<E> sub = this.decorated().subSet(fromElement, toElement);
        return unmodifiableSortedSet(sub);
    }
    
    @Override
    public SortedSet<E> headSet(final E toElement) {
        final SortedSet<E> head = this.decorated().headSet(toElement);
        return unmodifiableSortedSet(head);
    }
    
    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        final SortedSet<E> tail = this.decorated().tailSet(fromElement);
        return unmodifiableSortedSet(tail);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.decorated());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setCollection((Collection<E>)in.readObject());
    }
}
