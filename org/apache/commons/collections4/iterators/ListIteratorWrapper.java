package org.apache.commons.collections4.iterators;

import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.commons.collections4.ResettableListIterator;

public class ListIteratorWrapper<E> implements ResettableListIterator<E>
{
    private static final String UNSUPPORTED_OPERATION_MESSAGE = "ListIteratorWrapper does not support optional operations of ListIterator.";
    private static final String CANNOT_REMOVE_MESSAGE = "Cannot remove element at index {0}.";
    private final Iterator<? extends E> iterator;
    private final List<E> list;
    private int currentIndex;
    private int wrappedIteratorIndex;
    private boolean removeState;
    
    public ListIteratorWrapper(final Iterator<? extends E> iterator) {
        this.list = new ArrayList<E>();
        this.currentIndex = 0;
        this.wrappedIteratorIndex = 0;
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iterator = iterator;
    }
    
    @Override
    public void add(final E obj) throws UnsupportedOperationException {
        if (this.iterator instanceof ListIterator) {
            final ListIterator<E> li = (ListIterator)this.iterator;
            li.add(obj);
            return;
        }
        throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
    }
    
    @Override
    public boolean hasNext() {
        return (this.currentIndex != this.wrappedIteratorIndex && !(this.iterator instanceof ListIterator)) || this.iterator.hasNext();
    }
    
    @Override
    public boolean hasPrevious() {
        if (this.iterator instanceof ListIterator) {
            final ListIterator<?> li = (ListIterator)this.iterator;
            return li.hasPrevious();
        }
        return this.currentIndex > 0;
    }
    
    @Override
    public E next() throws NoSuchElementException {
        if (this.iterator instanceof ListIterator) {
            return (E)this.iterator.next();
        }
        if (this.currentIndex < this.wrappedIteratorIndex) {
            ++this.currentIndex;
            return this.list.get(this.currentIndex - 1);
        }
        final E retval = (E)this.iterator.next();
        this.list.add(retval);
        ++this.currentIndex;
        ++this.wrappedIteratorIndex;
        this.removeState = true;
        return retval;
    }
    
    @Override
    public int nextIndex() {
        if (this.iterator instanceof ListIterator) {
            final ListIterator<?> li = (ListIterator)this.iterator;
            return li.nextIndex();
        }
        return this.currentIndex;
    }
    
    @Override
    public E previous() throws NoSuchElementException {
        if (this.iterator instanceof ListIterator) {
            final ListIterator<E> li = (ListIterator)this.iterator;
            return li.previous();
        }
        if (this.currentIndex == 0) {
            throw new NoSuchElementException();
        }
        this.removeState = (this.wrappedIteratorIndex == this.currentIndex);
        final List<E> list = this.list;
        final int currentIndex = this.currentIndex - 1;
        this.currentIndex = currentIndex;
        return list.get(currentIndex);
    }
    
    @Override
    public int previousIndex() {
        if (this.iterator instanceof ListIterator) {
            final ListIterator<?> li = (ListIterator)this.iterator;
            return li.previousIndex();
        }
        return this.currentIndex - 1;
    }
    
    @Override
    public void remove() throws UnsupportedOperationException {
        if (this.iterator instanceof ListIterator) {
            this.iterator.remove();
            return;
        }
        int removeIndex = this.currentIndex;
        if (this.currentIndex == this.wrappedIteratorIndex) {
            --removeIndex;
        }
        if (!this.removeState || this.wrappedIteratorIndex - this.currentIndex > 1) {
            throw new IllegalStateException(MessageFormat.format("Cannot remove element at index {0}.", removeIndex));
        }
        this.iterator.remove();
        this.list.remove(removeIndex);
        this.currentIndex = removeIndex;
        --this.wrappedIteratorIndex;
        this.removeState = false;
    }
    
    @Override
    public void set(final E obj) throws UnsupportedOperationException {
        if (this.iterator instanceof ListIterator) {
            final ListIterator<E> li = (ListIterator)this.iterator;
            li.set(obj);
            return;
        }
        throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
    }
    
    @Override
    public void reset() {
        if (this.iterator instanceof ListIterator) {
            final ListIterator<?> li = (ListIterator)this.iterator;
            while (li.previousIndex() >= 0) {
                li.previous();
            }
            return;
        }
        this.currentIndex = 0;
    }
}
