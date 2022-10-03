package org.apache.lucene.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public final class MergedIterator<T extends Comparable<T>> implements Iterator<T>
{
    private T current;
    private final TermMergeQueue<T> queue;
    private final SubIterator<T>[] top;
    private final boolean removeDuplicates;
    private int numTop;
    
    public MergedIterator(final Iterator<T>... iterators) {
        this(true, (Iterator[])iterators);
    }
    
    public MergedIterator(final boolean removeDuplicates, final Iterator<T>... iterators) {
        this.removeDuplicates = removeDuplicates;
        this.queue = new TermMergeQueue<T>(iterators.length);
        this.top = new SubIterator[iterators.length];
        int index = 0;
        for (final Iterator<T> iterator : iterators) {
            if (iterator.hasNext()) {
                final SubIterator<T> sub = new SubIterator<T>();
                sub.current = iterator.next();
                sub.iterator = iterator;
                sub.index = index++;
                this.queue.add((SubIterator<C>)sub);
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        if (this.queue.size() > 0) {
            return true;
        }
        for (int i = 0; i < this.numTop; ++i) {
            if (this.top[i].iterator.hasNext()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public T next() {
        this.pushTop();
        if (this.queue.size() > 0) {
            this.pullTop();
        }
        else {
            this.current = null;
        }
        if (this.current == null) {
            throw new NoSuchElementException();
        }
        return this.current;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private void pullTop() {
        assert this.numTop == 0;
        this.top[this.numTop++] = (SubIterator)this.queue.pop();
        if (this.removeDuplicates) {
            while (this.queue.size() != 0 && this.queue.top().current.equals(this.top[0].current)) {
                this.top[this.numTop++] = (SubIterator)this.queue.pop();
            }
        }
        this.current = this.top[0].current;
    }
    
    private void pushTop() {
        for (int i = 0; i < this.numTop; ++i) {
            if (this.top[i].iterator.hasNext()) {
                this.top[i].current = this.top[i].iterator.next();
                this.queue.add((SubIterator<C>)this.top[i]);
            }
            else {
                this.top[i].current = null;
            }
        }
        this.numTop = 0;
    }
    
    private static class SubIterator<I extends Comparable<I>>
    {
        Iterator<I> iterator;
        I current;
        int index;
    }
    
    private static class TermMergeQueue<C extends Comparable<C>> extends PriorityQueue<SubIterator<C>>
    {
        TermMergeQueue(final int size) {
            super(size);
        }
        
        @Override
        protected boolean lessThan(final SubIterator<C> a, final SubIterator<C> b) {
            final int cmp = a.current.compareTo(b.current);
            if (cmp != 0) {
                return cmp < 0;
            }
            return a.index < b.index;
        }
    }
}
