package com.sun.org.apache.xml.internal.utils.res;

public class IntArrayWrapper
{
    private int[] m_int;
    
    public IntArrayWrapper(final int[] arg) {
        this.m_int = arg;
    }
    
    public int getInt(final int index) {
        return this.m_int[index];
    }
    
    public int getLength() {
        return this.m_int.length;
    }
}
