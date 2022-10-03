package org.antlr.v4.runtime.misc;

public final class MurmurHash
{
    private static final int DEFAULT_SEED = 0;
    
    public static int initialize() {
        return initialize(0);
    }
    
    public static int initialize(final int seed) {
        return seed;
    }
    
    public static int update(int hash, final int value) {
        final int c1 = -862048943;
        final int c2 = 461845907;
        final int r1 = 15;
        final int r2 = 13;
        final int m = 5;
        final int n = -430675100;
        int k = value;
        k *= -862048943;
        k = (k << 15 | k >>> 17);
        k *= 461845907;
        hash ^= k;
        hash = (hash << 13 | hash >>> 19);
        hash = hash * 5 - 430675100;
        return hash;
    }
    
    public static int update(final int hash, final Object value) {
        return update(hash, (value != null) ? value.hashCode() : 0);
    }
    
    public static int finish(int hash, final int numberOfWords) {
        hash ^= numberOfWords * 4;
        hash ^= hash >>> 16;
        hash *= -2048144789;
        hash ^= hash >>> 13;
        hash *= -1028477387;
        hash ^= hash >>> 16;
        return hash;
    }
    
    public static <T> int hashCode(final T[] data, final int seed) {
        int hash = initialize(seed);
        for (final T value : data) {
            hash = update(hash, value);
        }
        hash = finish(hash, data.length);
        return hash;
    }
    
    private MurmurHash() {
    }
}
