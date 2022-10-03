package org.apache.xerces.util;

import java.util.Arrays;

public class XML11Char
{
    private static final byte[] XML11CHARS;
    public static final int MASK_XML11_VALID = 1;
    public static final int MASK_XML11_SPACE = 2;
    public static final int MASK_XML11_NAME_START = 4;
    public static final int MASK_XML11_NAME = 8;
    public static final int MASK_XML11_CONTROL = 16;
    public static final int MASK_XML11_CONTENT = 32;
    public static final int MASK_XML11_NCNAME_START = 64;
    public static final int MASK_XML11_NCNAME = 128;
    public static final int MASK_XML11_CONTENT_INTERNAL = 48;
    
    public static boolean isXML11Space(final int n) {
        return n < 65536 && (XML11Char.XML11CHARS[n] & 0x2) != 0x0;
    }
    
    public static boolean isXML11Valid(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x1) != 0x0) || (65536 <= n && n <= 1114111);
    }
    
    public static boolean isXML11Invalid(final int n) {
        return !isXML11Valid(n);
    }
    
    public static boolean isXML11ValidLiteral(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x1) != 0x0 && (XML11Char.XML11CHARS[n] & 0x10) == 0x0) || (65536 <= n && n <= 1114111);
    }
    
    public static boolean isXML11Content(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x20) != 0x0) || (65536 <= n && n <= 1114111);
    }
    
    public static boolean isXML11InternalEntityContent(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x30) != 0x0) || (65536 <= n && n <= 1114111);
    }
    
    public static boolean isXML11NameStart(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x4) != 0x0) || (65536 <= n && n < 983040);
    }
    
    public static boolean isXML11Name(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x8) != 0x0) || (n >= 65536 && n < 983040);
    }
    
    public static boolean isXML11NCNameStart(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x40) != 0x0) || (65536 <= n && n < 983040);
    }
    
    public static boolean isXML11NCName(final int n) {
        return (n < 65536 && (XML11Char.XML11CHARS[n] & 0x80) != 0x0) || (65536 <= n && n < 983040);
    }
    
    public static boolean isXML11NameHighSurrogate(final int n) {
        return 55296 <= n && n <= 56191;
    }
    
    public static boolean isXML11ValidName(final String s) {
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        int i = 1;
        final char char1 = s.charAt(0);
        if (!isXML11NameStart(char1)) {
            if (length <= 1 || !isXML11NameHighSurrogate(char1)) {
                return false;
            }
            final char char2 = s.charAt(1);
            if (!XMLChar.isLowSurrogate(char2) || !isXML11NameStart(XMLChar.supplemental(char1, char2))) {
                return false;
            }
            i = 2;
        }
        while (i < length) {
            final char char3 = s.charAt(i);
            if (!isXML11Name(char3)) {
                if (++i >= length || !isXML11NameHighSurrogate(char3)) {
                    return false;
                }
                final char char4 = s.charAt(i);
                if (!XMLChar.isLowSurrogate(char4) || !isXML11Name(XMLChar.supplemental(char3, char4))) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }
    
    public static boolean isXML11ValidNCName(final String s) {
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        int i = 1;
        final char char1 = s.charAt(0);
        if (!isXML11NCNameStart(char1)) {
            if (length <= 1 || !isXML11NameHighSurrogate(char1)) {
                return false;
            }
            final char char2 = s.charAt(1);
            if (!XMLChar.isLowSurrogate(char2) || !isXML11NCNameStart(XMLChar.supplemental(char1, char2))) {
                return false;
            }
            i = 2;
        }
        while (i < length) {
            final char char3 = s.charAt(i);
            if (!isXML11NCName(char3)) {
                if (++i >= length || !isXML11NameHighSurrogate(char3)) {
                    return false;
                }
                final char char4 = s.charAt(i);
                if (!XMLChar.isLowSurrogate(char4) || !isXML11NCName(XMLChar.supplemental(char3, char4))) {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }
    
    public static boolean isXML11ValidNmtoken(final String s) {
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (!isXML11Name(char1)) {
                if (++i >= length || !isXML11NameHighSurrogate(char1)) {
                    return false;
                }
                final char char2 = s.charAt(i);
                if (!XMLChar.isLowSurrogate(char2) || !isXML11Name(XMLChar.supplemental(char1, char2))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static {
        Arrays.fill(XML11CHARS = new byte[65536], 1, 9, (byte)17);
        XML11Char.XML11CHARS[9] = 35;
        XML11Char.XML11CHARS[10] = 3;
        Arrays.fill(XML11Char.XML11CHARS, 11, 13, (byte)17);
        XML11Char.XML11CHARS[13] = 3;
        Arrays.fill(XML11Char.XML11CHARS, 14, 32, (byte)17);
        XML11Char.XML11CHARS[32] = 35;
        Arrays.fill(XML11Char.XML11CHARS, 33, 38, (byte)33);
        XML11Char.XML11CHARS[38] = 1;
        Arrays.fill(XML11Char.XML11CHARS, 39, 45, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 45, 47, (byte)(-87));
        XML11Char.XML11CHARS[47] = 33;
        Arrays.fill(XML11Char.XML11CHARS, 48, 58, (byte)(-87));
        XML11Char.XML11CHARS[58] = 45;
        XML11Char.XML11CHARS[59] = 33;
        XML11Char.XML11CHARS[60] = 1;
        Arrays.fill(XML11Char.XML11CHARS, 61, 65, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 65, 91, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 91, 93, (byte)33);
        XML11Char.XML11CHARS[93] = 1;
        XML11Char.XML11CHARS[94] = 33;
        XML11Char.XML11CHARS[95] = -19;
        XML11Char.XML11CHARS[96] = 33;
        Arrays.fill(XML11Char.XML11CHARS, 97, 123, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 123, 127, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 127, 133, (byte)17);
        XML11Char.XML11CHARS[133] = 35;
        Arrays.fill(XML11Char.XML11CHARS, 134, 160, (byte)17);
        Arrays.fill(XML11Char.XML11CHARS, 160, 183, (byte)33);
        XML11Char.XML11CHARS[183] = -87;
        Arrays.fill(XML11Char.XML11CHARS, 184, 192, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 192, 215, (byte)(-19));
        XML11Char.XML11CHARS[215] = 33;
        Arrays.fill(XML11Char.XML11CHARS, 216, 247, (byte)(-19));
        XML11Char.XML11CHARS[247] = 33;
        Arrays.fill(XML11Char.XML11CHARS, 248, 768, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 768, 880, (byte)(-87));
        Arrays.fill(XML11Char.XML11CHARS, 880, 894, (byte)(-19));
        XML11Char.XML11CHARS[894] = 33;
        Arrays.fill(XML11Char.XML11CHARS, 895, 8192, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 8192, 8204, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 8204, 8206, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 8206, 8232, (byte)33);
        XML11Char.XML11CHARS[8232] = 35;
        Arrays.fill(XML11Char.XML11CHARS, 8233, 8255, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 8255, 8257, (byte)(-87));
        Arrays.fill(XML11Char.XML11CHARS, 8257, 8304, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 8304, 8592, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 8592, 11264, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 11264, 12272, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 12272, 12289, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 12289, 55296, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 57344, 63744, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 63744, 64976, (byte)(-19));
        Arrays.fill(XML11Char.XML11CHARS, 64976, 65008, (byte)33);
        Arrays.fill(XML11Char.XML11CHARS, 65008, 65534, (byte)(-19));
    }
}
