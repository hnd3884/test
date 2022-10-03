package com.sun.org.apache.xerces.internal.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class XMLAttributesIteratorImpl extends XMLAttributesImpl implements Iterator
{
    protected int fCurrent;
    protected Attribute fLastReturnedItem;
    
    public XMLAttributesIteratorImpl() {
        this.fCurrent = 0;
    }
    
    @Override
    public boolean hasNext() {
        return this.fCurrent < this.getLength();
    }
    
    @Override
    public Object next() {
        if (this.hasNext()) {
            return this.fLastReturnedItem = this.fAttributes[this.fCurrent++];
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() {
        if (this.fLastReturnedItem == this.fAttributes[this.fCurrent - 1]) {
            this.removeAttributeAt(this.fCurrent--);
            return;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public void removeAllAttributes() {
        super.removeAllAttributes();
        this.fCurrent = 0;
    }
}
