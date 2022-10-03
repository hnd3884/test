package org.apache.commons.collections;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.AbstractCollection;

public class BoundedFifoBuffer extends AbstractCollection implements Buffer, BoundedCollection
{
    private final Object[] m_elements;
    private int m_start;
    private int m_end;
    private boolean m_full;
    private final int maxElements;
    
    public BoundedFifoBuffer() {
        this(32);
    }
    
    public BoundedFifoBuffer(final int size) {
        this.m_start = 0;
        this.m_end = 0;
        this.m_full = false;
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        this.m_elements = new Object[size];
        this.maxElements = this.m_elements.length;
    }
    
    public BoundedFifoBuffer(final Collection coll) {
        this(coll.size());
        this.addAll(coll);
    }
    
    public int size() {
        int size = 0;
        if (this.m_end < this.m_start) {
            size = this.maxElements - this.m_start + this.m_end;
        }
        else if (this.m_end == this.m_start) {
            size = (this.m_full ? this.maxElements : 0);
        }
        else {
            size = this.m_end - this.m_start;
        }
        return size;
    }
    
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    public boolean isFull() {
        return this.size() == this.maxElements;
    }
    
    public int maxSize() {
        return this.maxElements;
    }
    
    public void clear() {
        this.m_full = false;
        this.m_start = 0;
        this.m_end = 0;
        Arrays.fill(this.m_elements, null);
    }
    
    public boolean add(final Object element) {
        if (null == element) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }
        if (this.m_full) {
            throw new BufferOverflowException("The buffer cannot hold more than " + this.maxElements + " objects.");
        }
        this.m_elements[this.m_end++] = element;
        if (this.m_end >= this.maxElements) {
            this.m_end = 0;
        }
        if (this.m_end == this.m_start) {
            this.m_full = true;
        }
        return true;
    }
    
    public Object get() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        return this.m_elements[this.m_start];
    }
    
    public Object remove() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        final Object element = this.m_elements[this.m_start];
        if (null != element) {
            this.m_elements[this.m_start++] = null;
            if (this.m_start >= this.maxElements) {
                this.m_start = 0;
            }
            this.m_full = false;
        }
        return element;
    }
    
    private int increment(int index) {
        if (++index >= this.maxElements) {
            index = 0;
        }
        return index;
    }
    
    private int decrement(int index) {
        if (--index < 0) {
            index = this.maxElements - 1;
        }
        return index;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            private int index = BoundedFifoBuffer.this.m_start;
            private int lastReturnedIndex = -1;
            private boolean isFirst = BoundedFifoBuffer.this.m_full;
            
            public boolean hasNext() {
                return this.isFirst || this.index != BoundedFifoBuffer.this.m_end;
            }
            
            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.isFirst = false;
                this.lastReturnedIndex = this.index;
                this.index = BoundedFifoBuffer.this.increment(this.index);
                return BoundedFifoBuffer.this.m_elements[this.lastReturnedIndex];
            }
            
            public void remove() {
                if (this.lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                if (this.lastReturnedIndex == BoundedFifoBuffer.this.m_start) {
                    BoundedFifoBuffer.this.remove();
                    this.lastReturnedIndex = -1;
                    return;
                }
                int i = this.lastReturnedIndex + 1;
                while (i != BoundedFifoBuffer.this.m_end) {
                    if (i >= BoundedFifoBuffer.this.maxElements) {
                        BoundedFifoBuffer.this.m_elements[i - 1] = BoundedFifoBuffer.this.m_elements[0];
                        i = 0;
                    }
                    else {
                        BoundedFifoBuffer.this.m_elements[i - 1] = BoundedFifoBuffer.this.m_elements[i];
                        ++i;
                    }
                }
                this.lastReturnedIndex = -1;
                BoundedFifoBuffer.this.m_end = BoundedFifoBuffer.this.decrement(BoundedFifoBuffer.this.m_end);
                BoundedFifoBuffer.this.m_elements[BoundedFifoBuffer.this.m_end] = null;
                BoundedFifoBuffer.this.m_full = false;
                this.index = BoundedFifoBuffer.this.decrement(this.index);
            }
        };
    }
}
