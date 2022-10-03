package io.netty.util.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public final class StringUtil
{
    public static final String EMPTY_STRING = "";
    public static final String NEWLINE;
    public static final char DOUBLE_QUOTE = '\"';
    public static final char COMMA = ',';
    public static final char LINE_FEED = '\n';
    public static final char CARRIAGE_RETURN = '\r';
    public static final char TAB = '\t';
    public static final char SPACE = ' ';
    private static final String[] BYTE2HEX_PAD;
    private static final String[] BYTE2HEX_NOPAD;
    private static final byte[] HEX2B;
    private static final int CSV_NUMBER_ESCAPE_CHARACTERS = 7;
    private static final char PACKAGE_SEPARATOR_CHAR = '.';
    
    private StringUtil() {
    }
    
    public static String substringAfter(final String value, final char delim) {
        final int pos = value.indexOf(delim);
        if (pos >= 0) {
            return value.substring(pos + 1);
        }
        return null;
    }
    
    public static boolean commonSuffixOfLength(final String s, final String p, final int len) {
        return s != null && p != null && len >= 0 && s.regionMatches(s.length() - len, p, p.length() - len, len);
    }
    
    public static String byteToHexStringPadded(final int value) {
        return StringUtil.BYTE2HEX_PAD[value & 0xFF];
    }
    
    public static <T extends Appendable> T byteToHexStringPadded(final T buf, final int value) {
        try {
            buf.append(byteToHexStringPadded(value));
        }
        catch (final IOException e) {
            PlatformDependent.throwException(e);
        }
        return buf;
    }
    
    public static String toHexStringPadded(final byte[] src) {
        return toHexStringPadded(src, 0, src.length);
    }
    
    public static String toHexStringPadded(final byte[] src, final int offset, final int length) {
        return toHexStringPadded(new StringBuilder(length << 1), src, offset, length).toString();
    }
    
    public static <T extends Appendable> T toHexStringPadded(final T dst, final byte[] src) {
        return toHexStringPadded(dst, src, 0, src.length);
    }
    
    public static <T extends Appendable> T toHexStringPadded(final T dst, final byte[] src, final int offset, final int length) {
        for (int end = offset + length, i = offset; i < end; ++i) {
            byteToHexStringPadded(dst, src[i]);
        }
        return dst;
    }
    
    public static String byteToHexString(final int value) {
        return StringUtil.BYTE2HEX_NOPAD[value & 0xFF];
    }
    
    public static <T extends Appendable> T byteToHexString(final T buf, final int value) {
        try {
            buf.append(byteToHexString(value));
        }
        catch (final IOException e) {
            PlatformDependent.throwException(e);
        }
        return buf;
    }
    
    public static String toHexString(final byte[] src) {
        return toHexString(src, 0, src.length);
    }
    
    public static String toHexString(final byte[] src, final int offset, final int length) {
        return toHexString(new StringBuilder(length << 1), src, offset, length).toString();
    }
    
    public static <T extends Appendable> T toHexString(final T dst, final byte[] src) {
        return toHexString(dst, src, 0, src.length);
    }
    
    public static <T extends Appendable> T toHexString(final T dst, final byte[] src, final int offset, final int length) {
        assert length >= 0;
        if (length == 0) {
            return dst;
        }
        final int end = offset + length;
        int endMinusOne;
        int i;
        for (endMinusOne = end - 1, i = offset; i < endMinusOne && src[i] == 0; ++i) {}
        byteToHexString(dst, src[i++]);
        final int remaining = end - i;
        toHexStringPadded((Appendable)dst, src, i, remaining);
        return dst;
    }
    
    public static int decodeHexNibble(final char c) {
        assert StringUtil.HEX2B.length == 65536;
        return StringUtil.HEX2B[c];
    }
    
    public static byte decodeHexByte(final CharSequence s, final int pos) {
        final int hi = decodeHexNibble(s.charAt(pos));
        final int lo = decodeHexNibble(s.charAt(pos + 1));
        if (hi == -1 || lo == -1) {
            throw new IllegalArgumentException(String.format("invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
        }
        return (byte)((hi << 4) + lo);
    }
    
    public static byte[] decodeHexDump(final CharSequence hexDump, final int fromIndex, final int length) {
        if (length < 0 || (length & 0x1) != 0x0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length == 0) {
            return EmptyArrays.EMPTY_BYTES;
        }
        final byte[] bytes = new byte[length >>> 1];
        for (int i = 0; i < length; i += 2) {
            bytes[i >>> 1] = decodeHexByte(hexDump, fromIndex + i);
        }
        return bytes;
    }
    
    public static byte[] decodeHexDump(final CharSequence hexDump) {
        return decodeHexDump(hexDump, 0, hexDump.length());
    }
    
    public static String simpleClassName(final Object o) {
        if (o == null) {
            return "null_object";
        }
        return simpleClassName(o.getClass());
    }
    
    public static String simpleClassName(final Class<?> clazz) {
        final String className = ObjectUtil.checkNotNull(clazz, "clazz").getName();
        final int lastDotIdx = className.lastIndexOf(46);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }
    
    public static CharSequence escapeCsv(final CharSequence value) {
        return escapeCsv(value, false);
    }
    
    public static CharSequence escapeCsv(final CharSequence value, final boolean trimWhiteSpace) {
        final int length = ObjectUtil.checkNotNull(value, "value").length();
        int start;
        int last;
        if (trimWhiteSpace) {
            start = indexOfFirstNonOwsChar(value, length);
            last = indexOfLastNonOwsChar(value, start, length);
        }
        else {
            start = 0;
            last = length - 1;
        }
        if (start > last) {
            return "";
        }
        int firstUnescapedSpecial = -1;
        boolean quoted = false;
        if (isDoubleQuote(value.charAt(start))) {
            quoted = (isDoubleQuote(value.charAt(last)) && last > start);
            if (quoted) {
                ++start;
                --last;
            }
            else {
                firstUnescapedSpecial = start;
            }
        }
        if (firstUnescapedSpecial < 0) {
            if (quoted) {
                for (int i = start; i <= last; ++i) {
                    if (isDoubleQuote(value.charAt(i))) {
                        if (i == last || !isDoubleQuote(value.charAt(i + 1))) {
                            firstUnescapedSpecial = i;
                            break;
                        }
                        ++i;
                    }
                }
            }
            else {
                for (int i = start; i <= last; ++i) {
                    final char c = value.charAt(i);
                    if (c == '\n' || c == '\r' || c == ',') {
                        firstUnescapedSpecial = i;
                        break;
                    }
                    if (isDoubleQuote(c)) {
                        if (i == last || !isDoubleQuote(value.charAt(i + 1))) {
                            firstUnescapedSpecial = i;
                            break;
                        }
                        ++i;
                    }
                }
            }
            if (firstUnescapedSpecial < 0) {
                return quoted ? value.subSequence(start - 1, last + 2) : value.subSequence(start, last + 1);
            }
        }
        final StringBuilder result = new StringBuilder(last - start + 1 + 7);
        result.append('\"').append(value, start, firstUnescapedSpecial);
        for (int j = firstUnescapedSpecial; j <= last; ++j) {
            final char c2 = value.charAt(j);
            if (isDoubleQuote(c2)) {
                result.append('\"');
                if (j < last && isDoubleQuote(value.charAt(j + 1))) {
                    ++j;
                }
            }
            result.append(c2);
        }
        return result.append('\"');
    }
    
    public static CharSequence unescapeCsv(final CharSequence value) {
        final int length = ObjectUtil.checkNotNull(value, "value").length();
        if (length == 0) {
            return value;
        }
        final int last = length - 1;
        final boolean quoted = isDoubleQuote(value.charAt(0)) && isDoubleQuote(value.charAt(last)) && length != 1;
        if (!quoted) {
            validateCsvFormat(value);
            return value;
        }
        final StringBuilder unescaped = InternalThreadLocalMap.get().stringBuilder();
        for (int i = 1; i < last; ++i) {
            final char current = value.charAt(i);
            if (current == '\"') {
                if (!isDoubleQuote(value.charAt(i + 1)) || i + 1 == last) {
                    throw newInvalidEscapedCsvFieldException(value, i);
                }
                ++i;
            }
            unescaped.append(current);
        }
        return unescaped.toString();
    }
    
    public static List<CharSequence> unescapeCsvFields(final CharSequence value) {
        final List<CharSequence> unescaped = new ArrayList<CharSequence>(2);
        final StringBuilder current = InternalThreadLocalMap.get().stringBuilder();
        boolean quoted = false;
        final int last = value.length() - 1;
        for (int i = 0; i <= last; ++i) {
            final char c = value.charAt(i);
            if (quoted) {
                switch (c) {
                    case '\"': {
                        if (i == last) {
                            unescaped.add(current.toString());
                            return unescaped;
                        }
                        final char next = value.charAt(++i);
                        if (next == '\"') {
                            current.append('\"');
                            break;
                        }
                        if (next == ',') {
                            quoted = false;
                            unescaped.add(current.toString());
                            current.setLength(0);
                            break;
                        }
                        throw newInvalidEscapedCsvFieldException(value, i - 1);
                    }
                    default: {
                        current.append(c);
                        break;
                    }
                }
            }
            else {
                switch (c) {
                    case ',': {
                        unescaped.add(current.toString());
                        current.setLength(0);
                        break;
                    }
                    case '\"': {
                        if (current.length() == 0) {
                            quoted = true;
                            break;
                        }
                        throw newInvalidEscapedCsvFieldException(value, i);
                    }
                    case '\n':
                    case '\r': {
                        throw newInvalidEscapedCsvFieldException(value, i);
                    }
                    default: {
                        current.append(c);
                        break;
                    }
                }
            }
        }
        if (quoted) {
            throw newInvalidEscapedCsvFieldException(value, last);
        }
        unescaped.add(current.toString());
        return unescaped;
    }
    
    private static void validateCsvFormat(final CharSequence value) {
        final int length = value.length();
        int i = 0;
        while (i < length) {
            switch (value.charAt(i)) {
                case '\n':
                case '\r':
                case '\"':
                case ',': {
                    throw newInvalidEscapedCsvFieldException(value, i);
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
    }
    
    private static IllegalArgumentException newInvalidEscapedCsvFieldException(final CharSequence value, final int index) {
        return new IllegalArgumentException("invalid escaped CSV field: " + (Object)value + " index: " + index);
    }
    
    public static int length(final String s) {
        return (s == null) ? 0 : s.length();
    }
    
    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.isEmpty();
    }
    
    public static int indexOfNonWhiteSpace(final CharSequence seq, int offset) {
        while (offset < seq.length()) {
            if (!Character.isWhitespace(seq.charAt(offset))) {
                return offset;
            }
            ++offset;
        }
        return -1;
    }
    
    public static int indexOfWhiteSpace(final CharSequence seq, int offset) {
        while (offset < seq.length()) {
            if (Character.isWhitespace(seq.charAt(offset))) {
                return offset;
            }
            ++offset;
        }
        return -1;
    }
    
    public static boolean isSurrogate(final char c) {
        return c >= '\ud800' && c <= '\udfff';
    }
    
    private static boolean isDoubleQuote(final char c) {
        return c == '\"';
    }
    
    public static boolean endsWith(final CharSequence s, final char c) {
        final int len = s.length();
        return len > 0 && s.charAt(len - 1) == c;
    }
    
    public static CharSequence trimOws(final CharSequence value) {
        final int length = value.length();
        if (length == 0) {
            return value;
        }
        final int start = indexOfFirstNonOwsChar(value, length);
        final int end = indexOfLastNonOwsChar(value, start, length);
        return (start == 0 && end == length - 1) ? value : value.subSequence(start, end + 1);
    }
    
    public static CharSequence join(final CharSequence separator, final Iterable<? extends CharSequence> elements) {
        ObjectUtil.checkNotNull(separator, "separator");
        ObjectUtil.checkNotNull(elements, "elements");
        final Iterator<? extends CharSequence> iterator = elements.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        final CharSequence firstElement = (CharSequence)iterator.next();
        if (!iterator.hasNext()) {
            return firstElement;
        }
        final StringBuilder builder = new StringBuilder(firstElement);
        do {
            builder.append(separator).append((CharSequence)iterator.next());
        } while (iterator.hasNext());
        return builder;
    }
    
    private static int indexOfFirstNonOwsChar(final CharSequence value, final int length) {
        int i;
        for (i = 0; i < length && isOws(value.charAt(i)); ++i) {}
        return i;
    }
    
    private static int indexOfLastNonOwsChar(final CharSequence value, final int start, final int length) {
        int i;
        for (i = length - 1; i > start && isOws(value.charAt(i)); --i) {}
        return i;
    }
    
    private static boolean isOws(final char c) {
        return c == ' ' || c == '\t';
    }
    
    static {
        NEWLINE = SystemPropertyUtil.get("line.separator", "\n");
        BYTE2HEX_PAD = new String[256];
        BYTE2HEX_NOPAD = new String[256];
        for (int i = 0; i < StringUtil.BYTE2HEX_PAD.length; ++i) {
            final String str = Integer.toHexString(i);
            final int n;
            StringUtil.BYTE2HEX_PAD[n] = (((n = i) > 15) ? str : ('0' + str));
            StringUtil.BYTE2HEX_NOPAD[i] = str;
        }
        Arrays.fill(HEX2B = new byte[65536], (byte)(-1));
        StringUtil.HEX2B[48] = 0;
        StringUtil.HEX2B[49] = 1;
        StringUtil.HEX2B[50] = 2;
        StringUtil.HEX2B[51] = 3;
        StringUtil.HEX2B[52] = 4;
        StringUtil.HEX2B[53] = 5;
        StringUtil.HEX2B[54] = 6;
        StringUtil.HEX2B[55] = 7;
        StringUtil.HEX2B[56] = 8;
        StringUtil.HEX2B[57] = 9;
        StringUtil.HEX2B[65] = 10;
        StringUtil.HEX2B[66] = 11;
        StringUtil.HEX2B[67] = 12;
        StringUtil.HEX2B[68] = 13;
        StringUtil.HEX2B[69] = 14;
        StringUtil.HEX2B[70] = 15;
        StringUtil.HEX2B[97] = 10;
        StringUtil.HEX2B[98] = 11;
        StringUtil.HEX2B[99] = 12;
        StringUtil.HEX2B[100] = 13;
        StringUtil.HEX2B[101] = 14;
        StringUtil.HEX2B[102] = 15;
    }
}
