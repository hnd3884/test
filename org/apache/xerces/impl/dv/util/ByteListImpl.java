package org.apache.xerces.impl.dv.util;

import org.apache.xerces.xs.XSException;
import org.apache.xerces.xs.datatypes.ByteList;
import java.util.AbstractList;

public class ByteListImpl extends AbstractList implements ByteList
{
    protected final byte[] data;
    protected String canonical;
    
    public ByteListImpl(final byte[] data) {
        this.data = data;
    }
    
    public int getLength() {
        return this.data.length;
    }
    
    public boolean contains(final byte b) {
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] == b) {
                return true;
            }
        }
        return false;
    }
    
    public byte item(final int n) throws XSException {
        if (n < 0 || n > this.data.length - 1) {
            throw new XSException((short)2, null);
        }
        return this.data[n];
    }
    
    public Object get(final int n) {
        if (n >= 0 && n < this.data.length) {
            return new Byte(this.data[n]);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    public int size() {
        return this.getLength();
    }
    
    public byte[] toByteArray() {
        final byte[] array = new byte[this.data.length];
        System.arraycopy(this.data, 0, array, 0, this.data.length);
        return array;
    }
}
