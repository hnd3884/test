package org.w3c.tidy;

import java.io.UnsupportedEncodingException;

public final class TidyUtils
{
    private static final short DIGIT = 1;
    private static final short LETTER = 2;
    private static final short NAMECHAR = 4;
    private static final short WHITE = 8;
    private static final short NEWLINE = 16;
    private static final short LOWERCASE = 32;
    private static final short UPPERCASE = 64;
    private static short[] lexmap;
    
    private TidyUtils() {
    }
    
    static boolean toBoolean(final int n) {
        return n != 0;
    }
    
    static int toUnsigned(final int n) {
        return n & 0xFF;
    }
    
    static boolean wsubstrn(final String s, final int n, final String s2) {
        final int index = s.indexOf(s2);
        return index > -1 && index <= n;
    }
    
    static boolean wsubstrncase(final String s, final int n, final String s2) {
        return wsubstrn(s.toLowerCase(), n, s2.toLowerCase());
    }
    
    static int wstrnchr(final String s, final int n, final char c) {
        final int index = s.indexOf(c);
        if (index < n) {
            return index;
        }
        return -1;
    }
    
    static boolean wsubstr(final String s, final String s2) {
        for (int length = s.length(), length2 = s2.length(), i = 0; i <= length - length2; ++i) {
            if (s2.equalsIgnoreCase(s.substring(i))) {
                return true;
            }
        }
        return false;
    }
    
    static boolean isxdigit(final char c) {
        return Character.isDigit(c) || (Character.toLowerCase(c) >= 'a' && Character.toLowerCase(c) <= 'f');
    }
    
