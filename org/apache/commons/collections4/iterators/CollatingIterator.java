package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.list.UnmodifiableList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Comparator;
import java.util.Iterator;

public class CollatingIterator<E> implements Iterator<E>
{
    private Comparator<? super E> comparator;
    private List<Iterator<? extends E>> iterators;
    private List<E> values;
    private BitSet valueSet;
    private int lastReturned;
    
    public CollatingIterator() {
        this(null, 2);
    }
    
    public CollatingIterator(final Comparator<? super E> comp) {
        this(comp, 2);
    }
    
    public CollatingIterator(final Comparator<? super E> comp, final int initIterCapacity) {
        this.comparator = null;
        this.iterators = null;
        this.values = null;
        this.valueSet = null;
        this.lastReturned = -1;
        this.iterators = new ArrayList<Iterator<? extends E>>(initIterCapacity);
        this.setComparator(comp);
    }
    
    public CollatingIterator(final Comparator<? super E> comp, final Iterator<? extends E> a, final Iterator<? extends E> b) {
        this(comp, 2);
        this.addIterator(a);
        this.addIterator(b);
    }
    
    public CollatingIterator(final Comparator<? super E> comp, final Iterator<? extends E>[] iterators) {
        this(comp, iterators.length);
        for (final Iterator<? extends E> iterator : iterators) {
            this.addIterator(iterator);
        }
    }
    
    public CollatingIterator(final Comparator<? super E> comp, final Collection<Iterator<? extends E>> iterators) {
        this(comp, iterators.size());
        for (final Iterator<? extends E> iterator : iterators) {
            this.addIterator(iterator);
        }
    }
    
    public void addIterator(final Iterator<? extends E> iterator) {
        this.checkNotStarted();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iterators.add(iterator);
    }
    
    public void setIterator(final int index, final Iterator<? extends E> iterator) {
        this.checkNotStarted();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iterators.set(index, iterator);
    }
    
    public List<Iterator<? extends E>> getIterators() {
        return UnmodifiableList.unmodifiableList((List<? extends Iterator<? extends E>>)this.iterators);
    }
    
    public Comparator<? super E> getComparator() {
        return this.comparator;
    }
    
    public void setComparator(final Comparator<? super E> comp) {
        this.checkNotStarted();
        this.comparator = comp;
    }
    
    @Override
    public boolean hasNext() {
        this.start();
        return this.anyValueSet(this.valueSet) || this.anyHasNext(this.iterators);
    }
    
    @Override
    public E next() throws NoSuchElementException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final int leastIndex = this.least();
        if (leastIndex == -1) {
            throw new NoSuchElementException();
        }
        final E val = this.values.get(leastIndex);
        this.clear(leastIndex);
        this.lastReturned = leastIndex;
        return val;
    }
    
    @Override
    public void remove() {
        if (this.lastReturned == -1) {
            throw new IllegalStateException("No value can be removed at present");
        }
        this.iterators.get(this.lastReturned).remove();
    }
    
    public int getIteratorIndex() {
        if (this.lastReturned == -1) {
            throw new IllegalStateException("No value has been returned yet");
        }
        return this.lastReturned;
    }
    
    private void start() {
        if (this.values == null) {
            this.values = new ArrayList<E>(this.iterators.size());
            this.valueSet = new BitSet(this.iterators.size());
            for (int i = 0; i < this.iterators.size(); ++i) {
                this.values.add(null);
                this.valueSet.clear(i);
            }
        }
    }
    
    private boolean set(final int i) {
        final Iterator<? extends E> it = this.iterators.get(i);
        if (it.hasNext()) {
            this.values.set(i, (E)it.next());
            this.valueSet.set(i);
            return true;
        }
        this.values.set(i, null);
        this.valueSet.clear(i);
        return false;
    }
    
    private void clear(final int i) {
        this.values.set(i, null);
        this.valueSet.clear(i);
    }
    
    private void checkNotStarted() throws IllegalStateException {
        if (this.values != null) {
            throw new IllegalStateException("Can't do that after next or hasNext has been called.");
        }
    }
    
    private int least() {
        int leastIndex = -1;
        E leastObject = null;
        for (int i = 0; i < this.values.size(); ++i) {
            if (!this.valueSet.get(i)) {
                this.set(i);
            }
            if (this.valueSet.get(i)) {
                if (leastIndex == -1) {
                    leastIndex = i;
                    leastObject = this.values.get(i);
                }
                else {
                    final E curObject = this.values.get(i);
                    if (this.comparator == null) {
                        throw new NullPointerException("You must invoke setComparator() to set a comparator first.");
                    }
                    if (this.comparator.compare((Object)curObject, (Object)leastObject) < 0) {
                        leastObject = curObject;
                        leastIndex = i;
                    }
                }
            }
        }
        return leastIndex;
    }
    
    private boolean anyValueSet(final BitSet set) {
        for (int i = 0; i < set.size(); ++i) {
            if (set.get(i)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean anyHasNext(final List<Iterator<? extends E>> iters) {
        for (final Iterator<? extends E> iterator : iters) {
            if (iterator.hasNext()) {
                return true;
            }
        }
        return false;
    }
}
