package org.apache.commons.collections.buffer;

import java.util.NoSuchElementException;
import java.util.Iterator;
import org.apache.commons.collections.BufferUnderflowException;
import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections.Buffer;
import java.util.AbstractCollection;

public class PriorityBuffer extends AbstractCollection implements Buffer, Serializable
{
    private static final long serialVersionUID = 6891186490470027896L;
    private static final int DEFAULT_CAPACITY = 13;
    protected Object[] elements;
    protected int size;
    protected boolean ascendingOrder;
    protected Comparator comparator;
    
    public PriorityBuffer() {
        this(13, true, null);
    }
    
    public PriorityBuffer(final Comparator comparator) {
        this(13, true, comparator);
    }
    
    public PriorityBuffer(final boolean ascendingOrder) {
        this(13, ascendingOrder, null);
    }
    
    public PriorityBuffer(final boolean ascendingOrder, final Comparator comparator) {
        this(13, ascendingOrder, comparator);
    }
    
    public PriorityBuffer(final int capacity) {
        this(capacity, true, null);
    }
    
    public PriorityBuffer(final int capacity, final Comparator comparator) {
        this(capacity, true, comparator);
    }
    
    public PriorityBuffer(final int capacity, final boolean ascendingOrder) {
        this(capacity, ascendingOrder, null);
    }
    
    public PriorityBuffer(final int capacity, final boolean ascendingOrder, final Comparator comparator) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity");
        }
        this.ascendingOrder = ascendingOrder;
        this.elements = new Object[capacity + 1];
        this.comparator = comparator;
    }
    
    public boolean isAscendingOrder() {
        return this.ascendingOrder;
    }
    
    public Comparator comparator() {
        return this.comparator;
    }
    
    public int size() {
        return this.size;
    }
    
    public void clear() {
        this.elements = new Object[this.elements.length];
        this.size = 0;
    }
    
    public boolean add(final Object element) {
        if (this.isAtCapacity()) {
            this.grow();
        }
        if (this.ascendingOrder) {
            this.percolateUpMinHeap(element);
        }
        else {
            this.percolateUpMaxHeap(element);
        }
        return true;
    }
    
    public Object get() {
        if (this.isEmpty()) {
            throw new BufferUnderflowException();
        }
        return this.elements[1];
    }
    
    public Object remove() {
        final Object result = this.get();
        this.elements[1] = this.elements[this.size--];
        this.elements[this.size + 1] = null;
        if (this.size != 0) {
            if (this.ascendingOrder) {
                this.percolateDownMinHeap(1);
            }
            else {
                this.percolateDownMaxHeap(1);
            }
        }
        return result;
    }
    
    protected boolean isAtCapacity() {
        return this.elements.length == this.size + 1;
    }
    
    protected void percolateDownMinHeap(final int index) {
        final Object element = this.elements[index];
        int hole;
        int child;
        for (hole = index; hole * 2 <= this.size; hole = child) {
            child = hole * 2;
            if (child != this.size && this.compare(this.elements[child + 1], this.elements[child]) < 0) {
                ++child;
            }
            if (this.compare(this.elements[child], element) >= 0) {
                break;
            }
            this.elements[hole] = this.elements[child];
        }
        this.elements[hole] = element;
    }
    
    protected void percolateDownMaxHeap(final int index) {
        final Object element = this.elements[index];
        int hole;
        int child;
        for (hole = index; hole * 2 <= this.size; hole = child) {
            child = hole * 2;
            if (child != this.size && this.compare(this.elements[child + 1], this.elements[child]) > 0) {
                ++child;
            }
            if (this.compare(this.elements[child], element) <= 0) {
                break;
            }
            this.elements[hole] = this.elements[child];
        }
        this.elements[hole] = element;
    }
    
    protected void percolateUpMinHeap(final int index) {
        int hole;
        Object element;
        int next;
        for (hole = index, element = this.elements[hole]; hole > 1 && this.compare(element, this.elements[hole / 2]) < 0; hole = next) {
            next = hole / 2;
            this.elements[hole] = this.elements[next];
        }
        this.elements[hole] = element;
    }
    
    protected void percolateUpMinHeap(final Object element) {
        this.elements[++this.size] = element;
        this.percolateUpMinHeap(this.size);
    }
    
    protected void percolateUpMaxHeap(final int index) {
        int hole;
        Object element;
        int next;
        for (hole = index, element = this.elements[hole]; hole > 1 && this.compare(element, this.elements[hole / 2]) > 0; hole = next) {
            next = hole / 2;
            this.elements[hole] = this.elements[next];
        }
        this.elements[hole] = element;
    }
    
    protected void percolateUpMaxHeap(final Object element) {
        this.elements[++this.size] = element;
        this.percolateUpMaxHeap(this.size);
    }
    
    protected int compare(final Object a, final Object b) {
        if (this.comparator != null) {
            return this.comparator.compare(a, b);
        }
        return ((Comparable)a).compareTo(b);
    }
    
    protected void grow() {
        final Object[] array = new Object[this.elements.length * 2];
        System.arraycopy(this.elements, 0, array, 0, this.elements.length);
        this.elements = array;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            private int index = 1;
            private int lastReturnedIndex = -1;
            
            public boolean hasNext() {
                return this.index <= PriorityBuffer.this.size;
            }
            
            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturnedIndex = this.index;
                ++this.index;
                return PriorityBuffer.this.elements[this.lastReturnedIndex];
            }
            
            public void remove() {
                if (this.lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                PriorityBuffer.this.elements[this.lastReturnedIndex] = PriorityBuffer.this.elements[PriorityBuffer.this.size];
                PriorityBuffer.this.elements[PriorityBuffer.this.size] = null;
                final PriorityBuffer this$0 = PriorityBuffer.this;
                --this$0.size;
                if (PriorityBuffer.this.size != 0 && this.lastReturnedIndex <= PriorityBuffer.this.size) {
                    int compareToParent = 0;
                    if (this.lastReturnedIndex > 1) {
                        compareToParent = PriorityBuffer.this.compare(PriorityBuffer.this.elements[this.lastReturnedIndex], PriorityBuffer.this.elements[this.lastReturnedIndex / 2]);
                    }
                    if (PriorityBuffer.this.ascendingOrder) {
                        if (this.lastReturnedIndex > 1 && compareToParent < 0) {
                            PriorityBuffer.this.percolateUpMinHeap(this.lastReturnedIndex);
                        }
                        else {
                            PriorityBuffer.this.percolateDownMinHeap(this.lastReturnedIndex);
                        }
                    }
                    else if (this.lastReturnedIndex > 1 && compareToParent > 0) {
                        PriorityBuffer.this.percolateUpMaxHeap(this.lastReturnedIndex);
                    }
                    else {
                        PriorityBuffer.this.percolateDownMaxHeap(this.lastReturnedIndex);
                    }
                }
                --this.index;
                this.lastReturnedIndex = -1;
            }
        };
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        for (int i = 1; i < this.size + 1; ++i) {
            if (i != 1) {
                sb.append(", ");
            }
            sb.append(this.elements[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }
}
