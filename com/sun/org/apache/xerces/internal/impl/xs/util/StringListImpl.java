package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.lang.reflect.Array;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.xs.StringList;
import java.util.AbstractList;

public final class StringListImpl extends AbstractList implements StringList
{
    public static final StringListImpl EMPTY_LIST;
    private final String[] fArray;
    private final int fLength;
    private final Vector fVector;
    
    public StringListImpl(final Vector v) {
        this.fVector = v;
        this.fLength = ((v == null) ? 0 : v.size());
        this.fArray = null;
    }
    
    public StringListImpl(final String[] array, final int length) {
        this.fArray = array;
        this.fLength = length;
        this.fVector = null;
    }
    
    @Override
    public int getLength() {
        return this.fLength;
    }
    
    @Override
    public boolean contains(final String item) {
        if (this.fVector != null) {
            return this.fVector.contains(item);
        }
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
    public String item(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        if (this.fVector != null) {
            return this.fVector.elementAt(index);
        }
        return this.fArray[index];
    }
    
    @Override
    public Object get(final int index) {
        if (index < 0 || index >= this.fLength) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        if (this.fVector != null) {
            return this.fVector.elementAt(index);
        }
        return this.fArray[index];
    }
    
    @Override
    public int size() {
        return this.getLength();
    }
    
    @Override
    public Object[] toArray() {
        if (this.fVector != null) {
            return this.fVector.toArray();
        }
        final Object[] a = new Object[this.fLength];
        this.toArray0(a);
        return a;
    }
    
    @Override
    public Object[] toArray(Object[] a) {
        if (this.fVector != null) {
            return this.fVector.toArray(a);
        }
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
        EMPTY_LIST = new StringListImpl(new String[0], 0);
    }
}
