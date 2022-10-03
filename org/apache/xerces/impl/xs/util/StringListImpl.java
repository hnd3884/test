package org.apache.xerces.impl.xs.util;

import java.lang.reflect.Array;
import java.util.Vector;
import org.apache.xerces.xs.StringList;
import java.util.AbstractList;

public final class StringListImpl extends AbstractList implements StringList
{
    public static final StringListImpl EMPTY_LIST;
    private final String[] fArray;
    private final int fLength;
    private final Vector fVector;
    
    public StringListImpl(final Vector fVector) {
        this.fVector = fVector;
        this.fLength = ((fVector == null) ? 0 : fVector.size());
        this.fArray = null;
    }
    
    public StringListImpl(final String[] fArray, final int fLength) {
        this.fArray = fArray;
        this.fLength = fLength;
        this.fVector = null;
    }
    
    public int getLength() {
        return this.fLength;
    }
    
    public boolean contains(final String s) {
        if (this.fVector != null) {
            return this.fVector.contains(s);
        }
        if (s == null) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fArray[i] == null) {
                    return true;
                }
            }
        }
        else {
            for (int j = 0; j < this.fLength; ++j) {
                if (s.equals(this.fArray[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String item(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        if (this.fVector != null) {
            return this.fVector.elementAt(n);
        }
        return this.fArray[n];
    }
    
    public Object get(final int n) {
        if (n < 0 || n >= this.fLength) {
            throw new IndexOutOfBoundsException("Index: " + n);
        }
        if (this.fVector != null) {
            return this.fVector.elementAt(n);
        }
        return this.fArray[n];
    }
    
    public int size() {
        return this.getLength();
    }
    
    public Object[] toArray() {
        if (this.fVector != null) {
            return this.fVector.toArray();
        }
        final Object[] array = new Object[this.fLength];
        this.toArray0(array);
        return array;
    }
    
    public Object[] toArray(Object[] array) {
        if (this.fVector != null) {
            return this.fVector.toArray(array);
        }
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
        EMPTY_LIST = new StringListImpl(new String[0], 0);
    }
}
