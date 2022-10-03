package sun.awt.util;

import java.util.ConcurrentModificationException;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.AbstractSequentialList;

public class IdentityLinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>
{
    private transient Entry<E> header;
    private transient int size;
    
    public IdentityLinkedList() {
        this.header = new Entry<E>(null, null, null);
        this.size = 0;
        final Entry<E> header = this.header;
        final Entry<E> header2 = this.header;
        final Entry<E> header3 = this.header;
        header2.previous = header3;
        header.next = header3;
    }
    
    public IdentityLinkedList(final Collection<? extends E> collection) {
        this();
        this.addAll(collection);
    }
    
    @Override
    public E getFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.header.next.element;
    }
    
    @Override
    public E getLast() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.header.previous.element;
    }
    
    @Override
    public E removeFirst() {
        return this.remove(this.header.next);
    }
    
    @Override
    public E removeLast() {
        return this.remove(this.header.previous);
    }
    
    @Override
    public void addFirst(final E e) {
        this.addBefore(e, this.header.next);
    }
    
    @Override
    public void addLast(final E e) {
        this.addBefore(e, this.header);
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.indexOf(o) != -1;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean add(final E e) {
        this.addBefore(e, this.header);
        return true;
    }
    
    @Override
    public boolean remove(final Object o) {
        for (Entry<E> entry = this.header.next; entry != this.header; entry = entry.next) {
            if (o == entry.element) {
                this.remove(entry);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return this.addAll(this.size, collection);
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends E> collection) {
        if (n < 0 || n > this.size) {
            throw new IndexOutOfBoundsException("Index: " + n + ", Size: " + this.size);
        }
        final Object[] array = collection.toArray();
        final int length = array.length;
        if (length == 0) {
            return false;
        }
        ++this.modCount;
        final Entry<E> entry = (n == this.size) ? this.header : this.entry(n);
        Entry<Object> previous = (Entry<Object>)entry.previous;
        for (int i = 0; i < length; ++i) {
            final Entry next = new Entry<Object>(array[i], entry, previous);
            previous.next = (Entry<Object>)next;
            previous = (Entry<Object>)next;
        }
        entry.previous = (Entry<E>)previous;
        this.size += length;
        return true;
    }
    
    @Override
    public void clear() {
        Entry<E> next2;
        for (Entry<E> next = this.header.next; next != this.header; next = next2) {
            next2 = next.next;
            final Entry<E> entry = next;
            final Entry<E> entry2 = next;
            final Entry<E> entry3 = null;
            entry2.previous = (Entry<E>)entry3;
            entry.next = (Entry<E>)entry3;
            next.element = null;
        }
        final Entry<E> header = this.header;
        final Entry<E> header2 = this.header;
        final Entry<E> header3 = this.header;
        header2.previous = header3;
        header.next = header3;
        this.size = 0;
        ++this.modCount;
    }
    
    @Override
    public E get(final int n) {
        return this.entry(n).element;
    }
    
    @Override
    public E set(final int n, final E element) {
        final Entry<E> entry = this.entry(n);
        final E element2 = entry.element;
        entry.element = element;
        return element2;
    }
    
    @Override
    public void add(final int n, final E e) {
        this.addBefore(e, (n == this.size) ? this.header : this.entry(n));
    }
    
    @Override
    public E remove(final int n) {
        return this.remove(this.entry(n));
    }
    
    private Entry<E> entry(final int n) {
        if (n < 0 || n >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + n + ", Size: " + this.size);
        }
        Entry<E> entry = this.header;
        if (n < this.size >> 1) {
            for (int i = 0; i <= n; ++i) {
                entry = entry.next;
            }
        }
        else {
            for (int j = this.size; j > n; --j) {
                entry = entry.previous;
            }
        }
        return entry;
    }
    
    @Override
    public int indexOf(final Object o) {
        int n = 0;
        for (Entry<E> entry = this.header.next; entry != this.header; entry = entry.next) {
            if (o == entry.element) {
                return n;
            }
            ++n;
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        int size = this.size;
        for (Entry<E> entry = this.header.previous; entry != this.header; entry = entry.previous) {
            --size;
            if (o == entry.element) {
                return size;
            }
        }
        return -1;
    }
    
    @Override
    public E peek() {
        if (this.size == 0) {
            return null;
        }
        return this.getFirst();
    }
    
    @Override
    public E element() {
        return this.getFirst();
    }
    
    @Override
    public E poll() {
        if (this.size == 0) {
            return null;
        }
        return this.removeFirst();
    }
    
    @Override
    public E remove() {
        return this.removeFirst();
    }
    
    @Override
    public boolean offer(final E e) {
        return this.add(e);
    }
    
    @Override
    public boolean offerFirst(final E e) {
        this.addFirst(e);
        return true;
    }
    
    @Override
    public boolean offerLast(final E e) {
        this.addLast(e);
        return true;
    }
    
    @Override
    public E peekFirst() {
        if (this.size == 0) {
            return null;
        }
        return this.getFirst();
    }
    
    @Override
    public E peekLast() {
        if (this.size == 0) {
            return null;
        }
        return this.getLast();
    }
    
    @Override
    public E pollFirst() {
        if (this.size == 0) {
            return null;
        }
        return this.removeFirst();
    }
    
    @Override
    public E pollLast() {
        if (this.size == 0) {
            return null;
        }
        return this.removeLast();
    }
    
    @Override
    public void push(final E e) {
        this.addFirst(e);
    }
    
    @Override
    public E pop() {
        return this.removeFirst();
    }
    
    @Override
    public boolean removeFirstOccurrence(final Object o) {
        return this.remove(o);
    }
    
    @Override
    public boolean removeLastOccurrence(final Object o) {
        for (Entry<E> entry = this.header.previous; entry != this.header; entry = entry.previous) {
            if (o == entry.element) {
                this.remove(entry);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ListIterator<E> listIterator(final int n) {
        return new ListItr(n);
    }
    
    private Entry<E> addBefore(final E e, final Entry<E> entry) {
        final Entry entry2 = new Entry((E)e, (Entry<E>)entry, (Entry<E>)entry.previous);
        entry2.previous.next = entry2;
        entry2.next.previous = entry2;
        ++this.size;
        ++this.modCount;
        return entry2;
    }
    
    private E remove(final Entry<E> entry) {
        if (entry == this.header) {
            throw new NoSuchElementException();
        }
        final E element = entry.element;
        entry.previous.next = entry.next;
        entry.next.previous = entry.previous;
        final Entry<E> entry2 = null;
        entry.previous = (Entry<E>)entry2;
        entry.next = (Entry<E>)entry2;
        entry.element = null;
        --this.size;
        ++this.modCount;
        return element;
    }
    
    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }
    
    @Override
    public Object[] toArray() {
        final Object[] array = new Object[this.size];
        int n = 0;
        for (Entry<E> entry = this.header.next; entry != this.header; entry = entry.next) {
            array[n++] = entry.element;
        }
        return array;
    }
    
    @Override
    public <T> T[] toArray(T[] array) {
        if (array.length < this.size) {
            array = (T[])Array.newInstance(array.getClass().getComponentType(), this.size);
        }
        int n = 0;
        final T[] array2 = array;
        for (Entry<E> entry = this.header.next; entry != this.header; entry = entry.next) {
            array2[n++] = (T)entry.element;
        }
        if (array.length > this.size) {
            array[this.size] = null;
        }
        return array;
    }
    
    private class ListItr implements ListIterator<E>
    {
        private Entry<E> lastReturned;
        private Entry<E> next;
        private int nextIndex;
        private int expectedModCount;
        
        ListItr(final int n) {
            this.lastReturned = IdentityLinkedList.this.header;
            this.expectedModCount = IdentityLinkedList.this.modCount;
            if (n < 0 || n > IdentityLinkedList.this.size) {
                throw new IndexOutOfBoundsException("Index: " + n + ", Size: " + IdentityLinkedList.this.size);
            }
            if (n < IdentityLinkedList.this.size >> 1) {
                this.next = IdentityLinkedList.this.header.next;
                this.nextIndex = 0;
                while (this.nextIndex < n) {
                    this.next = this.next.next;
                    ++this.nextIndex;
                }
            }
            else {
                this.next = IdentityLinkedList.this.header;
                this.nextIndex = IdentityLinkedList.this.size;
                while (this.nextIndex > n) {
                    this.next = this.next.previous;
                    --this.nextIndex;
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.nextIndex != IdentityLinkedList.this.size;
        }
        
        @Override
        public E next() {
            this.checkForComodification();
            if (this.nextIndex == IdentityLinkedList.this.size) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.next;
            this.next = this.next.next;
            ++this.nextIndex;
            return this.lastReturned.element;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.nextIndex != 0;
        }
        
        @Override
        public E previous() {
            if (this.nextIndex == 0) {
                throw new NoSuchElementException();
            }
            final Entry<E> previous = this.next.previous;
            this.next = previous;
            this.lastReturned = previous;
            --this.nextIndex;
            this.checkForComodification();
            return this.lastReturned.element;
        }
        
        @Override
        public int nextIndex() {
            return this.nextIndex;
        }
        
        @Override
        public int previousIndex() {
            return this.nextIndex - 1;
        }
        
        @Override
        public void remove() {
            this.checkForComodification();
            final Entry<E> next = this.lastReturned.next;
            try {
                IdentityLinkedList.this.remove(this.lastReturned);
            }
            catch (final NoSuchElementException ex) {
                throw new IllegalStateException();
            }
            if (this.next == this.lastReturned) {
                this.next = next;
            }
            else {
                --this.nextIndex;
            }
            this.lastReturned = IdentityLinkedList.this.header;
            ++this.expectedModCount;
        }
        
        @Override
        public void set(final E element) {
            if (this.lastReturned == IdentityLinkedList.this.header) {
                throw new IllegalStateException();
            }
            this.checkForComodification();
            this.lastReturned.element = element;
        }
        
        @Override
        public void add(final E e) {
            this.checkForComodification();
            this.lastReturned = IdentityLinkedList.this.header;
            IdentityLinkedList.this.addBefore(e, this.next);
            ++this.nextIndex;
            ++this.expectedModCount;
        }
        
        final void checkForComodification() {
            if (IdentityLinkedList.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    private static class Entry<E>
    {
        E element;
        Entry<E> next;
        Entry<E> previous;
        
        Entry(final E element, final Entry<E> next, final Entry<E> previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }
    
    private class DescendingIterator implements Iterator
    {
        final ListItr itr;
        
        private DescendingIterator() {
            this.itr = new ListItr(IdentityLinkedList.this.size());
        }
        
        @Override
        public boolean hasNext() {
            return this.itr.hasPrevious();
        }
        
        @Override
        public E next() {
            return this.itr.previous();
        }
        
        @Override
        public void remove() {
            this.itr.remove();
        }
    }
}
