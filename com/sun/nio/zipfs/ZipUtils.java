package com.sun.nio.zipfs;

import java.util.regex.PatternSyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;

class ZipUtils
{
    private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
    private static final String regexMetaChars = ".^$+{[]|()";
    private static final String globMetaChars = "\\*?[{";
    private static char EOL;
    
    public static void writeShort(final OutputStream outputStream, final int n) throws IOException {
        outputStream.write(n & 0xFF);
        outputStream.write(n >>> 8 & 0xFF);
    }
    
    public static void writeInt(final OutputStream outputStream, final long n) throws IOException {
        outputStream.write((int)(n & 0xFFL));
        outputStream.write((int)(n >>> 8 & 0xFFL));
        outputStream.write((int)(n >>> 16 & 0xFFL));
        outputStream.write((int)(n >>> 24 & 0xFFL));
    }
    
    public static void writeLong(final OutputStream outputStream, final long n) throws IOException {
        outputStream.write((int)(n & 0xFFL));
        outputStream.write((int)(n >>> 8 & 0xFFL));
        outputStream.write((int)(n >>> 16 & 0xFFL));
        outputStream.write((int)(n >>> 24 & 0xFFL));
        outputStream.write((int)(n >>> 32 & 0xFFL));
        outputStream.write((int)(n >>> 40 & 0xFFL));
        outputStream.write((int)(n >>> 48 & 0xFFL));
        outputStream.write((int)(n >>> 56 & 0xFFL));
    }
    
    public static void writeBytes(final OutputStream outputStream, final byte[] array) throws IOException {
        outputStream.write(array, 0, array.length);
    }
    
    public static void writeBytes(final OutputStream outputStream, final byte[] array, final int n, final int n2) throws IOException {
        outputStream.write(array, n, n2);
    }
    
    public static byte[] toDirectoryPath(byte[] copy) {
        if (copy.length != 0 && copy[copy.length - 1] != 47) {
            copy = Arrays.copyOf(copy, copy.length + 1);
            copy[copy.length - 1] = 47;
        }
        return copy;
    }
    
    public static long dosToJavaTime(final long n) {
        return new Date((int)((n >> 25 & 0x7FL) + 80L), (int)((n >> 21 & 0xFL) - 1L), (int)(n >> 16 & 0x1FL), (int)(n >> 11 & 0x1FL), (int)(n >> 5 & 0x3FL), (int)(n << 1 & 0x3EL)).getTime();
    }
    
    public static long javaToDosTime(final long n) {
        final Date date = new Date(n);
        final int n2 = date.getYear() + 1900;
        if (n2 < 1980) {
            return 2162688L;
        }
        return n2 - 1980 << 25 | date.getMonth() + 1 << 21 | date.getDate() << 16 | date.getHours() << 11 | date.getMinutes() << 5 | date.getSeconds() >> 1;
    }
    
    public static final long winToJavaTime(final long n) {
        return TimeUnit.MILLISECONDS.convert(n / 10L - 11644473600000000L, TimeUnit.MICROSECONDS);
    }
    
    public static final long javaToWinTime(final long n) {
        return (TimeUnit.MICROSECONDS.convert(n, TimeUnit.MILLISECONDS) + 11644473600000000L) * 10L;
    }
    
    public static final long unixToJavaTime(final long n) {
        return TimeUnit.MILLISECONDS.convert(n, TimeUnit.SECONDS);
    }
    
    public static final long javaToUnixTime(final long n) {
        return TimeUnit.SECONDS.convert(n, TimeUnit.MILLISECONDS);
    }
    
    private static boolean isRegexMeta(final char c) {
        return ".^$+{[]|()".indexOf(c) != -1;
    }
    
    private static boolean isGlobMeta(final char c) {
        return "\\*?[{".indexOf(c) != -1;
    }
    
    private static char next(final String s, final int n) {
        if (n < s.length()) {
            return s.charAt(n);
        }
        return ZipUtils.EOL;
    }
    
    public static String toRegexPattern(final String s) {
        int n = 0;
        final StringBuilder sb = new StringBuilder("^");
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i++);
            switch (c) {
                case 92: {
                    if (i == s.length()) {
                        throw new PatternSyntaxException("No character to escape", s, i - 1);
                    }
                    final char char1 = s.charAt(i++);
                    if (isGlobMeta(char1) || isRegexMeta(char1)) {
                        sb.append('\\');
                    }
                    sb.append(char1);
                    continue;
                }
                case 47: {
                    sb.append(c);
                    continue;
                }
                case 91: {
                    sb.append("[[^/]&&[");
                    if (next(s, i) == '^') {
                        sb.append("\\^");
                        ++i;
                    }
                    else {
                        if (next(s, i) == '!') {
                            sb.append('^');
                            ++i;
                        }
                        if (next(s, i) == '-') {
                            sb.append('-');
                            ++i;
                        }
                    }
                    int n2 = 0;
                    char c2 = '\0';
                    while (i < s.length()) {
                        c = s.charAt(i++);
                        if (c == ']') {
                            break;
                        }
                        if (c == '/') {
                            throw new PatternSyntaxException("Explicit 'name separator' in class", s, i - 1);
                        }
                        if (c == '\\' || c == '[' || (c == '&' && next(s, i) == '&')) {
                            sb.append('\\');
                        }
                        sb.append(c);
                        if (c == '-') {
                            if (n2 == 0) {
                                throw new PatternSyntaxException("Invalid range", s, i - 1);
                            }
                            if ((c = next(s, i++)) == ZipUtils.EOL) {
                                break;
                            }
                            if (c == ']') {
                                break;
                            }
                            if (c < c2) {
                                throw new PatternSyntaxException("Invalid range", s, i - 3);
                            }
                            sb.append(c);
                            n2 = 0;
                        }
                        else {
                            n2 = 1;
                            c2 = c;
                        }
                    }
                    if (c != ']') {
                        throw new PatternSyntaxException("Missing ']", s, i - 1);
                    }
                    sb.append("]]");
                    continue;
                }
                case 123: {
                    if (n != 0) {
                        throw new PatternSyntaxException("Cannot nest groups", s, i - 1);
                    }
                    sb.append("(?:(?:");
                    n = 1;
                    continue;
                }
                case 125: {
                    if (n != 0) {
                        sb.append("))");
                        n = 0;
                        continue;
                    }
                    sb.append('}');
                    continue;
                }
                case 44: {
                    if (n != 0) {
                        sb.append(")|(?:");
                        continue;
                    }
                    sb.append(',');
                    continue;
                }
                case 42: {
                    if (next(s, i) == '*') {
                        sb.append(".*");
                        ++i;
                        continue;
                    }
                    sb.append("[^/]*");
                    continue;
                }
                case 63: {
                    sb.append("[^/]");
                    continue;
                }
                default: {
                    if (isRegexMeta(c)) {
                        sb.append('\\');
                    }
                    sb.append(c);
                    continue;
                }
            }
        }
        if (n != 0) {
            throw new PatternSyntaxException("Missing '}", s, i - 1);
        }
        return sb.append('$').toString();
    }
    
    static {
        ZipUtils.EOL = '\0';
    }
}
