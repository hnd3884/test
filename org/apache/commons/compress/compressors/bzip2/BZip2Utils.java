package org.apache.commons.compress.compressors.bzip2;

import java.util.Map;
import java.util.LinkedHashMap;
import org.apache.commons.compress.compressors.FileNameUtil;

public abstract class BZip2Utils
{
    private static final FileNameUtil fileNameUtil;
    
    private BZip2Utils() {
    }
    
    public static boolean isCompressedFilename(final String fileName) {
        return BZip2Utils.fileNameUtil.isCompressedFilename(fileName);
    }
    
    public static String getUncompressedFilename(final String fileName) {
        return BZip2Utils.fileNameUtil.getUncompressedFilename(fileName);
    }
    
    public static String getCompressedFilename(final String fileName) {
        return BZip2Utils.fileNameUtil.getCompressedFilename(fileName);
    }
    
    static {
        final Map<String, String> uncompressSuffix = new LinkedHashMap<String, String>();
        uncompressSuffix.put(".tar.bz2", ".tar");
        uncompressSuffix.put(".tbz2", ".tar");
        uncompressSuffix.put(".tbz", ".tar");
        uncompressSuffix.put(".bz2", "");
        uncompressSuffix.put(".bz", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".bz2");
    }
}
