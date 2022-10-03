package org.apache.lucene.analysis.util;

public class StemmerUtil
{
    private StemmerUtil() {
    }
    
    public static boolean startsWith(final char[] s, final int len, final String prefix) {
        final int prefixLen = prefix.length();
        if (prefixLen > len) {
            return false;
        }
        for (int i = 0; i < prefixLen; ++i) {
            if (s[i] != prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean endsWith(final char[] s, final int len, final String suffix) {
        final int suffixLen = suffix.length();
        if (suffixLen > len) {
            return false;
        }
        for (int i = suffixLen - 1; i >= 0; --i) {
            if (s[len - (suffixLen - i)] != suffix.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean endsWith(final char[] s, final int len, final char[] suffix) {
        final int suffixLen = suffix.length;
        if (suffixLen > len) {
            return false;
        }
        for (int i = suffixLen - 1; i >= 0; --i) {
            if (s[len - (suffixLen - i)] != suffix[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static int delete(final char[] s, final int pos, final int len) {
        assert pos < len;
        if (pos < len - 1) {
            System.arraycopy(s, pos + 1, s, pos, len - pos - 1);
        }
        return len - 1;
    }
    
    public static int deleteN(final char[] s, final int pos, final int len, final int nChars) {
        assert pos + nChars <= len;
        if (pos + nChars < len) {
            System.arraycopy(s, pos + nChars, s, pos, len - pos - nChars);
        }
        return len - nChars;
    }
}
