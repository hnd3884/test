package com.sun.org.apache.xml.internal.utils.res;

public class LongArrayWrapper
{
    private long[] m_long;
    
    public LongArrayWrapper(final long[] arg) {
        this.m_long = arg;
    }
    
    public long getLong(final int index) {
        return this.m_long[index];
    }
    
    public int getLength() {
        return this.m_long.length;
    }
}
