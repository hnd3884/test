package com.fasterxml.jackson.core.util;

import java.lang.ref.SoftReference;

public class BufferRecyclers
{
    public static final String SYSTEM_PROPERTY_TRACK_REUSABLE_BUFFERS = "com.fasterxml.jackson.core.util.BufferRecyclers.trackReusableBuffers";
    private static final ThreadLocalBufferManager _bufferRecyclerTracker;
    protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef;
    
    public static BufferRecycler getBufferRecycler() {
        SoftReference<BufferRecycler> ref = BufferRecyclers._recyclerRef.get();
        BufferRecycler br = (ref == null) ? null : ref.get();
        if (br == null) {
            br = new BufferRecycler();
            if (BufferRecyclers._bufferRecyclerTracker != null) {
                ref = BufferRecyclers._bufferRecyclerTracker.wrapAndTrack(br);
            }
            else {
                ref = new SoftReference<BufferRecycler>(br);
            }
            BufferRecyclers._recyclerRef.set(ref);
        }
        return br;
    }
    
    public static int releaseBuffers() {
        if (BufferRecyclers._bufferRecyclerTracker != null) {
            return BufferRecyclers._bufferRecyclerTracker.releaseBuffers();
        }
        return -1;
    }
    
    static {
        boolean trackReusableBuffers = false;
        try {
            trackReusableBuffers = "true".equals(System.getProperty("com.fasterxml.jackson.core.util.BufferRecyclers.trackReusableBuffers"));
        }
        catch (final SecurityException ex) {}
        _bufferRecyclerTracker = (trackReusableBuffers ? ThreadLocalBufferManager.instance() : null);
        _recyclerRef = new ThreadLocal<SoftReference<BufferRecycler>>();
    }
}
