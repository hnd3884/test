package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

final class AtomicQueueUtil
{
    static <E> E lvRefElement(final AtomicReferenceArray<E> buffer, final int offset) {
        return buffer.get(offset);
    }
    
    static <E> E lpRefElement(final AtomicReferenceArray<E> buffer, final int offset) {
        return buffer.get(offset);
    }
    
    static <E> void spRefElement(final AtomicReferenceArray<E> buffer, final int offset, final E value) {
        buffer.lazySet(offset, value);
    }
    
    static void soRefElement(final AtomicReferenceArray buffer, final int offset, final Object value) {
        buffer.lazySet(offset, value);
    }
    
    static <E> void svRefElement(final AtomicReferenceArray<E> buffer, final int offset, final E value) {
        buffer.set(offset, value);
    }
    
    static int calcRefElementOffset(final long index) {
        return (int)index;
    }
    
    static int calcCircularRefElementOffset(final long index, final long mask) {
        return (int)(index & mask);
    }
    
    static <E> AtomicReferenceArray<E> allocateRefArray(final int capacity) {
        return new AtomicReferenceArray<E>(capacity);
    }
    
    static void spLongElement(final AtomicLongArray buffer, final int offset, final long e) {
        buffer.lazySet(offset, e);
    }
    
    static void soLongElement(final AtomicLongArray buffer, final int offset, final long e) {
        buffer.lazySet(offset, e);
    }
    
    static long lpLongElement(final AtomicLongArray buffer, final int offset) {
        return buffer.get(offset);
    }
    
    static long lvLongElement(final AtomicLongArray buffer, final int offset) {
        return buffer.get(offset);
    }
    
    static int calcLongElementOffset(final long index) {
        return (int)index;
    }
    
    static int calcCircularLongElementOffset(final long index, final int mask) {
        return (int)(index & (long)mask);
    }
    
    static AtomicLongArray allocateLongArray(final int capacity) {
        return new AtomicLongArray(capacity);
    }
    
    static int length(final AtomicReferenceArray<?> buf) {
        return buf.length();
    }
    
    static int modifiedCalcCircularRefElementOffset(final long index, final long mask) {
        return (int)(index & mask) >> 1;
    }
    
    static int nextArrayOffset(final AtomicReferenceArray<?> curr) {
        return length(curr) - 1;
    }
}
