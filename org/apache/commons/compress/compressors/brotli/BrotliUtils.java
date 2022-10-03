package org.apache.commons.compress.compressors.brotli;

import org.apache.commons.compress.utils.OsgiUtils;

public class BrotliUtils
{
    private static volatile CachedAvailability cachedBrotliAvailability;
    
    private BrotliUtils() {
    }
    
    public static boolean isBrotliCompressionAvailable() {
        final CachedAvailability cachedResult = BrotliUtils.cachedBrotliAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        }
        return internalIsBrotliCompressionAvailable();
    }
    
    private static boolean internalIsBrotliCompressionAvailable() {
        try {
            Class.forName("org.brotli.dec.BrotliInputStream");
            return true;
        }
        catch (final NoClassDefFoundError | Exception error) {
            return false;
        }
    }
    
    public static void setCacheBrotliAvailablity(final boolean doCache) {
        if (!doCache) {
            BrotliUtils.cachedBrotliAvailability = CachedAvailability.DONT_CACHE;
        }
        else if (BrotliUtils.cachedBrotliAvailability == CachedAvailability.DONT_CACHE) {
            final boolean hasBrotli = internalIsBrotliCompressionAvailable();
            BrotliUtils.cachedBrotliAvailability = (hasBrotli ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE);
        }
    }
    
    static CachedAvailability getCachedBrotliAvailability() {
        return BrotliUtils.cachedBrotliAvailability;
    }
    
    static {
        BrotliUtils.cachedBrotliAvailability = CachedAvailability.DONT_CACHE;
        setCacheBrotliAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
    }
    
    enum CachedAvailability
    {
        DONT_CACHE, 
        CACHED_AVAILABLE, 
        CACHED_UNAVAILABLE;
    }
}
