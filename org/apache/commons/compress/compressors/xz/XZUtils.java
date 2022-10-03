package org.apache.commons.compress.compressors.xz;

import java.util.Map;
import org.apache.commons.compress.utils.OsgiUtils;
import java.util.HashMap;
import org.apache.commons.compress.compressors.FileNameUtil;

public class XZUtils
{
    private static final FileNameUtil fileNameUtil;
    private static final byte[] HEADER_MAGIC;
    private static volatile CachedAvailability cachedXZAvailability;
    
    private XZUtils() {
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < XZUtils.HEADER_MAGIC.length) {
            return false;
        }
        for (int i = 0; i < XZUtils.HEADER_MAGIC.length; ++i) {
            if (signature[i] != XZUtils.HEADER_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isXZCompressionAvailable() {
        final CachedAvailability cachedResult = XZUtils.cachedXZAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        }
        return internalIsXZCompressionAvailable();
    }
    
    private static boolean internalIsXZCompressionAvailable() {
        try {
            XZCompressorInputStream.matches(null, 0);
            return true;
        }
        catch (final NoClassDefFoundError error) {
            return false;
        }
    }
    
    public static boolean isCompressedFilename(final String fileName) {
        return XZUtils.fileNameUtil.isCompressedFilename(fileName);
    }
    
    public static String getUncompressedFilename(final String fileName) {
        return XZUtils.fileNameUtil.getUncompressedFilename(fileName);
    }
    
    public static String getCompressedFilename(final String fileName) {
        return XZUtils.fileNameUtil.getCompressedFilename(fileName);
    }
    
    public static void setCacheXZAvailablity(final boolean doCache) {
        if (!doCache) {
            XZUtils.cachedXZAvailability = CachedAvailability.DONT_CACHE;
        }
        else if (XZUtils.cachedXZAvailability == CachedAvailability.DONT_CACHE) {
            final boolean hasXz = internalIsXZCompressionAvailable();
            XZUtils.cachedXZAvailability = (hasXz ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE);
        }
    }
    
    static CachedAvailability getCachedXZAvailability() {
        return XZUtils.cachedXZAvailability;
    }
    
    static {
        HEADER_MAGIC = new byte[] { -3, 55, 122, 88, 90, 0 };
        final Map<String, String> uncompressSuffix = new HashMap<String, String>();
        uncompressSuffix.put(".txz", ".tar");
        uncompressSuffix.put(".xz", "");
        uncompressSuffix.put("-xz", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".xz");
        XZUtils.cachedXZAvailability = CachedAvailability.DONT_CACHE;
        setCacheXZAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
    }
    
    enum CachedAvailability
    {
        DONT_CACHE, 
        CACHED_AVAILABLE, 
        CACHED_UNAVAILABLE;
    }
}
