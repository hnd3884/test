package org.apache.xerces.impl.xs.util;

import org.apache.xerces.xs.XSException;
import org.apache.xerces.xs.ShortList;
import java.util.AbstractList;

public final class ShortListImpl extends AbstractList implements ShortList
{
    public static final ShortListImpl EMPTY_LIST;
    private final short[] fArray;
    private final int fLength;
    
    public ShortListImpl(final short[] fArray, final int fLength) {
        this.fArray = fArray;
        this.fLength = fLength;
    }
    
    public int getLength() {
        return this.fLength;
    }
    
    public boolean contains(final short n) {
        for (int i = 0; i < this.fLength; ++i) {
            if (this.fArray[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    public short item(final int n) throws XSException {
        if (n < 0 || n >= this.fLength) {
            throw new XSException((short)2, null);
        }
        return this.fArray[n];
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ShortList)) {
            return false;
        }
        final ShortList list = (ShortList)o;
        if (this.fLength != list.getLength()) {
            return false;
        }
        for (int i = 0; i < this.fLength; ++i) {
            if (this.fArray[i] != list.item(i)) {
                return false;
            }
        }
        return true;
    }
    
    public Object get(final int n) {
        if (n >= 0 && n < this.fLength) {
            return new Short(this.fArray[n]);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    public int size() {
        return this.getLength();
    }
    
    static {
        EMPTY_LIST = new ShortListImpl(new short[0], 0);
    }
}
