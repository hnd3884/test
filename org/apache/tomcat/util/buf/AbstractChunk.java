package org.apache.tomcat.util.buf;

import java.io.Serializable;

public abstract class AbstractChunk implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final int ARRAY_MAX_SIZE = 2147483639;
    private int hashCode;
    protected boolean hasHashCode;
    protected boolean isSet;
    private int limit;
    protected int start;
    protected int end;
    
    public AbstractChunk() {
        this.hashCode = 0;
        this.hasHashCode = false;
        this.limit = -1;
    }
    
    public void setLimit(final int limit) {
        this.limit = limit;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    protected int getLimitInternal() {
        if (this.limit > 0) {
            return this.limit;
        }
        return 2147483639;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    public void setEnd(final int i) {
        this.end = i;
    }
    
    public int getOffset() {
        return this.start;
    }
    
    public void setOffset(final int off) {
        if (this.end < off) {
            this.end = off;
        }
        this.start = off;
    }
    
    public int getLength() {
        return this.end - this.start;
    }
    
    public boolean isNull() {
        return this.end <= 0 && !this.isSet;
    }
    
    public int indexOf(final String src, final int srcOff, final int srcLen, final int myOff) {
        final char first = src.charAt(srcOff);
        final int srcEnd = srcOff + srcLen;
    Label_0096:
        for (int i = myOff + this.start; i <= this.end - srcLen; ++i) {
            if (this.getBufferElement(i) == first) {
                int myPos = i + 1;
                int srcPos = srcOff + 1;
                while (srcPos < srcEnd) {
                    if (this.getBufferElement(myPos++) != src.charAt(srcPos++)) {
                        continue Label_0096;
                    }
                }
                return i - this.start;
            }
        }
        return -1;
    }
    
    public void recycle() {
        this.hasHashCode = false;
        this.isSet = false;
        this.start = 0;
        this.end = 0;
    }
    
    @Override
    public int hashCode() {
        if (this.hasHashCode) {
            return this.hashCode;
        }
        int code = 0;
        code = this.hash();
        this.hashCode = code;
        this.hasHashCode = true;
        return code;
    }
    
    public int hash() {
        int code = 0;
        for (int i = this.start; i < this.end; ++i) {
            code = code * 37 + this.getBufferElement(i);
        }
        return code;
    }
    
    protected abstract int getBufferElement(final int p0);
}
