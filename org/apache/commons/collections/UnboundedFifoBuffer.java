package org.apache.commons.collections;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractCollection;

public class UnboundedFifoBuffer extends AbstractCollection implements Buffer
{
    protected Object[] m_buffer;
    protected int m_head;
    protected int m_tail;
    
    public UnboundedFifoBuffer() {
        this(32);
    }
    
    public UnboundedFifoBuffer(final int initialSize) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        this.m_buffer = new Object[initialSize + 1];
        this.m_head = 0;
        this.m_tail = 0;
    }
    
    public int size() {
        int size = 0;
        if (this.m_tail < this.m_head) {
            size = this.m_buffer.length - this.m_head + this.m_tail;
        }
        else {
            size = this.m_tail - this.m_head;
        }
        return size;
    }
    
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    public boolean add(final Object obj) {
        if (obj == null) {
            throw new NullPointerException("Attempted to add null object to buffer");
        }
        if (this.size() + 1 >= this.m_buffer.length) {
            final Object[] tmp = new Object[(this.m_buffer.length - 1) * 2 + 1];
            int j = 0;
            for (int i = this.m_head; i != this.m_tail; i = 0) {
                tmp[j] = this.m_buffer[i];
                this.m_buffer[i] = null;
                ++j;
                if (++i == this.m_buffer.length) {}
            }
            this.m_buffer = tmp;
            this.m_head = 0;
            this.m_tail = j;
        }
        this.m_buffer[this.m_tail] = obj;
        ++this.m_tail;
        if (this.m_tail >= this.m_buffer.length) {
            this.m_tail = 0;
        }
        return true;
    }
    
    public Object get() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        return this.m_buffer[this.m_head];
    }
    
    public Object remove() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException("The buffer is already empty");
        }
        final Object element = this.m_buffer[this.m_head];
        if (null != element) {
            this.m_buffer[this.m_head] = null;
            ++this.m_head;
            if (this.m_head >= this.m_buffer.length) {
                this.m_head = 0;
            }
        }
        return element;
    }
    
    private int increment(int index) {
        if (++index >= this.m_buffer.length) {
            index = 0;
        }
        return index;
    }
    
    private int decrement(int index) {
        if (--index < 0) {
            index = this.m_buffer.length - 1;
        }
        return index;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            private int index = UnboundedFifoBuffer.this.m_head;
            private int lastReturnedIndex = -1;
            
            public boolean hasNext() {
                return this.index != UnboundedFifoBuffer.this.m_tail;
            }
            
            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturnedIndex = this.index;
                this.index = UnboundedFifoBuffer.this.increment(this.index);
                return UnboundedFifoBuffer.this.m_buffer[this.lastReturnedIndex];
            }
            
            public void remove() {
                if (this.lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                if (this.lastReturnedIndex == UnboundedFifoBuffer.this.m_head) {
                    UnboundedFifoBuffer.this.remove();
                    this.lastReturnedIndex = -1;
                    return;
                }
                for (int i = UnboundedFifoBuffer.this.increment(this.lastReturnedIndex); i != UnboundedFifoBuffer.this.m_tail; i = UnboundedFifoBuffer.this.increment(i)) {
                    UnboundedFifoBuffer.this.m_buffer[UnboundedFifoBuffer.this.decrement(i)] = UnboundedFifoBuffer.this.m_buffer[i];
                }
                this.lastReturnedIndex = -1;
                UnboundedFifoBuffer.this.m_tail = UnboundedFifoBuffer.this.decrement(UnboundedFifoBuffer.this.m_tail);
                UnboundedFifoBuffer.this.m_buffer[UnboundedFifoBuffer.this.m_tail] = null;
                this.index = UnboundedFifoBuffer.this.decrement(this.index);
            }
        };
    }
}
