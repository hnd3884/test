package org.apache.tika.io;

import java.util.Locale;
import java.util.HashSet;

public class FilenameUtils
{
    public static final char[] RESERVED_FILENAME_CHARACTERS;
    private static final HashSet<Character> RESERVED;
    
    public static String normalize(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        final StringBuilder sb = new StringBuilder();
        for (final char c : name.toCharArray()) {
            if (FilenameUtils.RESERVED.contains(c)) {
                sb.append('%').append((c < '\u0010') ? "0" : "").append(Integer.toHexString(c).toUpperCase(Locale.ROOT));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public static String getName(final String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        final int unix = path.lastIndexOf("/");
        final int windows = path.lastIndexOf("\\");
        final int colon = path.lastIndexOf(":");
        final String cand = path.substring(Math.max(colon, Math.max(unix, windows)) + 1);
        if (cand.equals("..") || cand.equals(".")) {
            return "";
        }
        return cand;
    }
    
    static {
        RESERVED_FILENAME_CHARACTERS = new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '?', ':', '*', '<', '>', '|' };
        RESERVED = new HashSet<Character>(38);
        for (final char reservedFilenameCharacter : FilenameUtils.RESERVED_FILENAME_CHARACTERS) {
            FilenameUtils.RESERVED.add(reservedFilenameCharacter);
        }
    }
}
