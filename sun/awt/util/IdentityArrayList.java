package sun.awt.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;
import java.util.List;
import java.util.AbstractList;

public class IdentityArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess
{
    private transient Object[] elementData;
    private int size;
    
    public IdentityArrayList(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + n);
        }
        this.elementData = new Object[n];
    }
    
    public IdentityArrayList() {
        this(10);
    }
    
    public IdentityArrayList(final Collection<? extends E> collection) {
        this.elementData = collection.toArray();
        this.size = this.elementData.length;
        if (this.elementData.getClass() != Object[].class) {
            this.elementData = Arrays.copyOf(this.elementData, this.size, (Class<? extends Object[]>)Object[].class);
        }
    }
    
    public void trimToSize() {
        ++this.modCount;
        if (this.size < this.elementData.length) {
            this.elementData = Arrays.copyOf(this.elementData, this.size);
        }
    }
    
    public void ensureCapacity(final int n) {
        ++this.modCount;
        final int length = this.elementData.length;
        if (n > length) {
            final Object[] elementData = this.elementData;
            int n2 = length * 3 / 2 + 1;
            if (n2 < n) {
                n2 = n;
            }
            this.elementData = Arrays.copyOf(this.elementData, n2);
        }
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.indexOf(o) >= 0;
    }
    
    @Override
    public int indexOf(final Object o) {
        for (int i = 0; i < this.size; ++i) {
            if (o == this.elementData[i]) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        for (int i = this.size - 1; i >= 0; --i) {
            if (o == this.elementData[i]) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.elementData, this.size);
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        if (array.length < this.size) {
            return Arrays.copyOf(this.elementData, this.size, (Class<? extends T[]>)array.getClass());
        }
        System.arraycopy(this.elementData, 0, array, 0, this.size);
        if (array.length > this.size) {
            array[this.size] = null;
        }
        return array;
    }
    
    @Override
    public E get(final int n) {
        this.rangeCheck(n);
        return (E)this.elementData[n];
    }
    
    @Override
    public E set(final int n, final E e) {
        this.rangeCheck(n);
        final Object o = this.elementData[n];
        this.elementData[n] = e;
        return (E)o;
    }
    
    @Override
    public boolean add(final E e) {
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = e;
        return true;
    }
    
    @Override
    public void add(final int n, final E e) {
        this.rangeCheckForAdd(n);
        this.ensureCapacity(this.size + 1);
        System.arraycopy(this.elementData, n, this.elementData, n + 1, this.size - n);
        this.elementData[n] = e;
        ++this.size;
    }
    
    @Override
    public E remove(final int n) {
        this.rangeCheck(n);
        ++this.modCount;
        final Object o = this.elementData[n];
        final int n2 = this.size - n - 1;
        if (n2 > 0) {
            System.arraycopy(this.elementData, n + 1, this.elementData, n, n2);
        }
        this.elementData[--this.size] = null;
        return (E)o;
    }
    
    @Override
    public boolean remove(final Object o) {
        for (int i = 0; i < this.size; ++i) {
            if (o == this.elementData[i]) {
                this.fastRemove(i);
                return true;
            }
        }
        return false;
    }
    
    private void fastRemove(final int n) {
        ++this.modCount;
        final int n2 = this.size - n - 1;
        if (n2 > 0) {
            System.arraycopy(this.elementData, n + 1, this.elementData, n, n2);
        }
        this.elementData[--this.size] = null;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        for (int i = 0; i < this.size; ++i) {
            this.elementData[i] = null;
        }
        this.size = 0;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        final Object[] array = collection.toArray();
        final int length = array.length;
        this.ensureCapacity(this.size + length);
        System.arraycopy(array, 0, this.elementData, this.size, length);
        this.size += length;
        return length != 0;
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends E> collection) {
        this.rangeCheckForAdd(n);
        final Object[] array = collection.toArray();
        final int length = array.length;
        this.ensureCapacity(this.size + length);
        final int n2 = this.size - n;
        if (n2 > 0) {
            System.arraycopy(this.elementData, n, this.elementData, n + length, n2);
        }
        System.arraycopy(array, 0, this.elementData, n, length);
        this.size += length;
        return length != 0;
    }
    
    @Override
    protected void removeRange(final int n, final int n2) {
        ++this.modCount;
        System.arraycopy(this.elementData, n2, this.elementData, n, this.size - n2);
        while (this.size != this.size - (n2 - n)) {
            this.elementData[--this.size] = null;
        }
    }
    
    private void rangeCheck(final int n) {
        if (n >= this.size) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(n));
        }
    }
    
    private void rangeCheckForAdd(final int n) {
        if (n > this.size || n < 0) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(n));
        }
    }
    
    private String outOfBoundsMsg(final int n) {
        return "Index: " + n + ", Size: " + this.size;
    }
}
