package org.apache.commons.collections4.list;

import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import java.util.ListIterator;
import java.util.Collection;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableList<E> extends AbstractSerializableListDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = 6595182819922443652L;
    
    public static <E> List<E> unmodifiableList(final List<? extends E> list) {
        if (list instanceof Unmodifiable) {
            final List<E> tmpList = (List<E>)list;
            return tmpList;
        }
        return new UnmodifiableList<E>(list);
    }
    
    public UnmodifiableList(final List<? extends E> list) {
        super(list);
    }
    
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator((Iterator<? extends E>)this.decorated().iterator());
    }
    
    @Override
    public boolean add(final Object object) {
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
    public ListIterator<E> listIterator() {
        return UnmodifiableListIterator.umodifiableListIterator((ListIterator<? extends E>)this.decorated().listIterator());
    }
    
    @Override
    public ListIterator<E> listIterator(final int index) {
        return UnmodifiableListIterator.umodifiableListIterator((ListIterator<? extends E>)this.decorated().listIterator(index));
    }
    
    @Override
    public void add(final int index, final E object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E remove(final int index) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E set(final int index, final E object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        final List<E> sub = this.decorated().subList(fromIndex, toIndex);
        return new UnmodifiableList(sub);
    }
}
