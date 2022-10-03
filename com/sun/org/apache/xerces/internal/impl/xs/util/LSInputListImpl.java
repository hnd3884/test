package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.lang.reflect.Array;
import org.w3c.dom.ls.LSInput;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import java.util.AbstractList;

public final class LSInputListImpl extends AbstractList implements LSInputList
{
    public static final LSInputListImpl EMPTY_LIST;
    private final LSInput[] fArray;
    private final int fLength;
    
    public LSInputListImpl(final LSInput[] array, final int length) {
        this.fArray = array;
        this.fLength = length;
    }
    
    @Override
    public int getLength() {
        return this.fLength;
    }
    
    @Override
    public LSInput item(final int index) {
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
        EMPTY_LIST = new LSInputListImpl(new LSInput[0], 0);
    }
}
