package org.apache.lucene.index;

import java.util.regex.Pattern;

public final class IndexFileNames
{
    public static final String SEGMENTS = "segments";
    public static final String PENDING_SEGMENTS = "pending_segments";
    public static final String OLD_SEGMENTS_GEN = "segments.gen";
    public static final Pattern CODEC_FILE_PATTERN;
    
    private IndexFileNames() {
    }
    
    public static String fileNameFromGeneration(final String base, final String ext, final long gen) {
        if (gen == -1L) {
            return null;
        }
        if (gen == 0L) {
            return segmentFileName(base, "", ext);
        }
        assert gen > 0L;
        final StringBuilder res = new StringBuilder(base.length() + 6 + ext.length()).append(base).append('_').append(Long.toString(gen, 36));
        if (ext.length() > 0) {
            res.append('.').append(ext);
        }
        return res.toString();
    }
    
    public static String segmentFileName(final String segmentName, final String segmentSuffix, final String ext) {
        if (ext.length() <= 0 && segmentSuffix.length() <= 0) {
            return segmentName;
        }
        assert !ext.startsWith(".");
        final StringBuilder sb = new StringBuilder(segmentName.length() + 2 + segmentSuffix.length() + ext.length());
        sb.append(segmentName);
        if (segmentSuffix.length() > 0) {
            sb.append('_').append(segmentSuffix);
        }
        if (ext.length() > 0) {
            sb.append('.').append(ext);
        }
        return sb.toString();
    }
    
    public static boolean matchesExtension(final String filename, final String ext) {
        return filename.endsWith("." + ext);
    }
    
    private static int indexOfSegmentName(final String filename) {
        int idx = filename.indexOf(95, 1);
        if (idx == -1) {
            idx = filename.indexOf(46);
        }
        return idx;
    }
    
    public static String stripSegmentName(String filename) {
        final int idx = indexOfSegmentName(filename);
        if (idx != -1) {
            filename = filename.substring(idx);
        }
        return filename;
    }
    
    public static long parseGeneration(final String filename) {
        assert filename.startsWith("_");
        final String[] parts = stripExtension(filename).substring(1).split("_");
        if (parts.length == 2 || parts.length == 4) {
            return Long.parseLong(parts[1], 36);
        }
        return 0L;
    }
    
    public static String parseSegmentName(String filename) {
        final int idx = indexOfSegmentName(filename);
        if (idx != -1) {
            filename = filename.substring(0, idx);
        }
        return filename;
    }
    
    public static String stripExtension(String filename) {
        final int idx = filename.indexOf(46);
        if (idx != -1) {
            filename = filename.substring(0, idx);
        }
        return filename;
    }
    
    public static String getExtension(final String filename) {
        final int idx = filename.indexOf(46);
        if (idx == -1) {
            return null;
        }
        return filename.substring(idx + 1, filename.length());
    }
    
    static {
        CODEC_FILE_PATTERN = Pattern.compile("_[a-z0-9]+(_.*)?\\..*");
    }
}
