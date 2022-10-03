package org.glassfish.jersey.internal.guava;

final class ObjectArrays
{
    static final Object[] EMPTY_ARRAY;
    
    private ObjectArrays() {
    }
    
    public static <T> T[] newArray(final T[] reference, final int length) {
        return Platform.newArray(reference, length);
    }
    
    static <T> T[] arraysCopyOf(final T[] original, final int newLength) {
        final T[] copy = (T[])newArray((Object[])original, newLength);
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }
    
    static {
        EMPTY_ARRAY = new Object[0];
    }
}
