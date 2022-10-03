package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.StringList;
import java.util.AbstractList;

final class PSVIErrorList extends AbstractList implements StringList
{
    private final String[] fArray;
    private final int fLength;
    private final int fOffset;
    
    public PSVIErrorList(final String[] fArray, final boolean b) {
        this.fArray = fArray;
        this.fLength = this.fArray.length >> 1;
        this.fOffset = (b ? 0 : 1);
    }
    
    public boolean contains(final String s) {
        if (s == null) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fArray[(i << 1) + this.fOffset] == null) {
                    return true;
                }
            }
        }
        else {
            for (int j = 0; j < this.fLength; ++j) {
                if (s.equals(this.fArray[(j << 1) + this.fOffset])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int getLength() {
        return this.fLength;
    }
    
    public String item(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[(n << 1) + this.fOffset];
    }
    
    public Object get(final int n) {
        if (n >= 0 && n < this.fLength) {
            return this.fArray[(n << 1) + this.fOffset];
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    public int size() {
        return this.getLength();
    }
}
