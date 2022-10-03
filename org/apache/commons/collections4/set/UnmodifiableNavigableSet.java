package org.apache.commons.collections4.set;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.SortedSet;
import java.util.Collection;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.NavigableSet;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableNavigableSet<E> extends AbstractNavigableSetDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = 20150528L;
    
    public static <E> NavigableSet<E> unmodifiableNavigableSet(final NavigableSet<E> set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableNavigableSet<E>(set);
    }
    
    private UnmodifiableNavigableSet(final NavigableSet<E> set) {
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
    
    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        final SortedSet<E> sub = this.decorated().subSet(fromElement, toElement);
        return UnmodifiableSortedSet.unmodifiableSortedSet(sub);
    }
    
    @Override
    public SortedSet<E> headSet(final E toElement) {
        final SortedSet<E> head = this.decorated().headSet(toElement);
        return UnmodifiableSortedSet.unmodifiableSortedSet(head);
    }
    
    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        final SortedSet<E> tail = this.decorated().tailSet(fromElement);
        return UnmodifiableSortedSet.unmodifiableSortedSet(tail);
    }
    
    @Override
    public NavigableSet<E> descendingSet() {
        return unmodifiableNavigableSet(this.decorated().descendingSet());
    }
    
    @Override
    public Iterator<E> descendingIterator() {
        return UnmodifiableIterator.unmodifiableIterator((Iterator<? extends E>)this.decorated().descendingIterator());
    }
    
    @Override
    public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
        final NavigableSet<E> sub = this.decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
        return unmodifiableNavigableSet(sub);
    }
    
    @Override
    public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
        final NavigableSet<E> head = this.decorated().headSet(toElement, inclusive);
        return unmodifiableNavigableSet(head);
    }
    
    @Override
    public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
        final NavigableSet<E> tail = this.decorated().tailSet(fromElement, inclusive);
        return unmodifiableNavigableSet(tail);
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
