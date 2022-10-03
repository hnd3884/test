package org.apache.commons.compress.compressors.lzma;

import java.util.Map;
import org.apache.commons.compress.utils.OsgiUtils;
import java.util.HashMap;
import org.apache.commons.compress.compressors.FileNameUtil;

public class LZMAUtils
{
    private static final FileNameUtil fileNameUtil;
    private static final byte[] HEADER_MAGIC;
    private static volatile CachedAvailability cachedLZMAAvailability;
    
    private LZMAUtils() {
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < LZMAUtils.HEADER_MAGIC.length) {
            return false;
        }
        for (int i = 0; i < LZMAUtils.HEADER_MAGIC.length; ++i) {
            if (signature[i] != LZMAUtils.HEADER_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isLZMACompressionAvailable() {
        final CachedAvailability cachedResult = LZMAUtils.cachedLZMAAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        }
        return internalIsLZMACompressionAvailable();
    }
    
    private static boolean internalIsLZMACompressionAvailable() {
        try {
            LZMACompressorInputStream.matches(null, 0);
            return true;
        }
        catch (final NoClassDefFoundError error) {
            return false;
        }
    }
    
    public static boolean isCompressedFilename(final String fileName) {
        return LZMAUtils.fileNameUtil.isCompressedFilename(fileName);
    }
    
    public static String getUncompressedFilename(final String fileName) {
        return LZMAUtils.fileNameUtil.getUncompressedFilename(fileName);
    }
    
    public static String getCompressedFilename(final String fileName) {
        return LZMAUtils.fileNameUtil.getCompressedFilename(fileName);
    }
    
    public static void setCacheLZMAAvailablity(final boolean doCache) {
        if (!doCache) {
            LZMAUtils.cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
        }
        else if (LZMAUtils.cachedLZMAAvailability == CachedAvailability.DONT_CACHE) {
            final boolean hasLzma = internalIsLZMACompressionAvailable();
            LZMAUtils.cachedLZMAAvailability = (hasLzma ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE);
        }
    }
    
    static CachedAvailability getCachedLZMAAvailability() {
        return LZMAUtils.cachedLZMAAvailability;
    }
    
    static {
        HEADER_MAGIC = new byte[] { 93, 0, 0 };
        final Map<String, String> uncompressSuffix = new HashMap<String, String>();
        uncompressSuffix.put(".lzma", "");
        uncompressSuffix.put("-lzma", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".lzma");
        LZMAUtils.cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
        setCacheLZMAAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
    }
    
    enum CachedAvailability
    {
        DONT_CACHE, 
        CACHED_AVAILABLE, 
        CACHED_UNAVAILABLE;
    }
}
