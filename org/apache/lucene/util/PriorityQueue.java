package org.apache.lucene.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public abstract class PriorityQueue<T> implements Iterable<T>
{
    private int size;
    private final int maxSize;
    private final T[] heap;
    
    public PriorityQueue(final int maxSize) {
        this(maxSize, true);
    }
    
    public PriorityQueue(final int maxSize, final boolean prepopulate) {
        this.size = 0;
        int heapSize;
        if (0 == maxSize) {
            heapSize = 2;
        }
        else {
            heapSize = maxSize + 1;
            if (heapSize > ArrayUtil.MAX_ARRAY_LENGTH) {
                throw new IllegalArgumentException("maxSize must be <= " + (ArrayUtil.MAX_ARRAY_LENGTH - 1) + "; got: " + maxSize);
            }
        }
        final T[] h = (T[])new Object[heapSize];
        this.heap = h;
        this.maxSize = maxSize;
        if (prepopulate) {
            final T sentinel = this.getSentinelObject();
            if (sentinel != null) {
                this.heap[1] = sentinel;
                for (int i = 2; i < this.heap.length; ++i) {
                    this.heap[i] = this.getSentinelObject();
                }
                this.size = maxSize;
            }
        }
    }
    
    protected abstract boolean lessThan(final T p0, final T p1);
    
    protected T getSentinelObject() {
        return null;
    }
    
    public final T add(final T element) {
        ++this.size;
        this.heap[this.size] = element;
        this.upHeap(this.size);
        return this.heap[1];
    }
    
    public T insertWithOverflow(final T element) {
        if (this.size < this.maxSize) {
            this.add(element);
            return null;
        }
        if (this.size > 0 && !this.lessThan(element, this.heap[1])) {
            final T ret = this.heap[1];
            this.heap[1] = element;
            this.updateTop();
            return ret;
        }
        return element;
    }
    
    public final T top() {
        return this.heap[1];
    }
    
    public final T pop() {
        if (this.size > 0) {
            final T result = this.heap[1];
            this.heap[1] = this.heap[this.size];
            this.heap[this.size] = null;
            --this.size;
            this.downHeap(1);
            return result;
        }
        return null;
    }
    
    public final T updateTop() {
        this.downHeap(1);
        return this.heap[1];
    }
    
    public final T updateTop(final T newTop) {
        this.heap[1] = newTop;
        return this.updateTop();
    }
    
    public final int size() {
        return this.size;
    }
    
    public final void clear() {
        for (int i = 0; i <= this.size; ++i) {
            this.heap[i] = null;
        }
        this.size = 0;
    }
    
    public final boolean remove(final T element) {
        for (int i = 1; i <= this.size; ++i) {
            if (this.heap[i] == element) {
                this.heap[i] = this.heap[this.size];
                this.heap[this.size] = null;
                --this.size;
                if (i <= this.size && !this.upHeap(i)) {
                    this.downHeap(i);
                }
                return true;
            }
        }
        return false;
    }
    
    private final boolean upHeap(final int origPos) {
        int i = origPos;
        final T node = this.heap[i];
        for (int j = i >>> 1; j > 0 && this.lessThan(node, this.heap[j]); j >>>= 1) {
            this.heap[i] = this.heap[j];
            i = j;
        }
        this.heap[i] = node;
        return i != origPos;
    }
    
    private final void downHeap(int i) {
        final T node = this.heap[i];
        int j = i << 1;
        int k = j + 1;
        if (k <= this.size && this.lessThan(this.heap[k], this.heap[j])) {
            j = k;
        }
        while (j <= this.size && this.lessThan(this.heap[j], node)) {
            this.heap[i] = this.heap[j];
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= this.size && this.lessThan(this.heap[k], this.heap[j])) {
                j = k;
            }
        }
        this.heap[i] = node;
    }
    
    protected final Object[] getHeapArray() {
        return this.heap;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int i = 1;
            
            @Override
            public boolean hasNext() {
                return this.i <= PriorityQueue.this.size;
            }
            
            @Override
            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return PriorityQueue.this.heap[this.i++];
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
