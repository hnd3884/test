package io.netty.util.internal.shaded.org.jctools.util;

public final class UnsafeRefArrayAccess
{
    public static final long REF_ARRAY_BASE;
    public static final int REF_ELEMENT_SHIFT;
    
    public static <E> void spRefElement(final E[] buffer, final long offset, final E e) {
        UnsafeAccess.UNSAFE.putObject(buffer, offset, e);
    }
    
    public static <E> void soRefElement(final E[] buffer, final long offset, final E e) {
        UnsafeAccess.UNSAFE.putOrderedObject(buffer, offset, e);
    }
    
    public static <E> E lpRefElement(final E[] buffer, final long offset) {
        return (E)UnsafeAccess.UNSAFE.getObject(buffer, offset);
    }
    
    public static <E> E lvRefElement(final E[] buffer, final long offset) {
        return (E)UnsafeAccess.UNSAFE.getObjectVolatile(buffer, offset);
    }
    
    public static long calcRefElementOffset(final long index) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + (index << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
    }
    
    public static long calcCircularRefElementOffset(final long index, final long mask) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((index & mask) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT);
    }
    
    public static <E> E[] allocateRefArray(final int capacity) {
        return (E[])new Object[capacity];
    }
    
    static {
        final int scale = UnsafeAccess.UNSAFE.arrayIndexScale(Object[].class);
        if (4 == scale) {
            REF_ELEMENT_SHIFT = 2;
        }
        else {
            if (8 != scale) {
                throw new IllegalStateException("Unknown pointer size: " + scale);
            }
            REF_ELEMENT_SHIFT = 3;
        }
        REF_ARRAY_BASE = UnsafeAccess.UNSAFE.arrayBaseOffset(Object[].class);
    }
}
