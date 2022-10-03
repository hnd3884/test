package org.apache.commons.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Comparator;
import java.util.AbstractCollection;

public final class BinaryHeap extends AbstractCollection implements PriorityQueue, Buffer
{
    private static final int DEFAULT_CAPACITY = 13;
    int m_size;
    Object[] m_elements;
    boolean m_isMinHeap;
    Comparator m_comparator;
    
    public BinaryHeap() {
        this(13, true);
    }
    
    public BinaryHeap(final Comparator comparator) {
        this();
        this.m_comparator = comparator;
    }
    
    public BinaryHeap(final int capacity) {
        this(capacity, true);
    }
    
    public BinaryHeap(final int capacity, final Comparator comparator) {
        this(capacity);
        this.m_comparator = comparator;
    }
    
    public BinaryHeap(final boolean isMinHeap) {
        this(13, isMinHeap);
    }
    
    public BinaryHeap(final boolean isMinHeap, final Comparator comparator) {
        this(isMinHeap);
        this.m_comparator = comparator;
    }
    
    public BinaryHeap(final int capacity, final boolean isMinHeap) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity");
        }
        this.m_isMinHeap = isMinHeap;
        this.m_elements = new Object[capacity + 1];
    }
    
    public BinaryHeap(final int capacity, final boolean isMinHeap, final Comparator comparator) {
        this(capacity, isMinHeap);
        this.m_comparator = comparator;
    }
    
    public void clear() {
        this.m_elements = new Object[this.m_elements.length];
        this.m_size = 0;
    }
    
    public boolean isEmpty() {
        return this.m_size == 0;
    }
    
    public boolean isFull() {
        return this.m_elements.length == this.m_size + 1;
    }
    
    public void insert(final Object element) {
        if (this.isFull()) {
            this.grow();
        }
        if (this.m_isMinHeap) {
            this.percolateUpMinHeap(element);
        }
        else {
            this.percolateUpMaxHeap(element);
        }
    }
    
    public Object peek() throws NoSuchElementException {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.m_elements[1];
    }
    
    public Object pop() throws NoSuchElementException {
        final Object result = this.peek();
        this.m_elements[1] = this.m_elements[this.m_size--];
        this.m_elements[this.m_size + 1] = null;
        if (this.m_size != 0) {
            if (this.m_isMinHeap) {
                this.percolateDownMinHeap(1);
            }
            else {
                this.percolateDownMaxHeap(1);
            }
        }
        return result;
    }
    
    protected void percolateDownMinHeap(final int index) {
        final Object element = this.m_elements[index];
        int hole;
        int child;
        for (hole = index; hole * 2 <= this.m_size; hole = child) {
            child = hole * 2;
            if (child != this.m_size && this.compare(this.m_elements[child + 1], this.m_elements[child]) < 0) {
                ++child;
            }
            if (this.compare(this.m_elements[child], element) >= 0) {
                break;
            }
            this.m_elements[hole] = this.m_elements[child];
        }
        this.m_elements[hole] = element;
    }
    
    protected void percolateDownMaxHeap(final int index) {
        final Object element = this.m_elements[index];
        int hole;
        int child;
        for (hole = index; hole * 2 <= this.m_size; hole = child) {
            child = hole * 2;
            if (child != this.m_size && this.compare(this.m_elements[child + 1], this.m_elements[child]) > 0) {
                ++child;
            }
            if (this.compare(this.m_elements[child], element) <= 0) {
                break;
            }
            this.m_elements[hole] = this.m_elements[child];
        }
        this.m_elements[hole] = element;
    }
    
    protected void percolateUpMinHeap(final int index) {
        int hole;
        Object element;
        int next;
        for (hole = index, element = this.m_elements[hole]; hole > 1 && this.compare(element, this.m_elements[hole / 2]) < 0; hole = next) {
            next = hole / 2;
            this.m_elements[hole] = this.m_elements[next];
        }
        this.m_elements[hole] = element;
    }
    
    protected void percolateUpMinHeap(final Object element) {
        this.m_elements[++this.m_size] = element;
        this.percolateUpMinHeap(this.m_size);
    }
    
    protected void percolateUpMaxHeap(final int index) {
        int hole;
        Object element;
        int next;
        for (hole = index, element = this.m_elements[hole]; hole > 1 && this.compare(element, this.m_elements[hole / 2]) > 0; hole = next) {
            next = hole / 2;
            this.m_elements[hole] = this.m_elements[next];
        }
        this.m_elements[hole] = element;
    }
    
    protected void percolateUpMaxHeap(final Object element) {
        this.m_elements[++this.m_size] = element;
        this.percolateUpMaxHeap(this.m_size);
    }
    
    private int compare(final Object a, final Object b) {
        if (this.m_comparator != null) {
            return this.m_comparator.compare(a, b);
        }
        return ((Comparable)a).compareTo(b);
    }
    
    protected void grow() {
        final Object[] elements = new Object[this.m_elements.length * 2];
        System.arraycopy(this.m_elements, 0, elements, 0, this.m_elements.length);
        this.m_elements = elements;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        for (int i = 1; i < this.m_size + 1; ++i) {
            if (i != 1) {
                sb.append(", ");
            }
            sb.append(this.m_elements[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }
    
    public Iterator iterator() {
        return new Iterator() {
            private int index = 1;
            private int lastReturnedIndex = -1;
            
            public boolean hasNext() {
                return this.index <= BinaryHeap.this.m_size;
            }
            
            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.lastReturnedIndex = this.index;
                ++this.index;
                return BinaryHeap.this.m_elements[this.lastReturnedIndex];
            }
            
            public void remove() {
                if (this.lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                BinaryHeap.this.m_elements[this.lastReturnedIndex] = BinaryHeap.this.m_elements[BinaryHeap.this.m_size];
                BinaryHeap.this.m_elements[BinaryHeap.this.m_size] = null;
                final BinaryHeap this$0 = BinaryHeap.this;
                --this$0.m_size;
                if (BinaryHeap.this.m_size != 0 && this.lastReturnedIndex <= BinaryHeap.this.m_size) {
                    int compareToParent = 0;
                    if (this.lastReturnedIndex > 1) {
                        compareToParent = BinaryHeap.this.compare(BinaryHeap.this.m_elements[this.lastReturnedIndex], BinaryHeap.this.m_elements[this.lastReturnedIndex / 2]);
                    }
                    if (BinaryHeap.this.m_isMinHeap) {
                        if (this.lastReturnedIndex > 1 && compareToParent < 0) {
                            BinaryHeap.this.percolateUpMinHeap(this.lastReturnedIndex);
                        }
                        else {
                            BinaryHeap.this.percolateDownMinHeap(this.lastReturnedIndex);
                        }
                    }
                    else if (this.lastReturnedIndex > 1 && compareToParent > 0) {
                        BinaryHeap.this.percolateUpMaxHeap(this.lastReturnedIndex);
                    }
                    else {
                        BinaryHeap.this.percolateDownMaxHeap(this.lastReturnedIndex);
                    }
                }
                --this.index;
                this.lastReturnedIndex = -1;
            }
        };
    }
    
    public boolean add(final Object object) {
        this.insert(object);
        return true;
    }
    
    public Object get() {
        try {
            return this.peek();
        }
        catch (final NoSuchElementException e) {
            throw new BufferUnderflowException();
        }
    }
    
    public Object remove() {
        try {
            return this.pop();
        }
        catch (final NoSuchElementException e) {
            throw new BufferUnderflowException();
        }
    }
    
    public int size() {
        return this.m_size;
    }
}
