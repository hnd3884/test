package org.glassfish.jersey.internal.guava;

import java.util.NoSuchElementException;
import java.util.AbstractList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public final class Lists
{
    private Lists() {
    }
    
    private static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }
    
    public static <E> ArrayList<E> newArrayList(final Iterable<? extends E> elements) {
        Preconditions.checkNotNull(elements);
        return (elements instanceof Collection) ? new ArrayList<E>(Collections2.cast(elements)) : newArrayList(elements.iterator());
    }
    
    public static <E> ArrayList<E> newArrayList(final Iterator<? extends E> elements) {
        final ArrayList<E> list = newArrayList();
        Iterators.addAll(list, elements);
        return list;
    }
    
    private static <T> List<T> reverse(final List<T> list) {
        if (list instanceof ReverseList) {
            return ((ReverseList)list).getForwardList();
        }
        return new ReverseList<T>(list);
    }
    
    static boolean equalsImpl(final List<?> list, final Object object) {
        if (object == Preconditions.checkNotNull(list)) {
            return true;
        }
        if (!(object instanceof List)) {
            return false;
        }
        final List<?> o = (List<?>)object;
        return list.size() == o.size() && Iterators.elementsEqual(list.iterator(), o.iterator());
    }
    
    static int indexOfImpl(final List<?> list, final Object element) {
        final ListIterator<?> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            if (Objects.equals(element, listIterator.next())) {
                return listIterator.previousIndex();
            }
        }
        return -1;
    }
    
    static int lastIndexOfImpl(final List<?> list, final Object element) {
        final ListIterator<?> listIterator = list.listIterator(list.size());
        while (listIterator.hasPrevious()) {
            if (Objects.equals(element, listIterator.previous())) {
                return listIterator.nextIndex();
            }
        }
        return -1;
    }
    
    private static class ReverseList<T> extends AbstractList<T>
    {
        private final List<T> forwardList;
        
        ReverseList(final List<T> forwardList) {
            this.forwardList = Preconditions.checkNotNull(forwardList);
        }
        
        List<T> getForwardList() {
            return this.forwardList;
        }
        
        private int reverseIndex(final int index) {
            final int size = this.size();
            Preconditions.checkElementIndex(index, size);
            return size - 1 - index;
        }
        
        private int reversePosition(final int index) {
            final int size = this.size();
            Preconditions.checkPositionIndex(index, size);
            return size - index;
        }
        
        @Override
        public void add(final int index, final T element) {
            this.forwardList.add(this.reversePosition(index), element);
        }
        
        @Override
        public void clear() {
            this.forwardList.clear();
        }
        
        @Override
        public T remove(final int index) {
            return this.forwardList.remove(this.reverseIndex(index));
        }
        
        @Override
        protected void removeRange(final int fromIndex, final int toIndex) {
            this.subList(fromIndex, toIndex).clear();
        }
        
        @Override
        public T set(final int index, final T element) {
            return this.forwardList.set(this.reverseIndex(index), element);
        }
        
        @Override
        public T get(final int index) {
            return this.forwardList.get(this.reverseIndex(index));
        }
        
        @Override
        public int size() {
            return this.forwardList.size();
        }
        
        @Override
        public List<T> subList(final int fromIndex, final int toIndex) {
            Preconditions.checkPositionIndexes(fromIndex, toIndex, this.size());
            return (List<T>)reverse((List<Object>)this.forwardList.subList(this.reversePosition(toIndex), this.reversePosition(fromIndex)));
        }
        
        @Override
        public Iterator<T> iterator() {
            return this.listIterator();
        }
        
        @Override
        public ListIterator<T> listIterator(final int index) {
            final int start = this.reversePosition(index);
            final ListIterator<T> forwardIterator = this.forwardList.listIterator(start);
            return new ListIterator<T>() {
                boolean canRemoveOrSet;
                
                @Override
                public void add(final T e) {
                    forwardIterator.add(e);
                    forwardIterator.previous();
                    this.canRemoveOrSet = false;
                }
                
                @Override
                public boolean hasNext() {
                    return forwardIterator.hasPrevious();
                }
                
                @Override
                public boolean hasPrevious() {
                    return forwardIterator.hasNext();
                }
                
                @Override
                public T next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.canRemoveOrSet = true;
                    return forwardIterator.previous();
                }
                
                @Override
                public int nextIndex() {
                    return ReverseList.this.reversePosition(forwardIterator.nextIndex());
                }
                
                @Override
                public T previous() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.canRemoveOrSet = true;
                    return forwardIterator.next();
                }
                
                @Override
                public int previousIndex() {
                    return this.nextIndex() - 1;
                }
                
                @Override
                public void remove() {
                    CollectPreconditions.checkRemove(this.canRemoveOrSet);
                    forwardIterator.remove();
                    this.canRemoveOrSet = false;
                }
                
                @Override
                public void set(final T e) {
                    Preconditions.checkState(this.canRemoveOrSet);
                    forwardIterator.set(e);
                }
            };
        }
    }
}
