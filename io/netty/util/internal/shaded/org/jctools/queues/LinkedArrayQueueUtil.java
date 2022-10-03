package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

final class LinkedArrayQueueUtil
{
    static int length(final Object[] buf) {
        return buf.length;
    }
    
    static long modifiedCalcCircularRefElementOffset(final long index, final long mask) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((index & mask) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT - 1);
    }
    
    static long nextArrayOffset(final Object[] curr) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((long)(length(curr) - 1) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
    }
}
