package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.ResettableIterator;
import java.util.Iterator;

public class IteratorIterable<E> implements Iterable<E>
{
    private final Iterator<? extends E> iterator;
    private final Iterator<E> typeSafeIterator;
    
    private static <E> Iterator<E> createTypesafeIterator(final Iterator<? extends E> iterator) {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            
            @Override
            public E next() {
                return iterator.next();
            }
            
            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }
    
    public IteratorIterable(final Iterator<? extends E> iterator) {
        this(iterator, false);
    }
    
    public IteratorIterable(final Iterator<? extends E> iterator, final boolean multipleUse) {
        if (multipleUse && !(iterator instanceof ResettableIterator)) {
            this.iterator = (Iterator<? extends E>)new ListIteratorWrapper<E>(iterator);
        }
        else {
            this.iterator = iterator;
        }
        this.typeSafeIterator = createTypesafeIterator(this.iterator);
    }
    
    @Override
    public Iterator<E> iterator() {
        if (this.iterator instanceof ResettableIterator) {
            ((ResettableIterator)this.iterator).reset();
        }
        return this.typeSafeIterator;
    }
}
