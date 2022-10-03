package org.tukaani.xz;

public class ArrayCache
{
    private static final ArrayCache dummyCache;
    private static volatile ArrayCache defaultCache;
    
    public static ArrayCache getDummyCache() {
        return ArrayCache.dummyCache;
    }
    
    public static ArrayCache getDefaultCache() {
        return ArrayCache.defaultCache;
    }
    
    public static void setDefaultCache(final ArrayCache defaultCache) {
        if (defaultCache == null) {
            throw new NullPointerException();
        }
        ArrayCache.defaultCache = defaultCache;
    }
    
    public byte[] getByteArray(final int n, final boolean b) {
        return new byte[n];
    }
    
    public void putArray(final byte[] array) {
    }
    
    public int[] getIntArray(final int n, final boolean b) {
        return new int[n];
    }
    
    public void putArray(final int[] array) {
    }
    
    static {
        dummyCache = new ArrayCache();
        ArrayCache.defaultCache = ArrayCache.dummyCache;
    }
}
