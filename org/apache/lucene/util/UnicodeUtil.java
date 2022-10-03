package org.apache.lucene.util;

public final class UnicodeUtil
{
    public static final BytesRef BIG_TERM;
    public static final int UNI_SUR_HIGH_START = 55296;
    public static final int UNI_SUR_HIGH_END = 56319;
    public static final int UNI_SUR_LOW_START = 56320;
    public static final int UNI_SUR_LOW_END = 57343;
    public static final int UNI_REPLACEMENT_CHAR = 65533;
    private static final long UNI_MAX_BMP = 65535L;
    private static final long HALF_SHIFT = 10L;
    private static final long HALF_MASK = 1023L;
    private static final int SURROGATE_OFFSET = -56613888;
    public static final int MAX_UTF8_BYTES_PER_CHAR = 3;
    static final int[] utf8CodeLength;
    private static final int LEAD_SURROGATE_SHIFT_ = 10;
    private static final int TRAIL_SURROGATE_MASK_ = 1023;
    private static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
    private static final int LEAD_SURROGATE_MIN_VALUE = 55296;
    private static final int SUPPLEMENTARY_MIN_VALUE = 65536;
    private static final int LEAD_SURROGATE_OFFSET_ = 55232;
    
    private UnicodeUtil() {
    }
    
    public static int UTF16toUTF8(final char[] source, final int offset, final int length, final byte[] out) {
        int upto = 0;
        int i = offset;
        final int end = offset + length;
        while (i < end) {
            final int code = source[i++];
            if (code < 128) {
                out[upto++] = (byte)code;
            }
            else if (code < 2048) {
                out[upto++] = (byte)(0xC0 | code >> 6);
                out[upto++] = (byte)(0x80 | (code & 0x3F));
            }
            else if (code < 55296 || code > 57343) {
                out[upto++] = (byte)(0xE0 | code >> 12);
                out[upto++] = (byte)(0x80 | (code >> 6 & 0x3F));
                out[upto++] = (byte)(0x80 | (code & 0x3F));
            }
            else {
                if (code < 56320 && i < end) {
                    int utf32 = source[i];
                    if (utf32 >= 56320 && utf32 <= 57343) {
                        utf32 = (code << 10) + utf32 - 56613888;
                        ++i;
                        out[upto++] = (byte)(0xF0 | utf32 >> 18);
                        out[upto++] = (byte)(0x80 | (utf32 >> 12 & 0x3F));
                        out[upto++] = (byte)(0x80 | (utf32 >> 6 & 0x3F));
                        out[upto++] = (byte)(0x80 | (utf32 & 0x3F));
                        continue;
                    }
                }
                out[upto++] = -17;
                out[upto++] = -65;
                out[upto++] = -67;
            }
        }
        return upto;
    }
    
    public static int UTF16toUTF8(final CharSequence s, final int offset, final int length, final byte[] out) {
        return UTF16toUTF8(s, offset, length, out, 0);
    }
    
    public static int UTF16toUTF8(final CharSequence s, final int offset, final int length, final byte[] out, final int outOffset) {
        final int end = offset + length;
        int upto = outOffset;
        for (int i = offset; i < end; ++i) {
            final int code = s.charAt(i);
            if (code < 128) {
                out[upto++] = (byte)code;
            }
            else if (code < 2048) {
                out[upto++] = (byte)(0xC0 | code >> 6);
                out[upto++] = (byte)(0x80 | (code & 0x3F));
            }
            else if (code < 55296 || code > 57343) {
                out[upto++] = (byte)(0xE0 | code >> 12);
                out[upto++] = (byte)(0x80 | (code >> 6 & 0x3F));
                out[upto++] = (byte)(0x80 | (code & 0x3F));
            }
            else {
                if (code < 56320 && i < end - 1) {
                    int utf32 = s.charAt(i + 1);
                    if (utf32 >= 56320 && utf32 <= 57343) {
                        utf32 = (code << 10) + utf32 - 56613888;
                        ++i;
                        out[upto++] = (byte)(0xF0 | utf32 >> 18);
                        out[upto++] = (byte)(0x80 | (utf32 >> 12 & 0x3F));
                        out[upto++] = (byte)(0x80 | (utf32 >> 6 & 0x3F));
                        out[upto++] = (byte)(0x80 | (utf32 & 0x3F));
                        continue;
                    }
                }
                out[upto++] = -17;
                out[upto++] = -65;
                out[upto++] = -67;
            }
        }
        return upto;
    }
    
