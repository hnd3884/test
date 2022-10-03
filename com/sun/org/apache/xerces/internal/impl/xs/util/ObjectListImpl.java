package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.lang.reflect.Array;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.util.AbstractList;

public final class ObjectListImpl extends AbstractList implements ObjectList
{
    public static final ObjectListImpl EMPTY_LIST;
    private final Object[] fArray;
    private final int fLength;
    
    public ObjectListImpl(final Object[] array, final int length) {
        this.fArray = array;
        this.fLength = length;
    }
    
    @Override
    public int getLength() {
        return this.fLength;
    }
    
    @Override
    public boolean contains(final Object item) {
        if (item == null) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fArray[i] == null) {
                    return true;
                }
            }
        }
        else {
            for (int i = 0; i < this.fLength; ++i) {
                if (item.equals(this.fArray[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Object item(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.fArray[index];
    }
    
    @Override
    public Object get(final int index) {
        if (index >= 0 && index < this.fLength) {
            return this.fArray[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
    
    @Override
    public int size() {
        return this.getLength();
    }
    
    @Override
    public Object[] toArray() {
        final Object[] a = new Object[this.fLength];
        this.toArray0(a);
        return a;
    }
    
    @Override
    public Object[] toArray(Object[] a) {
        if (a.length < this.fLength) {
            final Class arrayClass = a.getClass();
            final Class componentType = arrayClass.getComponentType();
            a = (Object[])Array.newInstance(componentType, this.fLength);
        }
        this.toArray0(a);
        if (a.length > this.fLength) {
            a[this.fLength] = null;
        }
        return a;
    }
    
    private void toArray0(final Object[] a) {
        if (this.fLength > 0) {
            System.arraycopy(this.fArray, 0, a, 0, this.fLength);
        }
    }
    
    static {
        EMPTY_LIST = new ObjectListImpl(new Object[0], 0);
    }
}
