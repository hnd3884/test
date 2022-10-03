package org.apache.commons.collections4.list;

import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import java.util.ListIterator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.BoundedCollection;

public class FixedSizeList<E> extends AbstractSerializableListDecorator<E> implements BoundedCollection<E>
{
    private static final long serialVersionUID = -2218010673611160319L;
    
    public static <E> FixedSizeList<E> fixedSizeList(final List<E> list) {
        return new FixedSizeList<E>(list);
    }
    
    protected FixedSizeList(final List<E> list) {
        super(list);
    }
    
    @Override
    public boolean add(final E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public void add(final int index, final E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public E get(final int index) {
        return this.decorated().get(index);
    }
    
    @Override
    public int indexOf(final Object object) {
        return this.decorated().indexOf(object);
    }
    
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator((Iterator<? extends E>)this.decorated().iterator());
    }
    
    @Override
    public int lastIndexOf(final Object object) {
        return this.decorated().lastIndexOf(object);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return new FixedSizeListIterator(this.decorated().listIterator(0));
    }
    
    @Override
    public ListIterator<E> listIterator(final int index) {
        return new FixedSizeListIterator(this.decorated().listIterator(index));
    }
    
    @Override
    public E remove(final int index) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    @Override
    public E set(final int index, final E object) {
        return this.decorated().set(index, object);
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        final List<E> sub = this.decorated().subList(fromIndex, toIndex);
        return new FixedSizeList((List<Object>)sub);
    }
    
    @Override
    public boolean isFull() {
        return true;
    }
    
    @Override
    public int maxSize() {
        return this.size();
    }
    
    private class FixedSizeListIterator extends AbstractListIteratorDecorator<E>
    {
        protected FixedSizeListIterator(final ListIterator<E> iterator) {
            super(iterator);
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("List is fixed size");
        }
        
        @Override
        public void add(final Object object) {
            throw new UnsupportedOperationException("List is fixed size");
        }
    }
}