    public static int calcUTF16toUTF8Length(final CharSequence s, final int offset, final int len) {
        final int end = offset + len;
        int res = 0;
        for (int i = offset; i < end; ++i) {
            final int code = s.charAt(i);
            if (code < 128) {
                ++res;
            }
            else if (code < 2048) {
                res += 2;
            }
            else if (code < 55296 || code > 57343) {
                res += 3;
            }
            else {
                if (code < 56320 && i < end - 1) {
                    final int utf32 = s.charAt(i + 1);
                    if (utf32 >= 56320 && utf32 <= 57343) {
                        ++i;
                        res += 4;
                        continue;
                    }
                }
                res += 3;
            }
        }
        return res;
    }
    
    public static boolean validUTF16String(final CharSequence s) {
        for (int size = s.length(), i = 0; i < size; ++i) {
            final char ch = s.charAt(i);
            if (ch >= '\ud800' && ch <= '\udbff') {
                if (i >= size - 1) {
                    return false;
                }
                ++i;
                final char nextCH = s.charAt(i);
                if (nextCH < '\udc00' || nextCH > '\udfff') {
                    return false;
                }
            }
            else if (ch >= '\udc00' && ch <= '\udfff') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean validUTF16String(final char[] s, final int size) {
        for (int i = 0; i < size; ++i) {
            final char ch = s[i];
            if (ch >= '\ud800' && ch <= '\udbff') {
                if (i >= size - 1) {
                    return false;
                }
                ++i;
                final char nextCH = s[i];
                if (nextCH < '\udc00' || nextCH > '\udfff') {
                    return false;
                }
            }
            else if (ch >= '\udc00' && ch <= '\udfff') {
                return false;
            }
        }
        return true;
    }
    
    public static int codePointCount(final BytesRef utf8) {
        int pos = utf8.offset;
        final int limit = pos + utf8.length;
        final byte[] bytes = utf8.bytes;
        int codePointCount = 0;
        while (pos < limit) {
            final int v = bytes[pos] & 0xFF;
            Label_0106: {
                if (v >= 128) {
                    if (v >= 192) {
                        if (v < 224) {
                            pos += 2;
                            break Label_0106;
                        }
                        if (v < 240) {
                            pos += 3;
                            break Label_0106;
                        }
                        if (v < 248) {
                            pos += 4;
                            break Label_0106;
                        }
                    }
                    throw new IllegalArgumentException();
                }
                ++pos;
            }
            ++codePointCount;
        }
        if (pos > limit) {
            throw new IllegalArgumentException();
        }
        return codePointCount;
    }
    
    public static int UTF8toUTF32(final BytesRef utf8, final int[] ints) {
        int utf32Count = 0;
        int utf8Upto = utf8.offset;
        final byte[] bytes = utf8.bytes;
        final int utf8Limit = utf8.offset + utf8.length;
        while (utf8Upto < utf8Limit) {
            final int numBytes = UnicodeUtil.utf8CodeLength[bytes[utf8Upto] & 0xFF];
            int v = 0;
            switch (numBytes) {
                case 1: {
                    ints[utf32Count++] = bytes[utf8Upto++];
                    continue;
                }
                case 2: {
                    v = (bytes[utf8Upto++] & 0x1F);
                    break;
                }
                case 3: {
                    v = (bytes[utf8Upto++] & 0xF);
                    break;
                }
                case 4: {
                    v = (bytes[utf8Upto++] & 0x7);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("invalid utf8");
                }
            }
            for (int limit = utf8Upto + numBytes - 1; utf8Upto < limit; v = (v << 6 | (bytes[utf8Upto++] & 0x3F))) {}
            ints[utf32Count++] = v;
        }
        return utf32Count;
    }
    
    public static String newString(final int[] codePoints, final int offset, final int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        char[] chars = new char[count];
        int w = 0;
        for (int r = offset, e = offset + count; r < e; ++r) {
            final int cp = codePoints[r];
            if (cp < 0 || cp > 1114111) {
                throw new IllegalArgumentException();
            }
            while (true) {
                try {
                    if (cp < 65536) {
                        chars[w] = (char)cp;
                        ++w;
                    }
                    else {
                        chars[w] = (char)(55232 + (cp >> 10));
                        chars[w + 1] = (char)(56320 + (cp & 0x3FF));
                        w += 2;
                    }
                }
                catch (final IndexOutOfBoundsException ex) {
                    final int newlen = (int)Math.ceil(codePoints.length * (double)(w + 2) / (r - offset + 1));
                    final char[] temp = new char[newlen];
                    System.arraycopy(chars, 0, temp, 0, w);
                    chars = temp;
                    continue;
                }
                break;
            }
        }
        return new String(chars, 0, w);
    }
    
    public static String toHexString(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (i > 0) {
                sb.append(' ');
            }
            if (ch < '\u0080') {
                sb.append(ch);
            }
            else {
                if (ch >= '\ud800' && ch <= '\udbff') {
                    sb.append("H:");
                }
                else if (ch >= '\udc00' && ch <= '\udfff') {
                    sb.append("L:");
                }
                else if (ch > '\udfff') {
                    if (ch == '\uffff') {
                        sb.append("F:");
                    }
                    else {
                        sb.append("E:");
                    }
                }
                sb.append("0x" + Integer.toHexString(ch));
            }
        }
        return sb.toString();
    }
    
    public static int UTF8toUTF16(final byte[] utf8, int offset, final int length, final char[] out) {
        int out_offset = 0;
        final int limit = offset + length;
        while (offset < limit) {
            final int b = utf8[offset++] & 0xFF;
            if (b < 192) {
                assert b < 128;
                out[out_offset++] = (char)b;
            }
            else if (b < 224) {
                out[out_offset++] = (char)(((b & 0x1F) << 6) + (utf8[offset++] & 0x3F));
            }
            else if (b < 240) {
                out[out_offset++] = (char)(((b & 0xF) << 12) + ((utf8[offset] & 0x3F) << 6) + (utf8[offset + 1] & 0x3F));
                offset += 2;
            }
            else {
                assert b < 248 : "b = 0x" + Integer.toHexString(b);
                final int ch = ((b & 0x7) << 18) + ((utf8[offset] & 0x3F) << 12) + ((utf8[offset + 1] & 0x3F) << 6) + (utf8[offset + 2] & 0x3F);
                offset += 3;
                if (ch < 65535L) {
                    out[out_offset++] = (char)ch;
                }
                else {
                    final int chHalf = ch - 65536;
                    out[out_offset++] = (char)((chHalf >> 10) + 55296);
                    out[out_offset++] = (char)(((long)chHalf & 0x3FFL) + 56320L);
                }
            }
        }
        return out_offset;
    }
    
    public static int UTF8toUTF16(final BytesRef bytesRef, final char[] chars) {
        return UTF8toUTF16(bytesRef.bytes, bytesRef.offset, bytesRef.length, chars);
    }
    
    static {
        BIG_TERM = new BytesRef(new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 });
        final int v = Integer.MIN_VALUE;
        utf8CodeLength = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 };
    }
}
