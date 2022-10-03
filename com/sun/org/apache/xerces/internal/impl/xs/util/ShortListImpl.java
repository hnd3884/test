package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import java.util.AbstractList;

public final class ShortListImpl extends AbstractList implements ShortList
{
    public static final ShortListImpl EMPTY_LIST;
    private final short[] fArray;
    private final int fLength;
    
    public ShortListImpl(final short[] array, final int length) {
        this.fArray = array;
        this.fLength = length;
    }
    
    @Override
    public int getLength() {
        return this.fLength;
    }
    
    @Override
    public boolean contains(final short item) {
        for (int i = 0; i < this.fLength; ++i) {
            if (this.fArray[i] == item) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public short item(final int index) throws XSException {
        if (index < 0 || index >= this.fLength) {
            throw new XSException((short)2, null);
        }
        return this.fArray[index];
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof ShortList)) {
            return false;
        }
        final ShortList rhs = (ShortList)obj;
        if (this.fLength != rhs.getLength()) {
            return false;
        }
        for (int i = 0; i < this.fLength; ++i) {
            if (this.fArray[i] != rhs.item(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Object get(final int index) {
        if (index >= 0 && index < this.fLength) {
            return new Short(this.fArray[index]);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
    
    @Override
    public int size() {
        return this.getLength();
    }
    
    static {
        EMPTY_LIST = new ShortListImpl(new short[0], 0);
    }
}
