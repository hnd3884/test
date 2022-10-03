package org.glassfish.jersey.internal.guava;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.Iterator;

public final class Iterators
{
    private static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR;
    private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR;
    
    private Iterators() {
    }
    
    @Deprecated
    public static <T> UnmodifiableIterator<T> emptyIterator() {
        return (UnmodifiableIterator<T>)emptyListIterator();
    }
    
    private static <T> UnmodifiableListIterator<T> emptyListIterator() {
        return (UnmodifiableListIterator<T>)Iterators.EMPTY_LIST_ITERATOR;
    }
    
    static <T> Iterator<T> emptyModifiableIterator() {
        return (Iterator<T>)Iterators.EMPTY_MODIFIABLE_ITERATOR;
    }
    
    public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        if (iterator instanceof UnmodifiableIterator) {
            return (UnmodifiableIterator)iterator;
        }
        return new UnmodifiableIterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            
            @Override
            public T next() {
                return iterator.next();
            }
        };
    }
    
    public static int size(final Iterator<?> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++count;
        }
        return count;
    }
    
    public static boolean removeAll(final Iterator<?> removeFrom, final Collection<?> elementsToRemove) {
        return removeIf(removeFrom, Predicates.in(elementsToRemove));
    }
    
    public static <T> boolean removeIf(final Iterator<T> removeFrom, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        boolean modified = false;
        while (removeFrom.hasNext()) {
            if (predicate.test(removeFrom.next())) {
                removeFrom.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    public static boolean elementsEqual(final Iterator<?> iterator1, final Iterator<?> iterator2) {
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            final Object o1 = iterator1.next();
            final Object o2 = iterator2.next();
            if (!Objects.equals(o1, o2)) {
                return false;
            }
        }
        return !iterator2.hasNext();
    }
    
    public static <T> boolean addAll(final Collection<T> addTo, final Iterator<? extends T> iterator) {
        Preconditions.checkNotNull(addTo);
        Preconditions.checkNotNull(iterator);
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add((T)iterator.next());
        }
        return wasModified;
    }
    
    public static <T> boolean all(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        while (iterator.hasNext()) {
            final T element = iterator.next();
            if (!predicate.test(element)) {
                return false;
            }
        }
        return true;
    }
    
    private static <T> int indexOf(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate, (Object)"predicate");
        int i = 0;
        while (iterator.hasNext()) {
            final T current = iterator.next();
            if (predicate.test(current)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public static <F, T> Iterator<T> transform(final Iterator<F> fromIterator, final Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(function);
        return new TransformedIterator<F, T>(fromIterator) {
            @Override
            T transform(final F from) {
                return function.apply(from);
            }
        };
    }
    
    public static <T> T getNext(final Iterator<? extends T> iterator, final T defaultValue) {
        return iterator.hasNext() ? iterator.next() : defaultValue;
    }
    
    static <T> T pollNext(final Iterator<T> iterator) {
        if (iterator.hasNext()) {
            final T result = iterator.next();
            iterator.remove();
            return result;
        }
        return null;
    }
    
    static void clear(final Iterator<?> iterator) {
        Preconditions.checkNotNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
    
    public static <T> UnmodifiableIterator<T> forArray(final T... array) {
        return forArray(array, 0, array.length, 0);
    }
    
    static <T> UnmodifiableListIterator<T> forArray(final T[] array, final int offset, final int length, final int index) {
        Preconditions.checkArgument(length >= 0);
        final int end = offset + length;
        Preconditions.checkPositionIndexes(offset, end, array.length);
        Preconditions.checkPositionIndex(index, length);
        if (length == 0) {
            return emptyListIterator();
        }
        return new AbstractIndexedListIterator<T>(length, index) {
            @Override
            protected T get(final int index) {
                return array[offset + index];
            }
        };
    }
    
    public static <T> UnmodifiableIterator<T> singletonIterator(final T value) {
        return new UnmodifiableIterator<T>() {
            boolean done;
            
            @Override
            public boolean hasNext() {
                return !this.done;
            }
            
            @Override
            public T next() {
                if (this.done) {
                    throw new NoSuchElementException();
                }
                this.done = true;
                return value;
            }
        };
    }
    
    public static <T> PeekingIterator<T> peekingIterator(final Iterator<? extends T> iterator) {
        if (iterator instanceof PeekingImpl) {
            final PeekingImpl<T> peeking = (PeekingImpl)iterator;
            return peeking;
        }
        return new PeekingImpl<T>(iterator);
    }
    
    static {
        EMPTY_LIST_ITERATOR = new UnmodifiableListIterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public Object next() {
                throw new NoSuchElementException();
            }
            
            @Override
            public boolean hasPrevious() {
                return false;
            }
            
            @Override
            public Object previous() {
                throw new NoSuchElementException();
            }
            
            @Override
            public int nextIndex() {
                return 0;
            }
            
            @Override
            public int previousIndex() {
                return -1;
            }
        };
        EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public Object next() {
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                CollectPreconditions.checkRemove(false);
            }
        };
    }
    
    private static class PeekingImpl<E> implements PeekingIterator<E>
    {
        private final Iterator<? extends E> iterator;
        private boolean hasPeeked;
        private E peekedElement;
        
        public PeekingImpl(final Iterator<? extends E> iterator) {
            this.iterator = Preconditions.checkNotNull(iterator);
        }
        
        @Override
        public boolean hasNext() {
            return this.hasPeeked || this.iterator.hasNext();
        }
        
        @Override
        public E next() {
            if (!this.hasPeeked) {
                return (E)this.iterator.next();
            }
            final E result = this.peekedElement;
            this.hasPeeked = false;
            this.peekedElement = null;
            return result;
        }
        
        @Override
        public void remove() {
            Preconditions.checkState(!this.hasPeeked, (Object)"Can't remove after you've peeked at next");
            this.iterator.remove();
        }
        
        @Override
        public E peek() {
            if (!this.hasPeeked) {
                this.peekedElement = (E)this.iterator.next();
                this.hasPeeked = true;
            }
            return this.peekedElement;
        }
    }
}
