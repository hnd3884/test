package com.sun.org.apache.xml.internal.utils;

public class StringBufferPool
{
    private static ObjectPool m_stringBufPool;
    
    public static synchronized FastStringBuffer get() {
        return (FastStringBuffer)StringBufferPool.m_stringBufPool.getInstance();
    }
    
    public static synchronized void free(final FastStringBuffer sb) {
        sb.setLength(0);
        StringBufferPool.m_stringBufPool.freeInstance(sb);
    }
    
    static {
        StringBufferPool.m_stringBufPool = new ObjectPool(FastStringBuffer.class);
    }
}