    static boolean isInValuesIgnoreCase(final String[] array, final String s) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i].equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean findBadSubString(final String s, final String s2, int n) {
        final int i = s.length();
        int n2 = 0;
        while (i < n) {
            final String substring = s2.substring(n2, n2 + i);
            if (s.equalsIgnoreCase(substring)) {
                return !s.equals(substring);
            }
            ++n2;
            --n;
        }
        return false;
    }
    
    static boolean isXMLLetter(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '\u00c0' && c <= '\u00d6') || (c >= '\u00d8' && c <= '\u00f6') || (c >= '\u00f8' && c <= '\u00ff') || (c >= '\u0100' && c <= '\u0131') || (c >= '\u0134' && c <= '\u013e') || (c >= '\u0141' && c <= '\u0148') || (c >= '\u014a' && c <= '\u017e') || (c >= '\u0180' && c <= '\u01c3') || (c >= '\u01cd' && c <= '\u01f0') || (c >= '\u01f4' && c <= '\u01f5') || (c >= '\u01fa' && c <= '\u0217') || (c >= '\u0250' && c <= '\u02a8') || (c >= '\u02bb' && c <= '\u02c1') || c == '\u0386' || (c >= '\u0388' && c <= '\u038a') || c == '\u038c' || (c >= '\u038e' && c <= '\u03a1') || (c >= '\u03a3' && c <= '\u03ce') || (c >= '\u03d0' && c <= '\u03d6') || c == '\u03da' || c == '\u03dc' || c == '\u03de' || c == '\u03e0' || (c >= '\u03e2' && c <= '\u03f3') || (c >= '\u0401' && c <= '\u040c') || (c >= '\u040e' && c <= '\u044f') || (c >= '\u0451' && c <= '\u045c') || (c >= '\u045e' && c <= '\u0481') || (c >= '\u0490' && c <= '\u04c4') || (c >= '\u04c7' && c <= '\u04c8') || (c >= '\u04cb' && c <= '\u04cc') || (c >= '\u04d0' && c <= '\u04eb') || (c >= '\u04ee' && c <= '\u04f5') || (c >= '\u04f8' && c <= '\u04f9') || (c >= '\u0531' && c <= '\u0556') || c == '\u0559' || (c >= '\u0561' && c <= '\u0586') || (c >= '\u05d0' && c <= '\u05ea') || (c >= '\u05f0' && c <= '\u05f2') || (c >= '\u0621' && c <= '\u063a') || (c >= '\u0641' && c <= '\u064a') || (c >= '\u0671' && c <= '\u06b7') || (c >= '\u06ba' && c <= '\u06be') || (c >= '\u06c0' && c <= '\u06ce') || (c >= '\u06d0' && c <= '\u06d3') || c == '\u06d5' || (c >= '\u06e5' && c <= '\u06e6') || (c >= '\u0905' && c <= '\u0939') || c == '\u093d' || (c >= '\u0958' && c <= '\u0961') || (c >= '\u0985' && c <= '\u098c') || (c >= '\u098f' && c <= '\u0990') || (c >= '\u0993' && c <= '\u09a8') || (c >= '\u09aa' && c <= '\u09b0') || c == '\u09b2' || (c >= '\u09b6' && c <= '\u09b9') || (c >= '\u09dc' && c <= '\u09dd') || (c >= '\u09df' && c <= '\u09e1') || (c >= '\u09f0' && c <= '\u09f1') || (c >= '\u0a05' && c <= '\u0a0a') || (c >= '\u0a0f' && c <= '\u0a10') || (c >= '\u0a13' && c <= '\u0a28') || (c >= '\u0a2a' && c <= '\u0a30') || (c >= '\u0a32' && c <= '\u0a33') || (c >= '\u0a35' && c <= '\u0a36') || (c >= '\u0a38' && c <= '\u0a39') || (c >= '\u0a59' && c <= '\u0a5c') || c == '\u0a5e' || (c >= '\u0a72' && c <= '\u0a74') || (c >= '\u0a85' && c <= '\u0a8b') || c == '\u0a8d' || (c >= '\u0a8f' && c <= '\u0a91') || (c >= '\u0a93' && c <= '\u0aa8') || (c >= '\u0aaa' && c <= '\u0ab0') || (c >= '\u0ab2' && c <= '\u0ab3') || (c >= '\u0ab5' && c <= '\u0ab9') || c == '\u0abd' || c == '\u0ae0' || (c >= '\u0b05' && c <= '\u0b0c') || (c >= '\u0b0f' && c <= '\u0b10') || (c >= '\u0b13' && c <= '\u0b28') || (c >= '\u0b2a' && c <= '\u0b30') || (c >= '\u0b32' && c <= '\u0b33') || (c >= '\u0b36' && c <= '\u0b39') || c == '\u0b3d' || (c >= '\u0b5c' && c <= '\u0b5d') || (c >= '\u0b5f' && c <= '\u0b61') || (c >= '\u0b85' && c <= '\u0b8a') || (c >= '\u0b8e' && c <= '\u0b90') || (c >= '\u0b92' && c <= '\u0b95') || (c >= '\u0b99' && c <= '\u0b9a') || c == '\u0b9c' || (c >= '\u0b9e' && c <= '\u0b9f') || (c >= '\u0ba3' && c <= '\u0ba4') || (c >= '\u0ba8' && c <= '\u0baa') || (c >= '\u0bae' && c <= '\u0bb5') || (c >= '\u0bb7' && c <= '\u0bb9') || (c >= '\u0c05' && c <= '\u0c0c') || (c >= '\u0c0e' && c <= '\u0c10') || (c >= '\u0c12' && c <= '\u0c28') || (c >= '\u0c2a' && c <= '\u0c33') || (c >= '\u0c35' && c <= '\u0c39') || (c >= '\u0c60' && c <= '\u0c61') || (c >= '\u0c85' && c <= '\u0c8c') || (c >= '\u0c8e' && c <= '\u0c90') || (c >= '\u0c92' && c <= '\u0ca8') || (c >= '\u0caa' && c <= '\u0cb3') || (c >= '\u0cb5' && c <= '\u0cb9') || c == '\u0cde' || (c >= '\u0ce0' && c <= '\u0ce1') || (c >= '\u0d05' && c <= '\u0d0c') || (c >= '\u0d0e' && c <= '\u0d10') || (c >= '\u0d12' && c <= '\u0d28') || (c >= '\u0d2a' && c <= '\u0d39') || (c >= '\u0d60' && c <= '\u0d61') || (c >= '\u0e01' && c <= '\u0e2e') || c == '\u0e30' || (c >= '\u0e32' && c <= '\u0e33') || (c >= '\u0e40' && c <= '\u0e45') || (c >= '\u0e81' && c <= '\u0e82') || c == '\u0e84' || (c >= '\u0e87' && c <= '\u0e88') || c == '\u0e8a' || c == '\u0e8d' || (c >= '\u0e94' && c <= '\u0e97') || (c >= '\u0e99' && c <= '\u0e9f') || (c >= '\u0ea1' && c <= '\u0ea3') || c == '\u0ea5' || c == '\u0ea7' || (c >= '\u0eaa' && c <= '\u0eab') || (c >= '\u0ead' && c <= '\u0eae') || c == '\u0eb0' || (c >= '\u0eb2' && c <= '\u0eb3') || c == '\u0ebd' || (c >= '\u0ec0' && c <= '\u0ec4') || (c >= '\u0f40' && c <= '\u0f47') || (c >= '\u0f49' && c <= '\u0f69') || (c >= '\u10a0' && c <= '\u10c5') || (c >= '\u10d0' && c <= '\u10f6') || c == '\u1100' || (c >= '\u1102' && c <= '\u1103') || (c >= '\u1105' && c <= '\u1107') || c == '\u1109' || (c >= '\u110b' && c <= '\u110c') || (c >= '\u110e' && c <= '\u1112') || c == '\u113c' || c == '\u113e' || c == '\u1140' || c == '\u114c' || c == '\u114e' || c == '\u1150' || (c >= '\u1154' && c <= '\u1155') || c == '\u1159' || (c >= '\u115f' && c <= '\u1161') || c == '\u1163' || c == '\u1165' || c == '\u1167' || c == '\u1169' || (c >= '\u116d' && c <= '\u116e') || (c >= '\u1172' && c <= '\u1173') || c == '\u1175' || c == '\u119e' || c == '\u11a8' || c == '\u11ab' || (c >= '\u11ae' && c <= '\u11af') || (c >= '\u11b7' && c <= '\u11b8') || c == '\u11ba' || (c >= '\u11bc' && c <= '\u11c2') || c == '\u11eb' || c == '\u11f0' || c == '\u11f9' || (c >= '\u1e00' && c <= '\u1e9b') || (c >= '\u1ea0' && c <= '\u1ef9') || (c >= '\u1f00' && c <= '\u1f15') || (c >= '\u1f18' && c <= '\u1f1d') || (c >= '\u1f20' && c <= '\u1f45') || (c >= '\u1f48' && c <= '\u1f4d') || (c >= '\u1f50' && c <= '\u1f57') || c == '\u1f59' || c == '\u1f5b' || c == '\u1f5d' || (c >= '\u1f5f' && c <= '\u1f7d') || (c >= '\u1f80' && c <= '\u1fb4') || (c >= '\u1fb6' && c <= '\u1fbc') || c == '\u1fbe' || (c >= '\u1fc2' && c <= '\u1fc4') || (c >= '\u1fc6' && c <= '\u1fcc') || (c >= '\u1fd0' && c <= '\u1fd3') || (c >= '\u1fd6' && c <= '\u1fdb') || (c >= '\u1fe0' && c <= '\u1fec') || (c >= '\u1ff2' && c <= '\u1ff4') || (c >= '\u1ff6' && c <= '\u1ffc') || c == '\u2126' || (c >= '\u212a' && c <= '\u212b') || c == '\u212e' || (c >= '\u2180' && c <= '\u2182') || (c >= '\u3041' && c <= '\u3094') || (c >= '\u30a1' && c <= '\u30fa') || (c >= '\u3105' && c <= '\u312c') || (c >= '\uac00' && c <= '\ud7a3') || (c >= '\u4e00' && c <= '\u9fa5') || c == '\u3007' || (c >= '\u3021' && c <= '\u3029') || (c >= '\u4e00' && c <= '\u9fa5') || c == '\u3007' || (c >= '\u3021' && c <= '\u3029');
    }
    
    static boolean isXMLNamechar(final char c) {
        return isXMLLetter(c) || c == '.' || c == '_' || c == ':' || c == '-' || (c >= '\u0300' && c <= '\u0345') || (c >= '\u0360' && c <= '\u0361') || (c >= '\u0483' && c <= '\u0486') || (c >= '\u0591' && c <= '\u05a1') || (c >= '\u05a3' && c <= '\u05b9') || (c >= '\u05bb' && c <= '\u05bd') || c == '\u05bf' || (c >= '\u05c1' && c <= '\u05c2') || c == '\u05c4' || (c >= '\u064b' && c <= '\u0652') || c == '\u0670' || (c >= '\u06d6' && c <= '\u06dc') || (c >= '\u06dd' && c <= '\u06df') || (c >= '\u06e0' && c <= '\u06e4') || (c >= '\u06e7' && c <= '\u06e8') || (c >= '\u06ea' && c <= '\u06ed') || (c >= '\u0901' && c <= '\u0903') || c == '\u093c' || (c >= '\u093e' && c <= '\u094c') || c == '\u094d' || (c >= '\u0951' && c <= '\u0954') || (c >= '\u0962' && c <= '\u0963') || (c >= '\u0981' && c <= '\u0983') || c == '\u09bc' || c == '\u09be' || c == '\u09bf' || (c >= '\u09c0' && c <= '\u09c4') || (c >= '\u09c7' && c <= '\u09c8') || (c >= '\u09cb' && c <= '\u09cd') || c == '\u09d7' || (c >= '\u09e2' && c <= '\u09e3') || c == '\u0a02' || c == '\u0a3c' || c == '\u0a3e' || c == '\u0a3f' || (c >= '\u0a40' && c <= '\u0a42') || (c >= '\u0a47' && c <= '\u0a48') || (c >= '\u0a4b' && c <= '\u0a4d') || (c >= '\u0a70' && c <= '\u0a71') || (c >= '\u0a81' && c <= '\u0a83') || c == '\u0abc' || (c >= '\u0abe' && c <= '\u0ac5') || (c >= '\u0ac7' && c <= '\u0ac9') || (c >= '\u0acb' && c <= '\u0acd') || (c >= '\u0b01' && c <= '\u0b03') || c == '\u0b3c' || (c >= '\u0b3e' && c <= '\u0b43') || (c >= '\u0b47' && c <= '\u0b48') || (c >= '\u0b4b' && c <= '\u0b4d') || (c >= '\u0b56' && c <= '\u0b57') || (c >= '\u0b82' && c <= '\u0b83') || (c >= '\u0bbe' && c <= '\u0bc2') || (c >= '\u0bc6' && c <= '\u0bc8') || (c >= '\u0bca' && c <= '\u0bcd') || c == '\u0bd7' || (c >= '\u0c01' && c <= '\u0c03') || (c >= '\u0c3e' && c <= '\u0c44') || (c >= '\u0c46' && c <= '\u0c48') || (c >= '\u0c4a' && c <= '\u0c4d') || (c >= '\u0c55' && c <= '\u0c56') || (c >= '\u0c82' && c <= '\u0c83') || (c >= '\u0cbe' && c <= '\u0cc4') || (c >= '\u0cc6' && c <= '\u0cc8') || (c >= '\u0cca' && c <= '\u0ccd') || (c >= '\u0cd5' && c <= '\u0cd6') || (c >= '\u0d02' && c <= '\u0d03') || (c >= '\u0d3e' && c <= '\u0d43') || (c >= '\u0d46' && c <= '\u0d48') || (c >= '\u0d4a' && c <= '\u0d4d') || c == '\u0d57' || c == '\u0e31' || (c >= '\u0e34' && c <= '\u0e3a') || (c >= '\u0e47' && c <= '\u0e4e') || c == '\u0eb1' || (c >= '\u0eb4' && c <= '\u0eb9') || (c >= '\u0ebb' && c <= '\u0ebc') || (c >= '\u0ec8' && c <= '\u0ecd') || (c >= '\u0f18' && c <= '\u0f19') || c == '\u0f35' || c == '\u0f37' || c == '\u0f39' || c == '\u0f3e' || c == '\u0f3f' || (c >= '\u0f71' && c <= '\u0f84') || (c >= '\u0f86' && c <= '\u0f8b') || (c >= '\u0f90' && c <= '\u0f95') || c == '\u0f97' || (c >= '\u0f99' && c <= '\u0fad') || (c >= '\u0fb1' && c <= '\u0fb7') || c == '\u0fb9' || (c >= '\u20d0' && c <= '\u20dc') || c == '\u20e1' || (c >= '\u302a' && c <= '\u302f') || c == '\u3099' || c == '\u309a' || (c >= '0' && c <= '9') || (c >= '\u0660' && c <= '\u0669') || (c >= '\u06f0' && c <= '\u06f9') || (c >= '\u0966' && c <= '\u096f') || (c >= '\u09e6' && c <= '\u09ef') || (c >= '\u0a66' && c <= '\u0a6f') || (c >= '\u0ae6' && c <= '\u0aef') || (c >= '\u0b66' && c <= '\u0b6f') || (c >= '\u0be7' && c <= '\u0bef') || (c >= '\u0c66' && c <= '\u0c6f') || (c >= '\u0ce6' && c <= '\u0cef') || (c >= '\u0d66' && c <= '\u0d6f') || (c >= '\u0e50' && c <= '\u0e59') || (c >= '\u0ed0' && c <= '\u0ed9') || (c >= '\u0f20' && c <= '\u0f29') || c == '·' || c == '\u02d0' || c == '\u02d1' || c == '\u0387' || c == '\u0640' || c == '\u0e46' || c == '\u0ec6' || c == '\u3005' || (c >= '\u3031' && c <= '\u3035') || (c >= '\u309d' && c <= '\u309e') || (c >= '\u30fc' && c <= '\u30fe');
    }
    
    static boolean isQuote(final int n) {
        return n == 39 || n == 34;
    }
    
    public static byte[] getBytes(final String s) {
        try {
            return s.getBytes("UTF8");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new Error("String to UTF-8 conversion failed: " + ex.getMessage());
        }
    }
    
    public static String getString(final byte[] array, final int n, final int n2) {
        try {
            return new String(array, n, n2, "UTF8");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new Error("UTF-8 to string conversion failed: " + ex.getMessage());
        }
    }
    
    public static int lastChar(final String s) {
        if (s != null && s.length() > 0) {
            return s.charAt(s.length() - 1);
        }
        return 0;
    }
    
    public static boolean isWhite(final char c) {
        return toBoolean(map(c) & 0x8);
    }
    
    public static boolean isDigit(final char c) {
        return toBoolean(map(c) & 0x1);
    }
    
    public static boolean isLetter(final char c) {
        return toBoolean(map(c) & 0x2);
    }
    
    public static boolean isNamechar(final char c) {
        return toBoolean(map(c) & 0x4);
    }
    
    public static boolean isLower(final char c) {
        return toBoolean(map(c) & 0x20);
    }
    
    public static boolean isUpper(final char c) {
        return toBoolean(map(c) & 0x40);
    }
    
    public static char toLower(char c) {
        if (toBoolean(map(c) & 0x40)) {
            c = (char)(c + 'a' - 65);
        }
        return c;
    }
    
    public static char toUpper(char c) {
        if (toBoolean(map(c) & 0x20)) {
            c = (char)(c + 'A' - 97);
        }
        return c;
    }
    
    public static char foldCase(char c, final boolean b, final boolean b2) {
        if (!b2) {
            if (b) {
                if (isLower(c)) {
                    c = toUpper(c);
                }
            }
            else if (isUpper(c)) {
                c = toLower(c);
            }
        }
        return c;
    }
    
    private static void mapStr(final String s, final short n) {
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            final short[] lexmap = TidyUtils.lexmap;
            final char c = char1;
            lexmap[c] |= n;
        }
    }
    
    private static short map(final char c) {
        return (short)((c < '\u0080') ? TidyUtils.lexmap[c] : 0);
    }
    
    public static boolean isCharEncodingSupported(String java) {
        java = EncodingNameMapper.toJava(java);
        if (java == null) {
            return false;
        }
        try {
            "".getBytes(java);
        }
        catch (final UnsupportedEncodingException ex) {
            return false;
        }
        return true;
    }
    
    static {
        TidyUtils.lexmap = new short[128];
        mapStr("\r\n\f", (short)24);
        mapStr(" \t", (short)8);
        mapStr("-.:_", (short)4);
        mapStr("0123456789", (short)5);
        mapStr("abcdefghijklmnopqrstuvwxyz", (short)38);
        mapStr("ABCDEFGHIJKLMNOPQRSTUVWXYZ", (short)70);
    }
}
