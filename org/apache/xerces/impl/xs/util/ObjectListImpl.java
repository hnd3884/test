package org.apache.xerces.impl.xs.util;

import java.lang.reflect.Array;
import org.apache.xerces.xs.datatypes.ObjectList;
import java.util.AbstractList;

public final class ObjectListImpl extends AbstractList implements ObjectList
{
    public static final ObjectListImpl EMPTY_LIST;
    private final Object[] fArray;
    private final int fLength;
    
    public ObjectListImpl(final Object[] fArray, final int fLength) {
        this.fArray = fArray;
        this.fLength = fLength;
    }
    
    public int getLength() {
        return this.fLength;
    }
    
    public boolean contains(final Object o) {
        if (o == null) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fArray[i] == null) {
                    return true;
                }
            }
        }
        else {
            for (int j = 0; j < this.fLength; ++j) {
                if (o.equals(this.fArray[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Object item(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
    }
    
    public Object get(final int n) {
        if (n >= 0 && n < this.fLength) {
            return this.fArray[n];
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    public int size() {
        return this.getLength();
    }
    
    public Object[] toArray() {
        final Object[] array = new Object[this.fLength];
        this.toArray0(array);
        return array;
    }
    
    public Object[] toArray(Object[] array) {
        if (array.length < this.fLength) {
            array = (Object[])Array.newInstance(array.getClass().getComponentType(), this.fLength);
        }
        this.toArray0(array);
        if (array.length > this.fLength) {
            array[this.fLength] = null;
        }
        return array;
    }
    
    private void toArray0(final Object[] array) {
        if (this.fLength > 0) {
            System.arraycopy(this.fArray, 0, array, 0, this.fLength);
        }
    }
    
    static {
        EMPTY_LIST = new ObjectListImpl(new Object[0], 0);
    }
}
