package com.sun.xml.internal.stream.util;

import java.lang.ref.SoftReference;

public class ThreadLocalBufferAllocator
{
    private static final ThreadLocal<SoftReference<BufferAllocator>> TL;
    
    public static BufferAllocator getBufferAllocator() {
        BufferAllocator ba = null;
        SoftReference<BufferAllocator> sr = ThreadLocalBufferAllocator.TL.get();
        if (sr != null) {
            ba = sr.get();
        }
        if (ba == null) {
            ba = new BufferAllocator();
            sr = new SoftReference<BufferAllocator>(ba);
            ThreadLocalBufferAllocator.TL.set(sr);
        }
        return ba;
    }
    
    static {
        TL = new ThreadLocal<SoftReference<BufferAllocator>>();
    }
}
