package org.apache.xerces.impl.xs.util;

import java.lang.reflect.Array;
import org.w3c.dom.ls.LSInput;
import org.apache.xerces.xs.LSInputList;
import java.util.AbstractList;

public final class LSInputListImpl extends AbstractList implements LSInputList
{
    public static final LSInputListImpl EMPTY_LIST;
    private final LSInput[] fArray;
    private final int fLength;
    
    public LSInputListImpl(final LSInput[] fArray, final int fLength) {
        this.fArray = fArray;
        this.fLength = fLength;
    }
    
    public int getLength() {
        return this.fLength;
    }
    
    public LSInput item(final int n) {
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
        EMPTY_LIST = new LSInputListImpl(new LSInput[0], 0);
    }
}
