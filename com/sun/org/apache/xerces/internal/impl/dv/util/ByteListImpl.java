package com.sun.org.apache.xerces.internal.impl.dv.util;

import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;
import java.util.AbstractList;

public class ByteListImpl extends AbstractList implements ByteList
{
    protected final byte[] data;
    protected String canonical;
    
    public ByteListImpl(final byte[] data) {
        this.data = data;
    }
    
    @Override
    public int getLength() {
        return this.data.length;
    }
    
    @Override
    public boolean contains(final byte item) {
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] == item) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public byte item(final int index) throws XSException {
        if (index < 0 || index > this.data.length - 1) {
            throw new XSException((short)2, null);
        }
        return this.data[index];
    }
    
    @Override
    public Object get(final int index) {
        if (index >= 0 && index < this.data.length) {
            return new Byte(this.data[index]);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
    
    @Override
    public int size() {
        return this.getLength();
    }
}
