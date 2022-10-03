package sun.invoke.util;

public class BytecodeName
{
    static char ESCAPE_C;
    static char NULL_ESCAPE_C;
    static String NULL_ESCAPE;
    static final String DANGEROUS_CHARS = "\\/.;:$[]<>";
    static final String REPLACEMENT_CHARS = "-|,?!%{}^_";
    static final int DANGEROUS_CHAR_FIRST_INDEX = 1;
    static char[] DANGEROUS_CHARS_A;
    static char[] REPLACEMENT_CHARS_A;
    static final Character[] DANGEROUS_CHARS_CA;
    static final long[] SPECIAL_BITMAP;
    
    private BytecodeName() {
    }
    
    public static String toBytecodeName(final String s) {
        final String mangle = mangle(s);
        assert !(!looksMangled(mangle)) : mangle;
        assert s.equals(toSourceName(mangle)) : s;
        return mangle;
    }
    
    public static String toSourceName(final String s) {
        checkSafeBytecodeName(s);
        String demangle = s;
        if (looksMangled(s)) {
            demangle = demangle(s);
            assert s.equals(mangle(demangle)) : s + " => " + demangle + " => " + mangle(demangle);
        }
        return demangle;
    }
    
    public static Object[] parseBytecodeName(final String s) {
        final int length = s.length();
        Object[] array = null;
        int i = 0;
        while (i <= 1) {
            int n = 0;
            int n2 = 0;
            for (int j = 0; j <= length; ++j) {
                int index = -1;
                if (j < length) {
                    index = "\\/.;:$[]<>".indexOf(s.charAt(j));
                    if (index < 1) {
                        continue;
                    }
                }
                if (n2 < j) {
                    if (i != 0) {
                        array[n] = toSourceName(s.substring(n2, j));
                    }
                    ++n;
                    n2 = j + 1;
                }
                if (index >= 1) {
                    if (i != 0) {
                        array[n] = BytecodeName.DANGEROUS_CHARS_CA[index];
                    }
                    ++n;
                    n2 = j + 1;
                }
            }
            if (i != 0) {
                break;
            }
            array = new Object[n];
            if (n <= 1 && n2 == 0) {
                if (n != 0) {
                    array[0] = toSourceName(s);
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return array;
    }
    
    public static String unparseBytecodeName(Object[] array) {
        final Object[] array2 = array;
        for (int i = 0; i < array.length; ++i) {
            final Object o = array[i];
            if (o instanceof String) {
                final String bytecodeName = toBytecodeName((String)o);
                if (i == 0 && array.length == 1) {
                    return bytecodeName;
                }
                if (bytecodeName != o) {
                    if (array == array2) {
                        array = array.clone();
                    }
                    array[i] = bytecodeName;
                }
            }
        }
        return appendAll(array);
    }
    
    private static String appendAll(final Object[] array) {
        if (array.length > 1) {
            int n = 0;
            for (final Object o : array) {
                if (o instanceof String) {
                    n += String.valueOf(o).length();
                }
                else {
                    ++n;
                }
            }
            final StringBuilder sb = new StringBuilder(n);
            for (int length2 = array.length, j = 0; j < length2; ++j) {
                sb.append(array[j]);
            }
            return sb.toString();
        }
        if (array.length == 1) {
            return String.valueOf(array[0]);
        }
        return "";
    }
    
    public static String toDisplayName(final String s) {
        final Object[] bytecodeName = parseBytecodeName(s);
        for (int i = 0; i < bytecodeName.length; ++i) {
            if (bytecodeName[i] instanceof String) {
                final String s2 = (String)bytecodeName[i];
                if (!isJavaIdent(s2) || s2.indexOf(36) >= 0) {
                    bytecodeName[i] = quoteDisplay(s2);
                }
            }
        }
        return appendAll(bytecodeName);
    }
    
    private static boolean isJavaIdent(final String s) {
        final int length = s.length();
        if (length == 0) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < length; ++i) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private static String quoteDisplay(final String s) {
        return "'" + s.replaceAll("['\\\\]", "\\\\$0") + "'";
    }
    
    private static void checkSafeBytecodeName(final String s) throws IllegalArgumentException {
        if (!isSafeBytecodeName(s)) {
            throw new IllegalArgumentException(s);
        }
    }
    
    public static boolean isSafeBytecodeName(final String s) {
        if (s.length() == 0) {
            return false;
        }
        for (final char c : BytecodeName.DANGEROUS_CHARS_A) {
            if (c != BytecodeName.ESCAPE_C) {
                if (s.indexOf(c) >= 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isSafeBytecodeChar(final char c) {
        return "\\/.;:$[]<>".indexOf(c) < 1;
    }
    
    private static boolean looksMangled(final String s) {
        return s.charAt(0) == BytecodeName.ESCAPE_C;
    }
    
    private static String mangle(final String s) {
        if (s.length() == 0) {
            return BytecodeName.NULL_ESCAPE;
        }
        StringBuilder sb = null;
        for (int i = 0, length = s.length(); i < length; ++i) {
            final char char1 = s.charAt(i);
            boolean dangerous = false;
            if (char1 == BytecodeName.ESCAPE_C) {
                if (i + 1 < length) {
                    final char char2 = s.charAt(i + 1);
                    if ((i == 0 && char2 == BytecodeName.NULL_ESCAPE_C) || char2 != originalOfReplacement(char2)) {
                        dangerous = true;
                    }
                }
            }
            else {
                dangerous = isDangerous(char1);
            }
            if (!dangerous) {
                if (sb != null) {
                    sb.append(char1);
                }
            }
            else {
                if (sb == null) {
                    sb = new StringBuilder(s.length() + 10);
                    if (s.charAt(0) != BytecodeName.ESCAPE_C && i > 0) {
                        sb.append(BytecodeName.NULL_ESCAPE);
                    }
                    sb.append(s.substring(0, i));
                }
                sb.append(BytecodeName.ESCAPE_C);
                sb.append(replacementOf(char1));
            }
        }
        if (sb != null) {
            return sb.toString();
        }
        return s;
    }
    
    private static String demangle(final String s) {
        StringBuilder sb = null;
        int n = 0;
        if (s.startsWith(BytecodeName.NULL_ESCAPE)) {
            n = 2;
        }
        for (int i = n, length = s.length(); i < length; ++i) {
            char char1 = s.charAt(i);
            if (char1 == BytecodeName.ESCAPE_C && i + 1 < length) {
                final char char2 = s.charAt(i + 1);
                final char originalOfReplacement = originalOfReplacement(char2);
                if (originalOfReplacement != char2) {
                    if (sb == null) {
                        sb = new StringBuilder(s.length());
                        sb.append(s.substring(n, i));
                    }
                    ++i;
                    char1 = originalOfReplacement;
                }
            }
            if (sb != null) {
                sb.append(char1);
            }
        }
        if (sb != null) {
            return sb.toString();
        }
        return s.substring(n);
    }
    
    static boolean isSpecial(final char c) {
        return c >>> 6 < BytecodeName.SPECIAL_BITMAP.length && (BytecodeName.SPECIAL_BITMAP[c >>> 6] >> c & 0x1L) != 0x0L;
    }
    
    static char replacementOf(final char c) {
        if (!isSpecial(c)) {
            return c;
        }
        final int index = "\\/.;:$[]<>".indexOf(c);
        if (index < 0) {
            return c;
        }
        return "-|,?!%{}^_".charAt(index);
    }
    
    static char originalOfReplacement(final char c) {
        if (!isSpecial(c)) {
            return c;
        }
        final int index = "-|,?!%{}^_".indexOf(c);
        if (index < 0) {
            return c;
        }
        return "\\/.;:$[]<>".charAt(index);
    }
    
    static boolean isDangerous(final char c) {
        return isSpecial(c) && "\\/.;:$[]<>".indexOf(c) >= 1;
    }
    
    static int indexOfDangerousChar(final String s, final int n) {
        for (int i = n; i < s.length(); ++i) {
            if (isDangerous(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
    
    static int lastIndexOfDangerousChar(final String s, final int n) {
        for (int i = Math.min(n, s.length() - 1); i >= 0; --i) {
            if (isDangerous(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
    
    static {
        BytecodeName.ESCAPE_C = '\\';
        BytecodeName.NULL_ESCAPE_C = '=';
        BytecodeName.NULL_ESCAPE = BytecodeName.ESCAPE_C + "" + BytecodeName.NULL_ESCAPE_C;
        BytecodeName.DANGEROUS_CHARS_A = "\\/.;:$[]<>".toCharArray();
        BytecodeName.REPLACEMENT_CHARS_A = "-|,?!%{}^_".toCharArray();
        final Character[] dangerous_CHARS_CA = new Character["\\/.;:$[]<>".length()];
        for (int i = 0; i < dangerous_CHARS_CA.length; ++i) {
            dangerous_CHARS_CA[i] = "\\/.;:$[]<>".charAt(i);
        }
        DANGEROUS_CHARS_CA = dangerous_CHARS_CA;
        SPECIAL_BITMAP = new long[2];
        for (final char c : "\\/.;:$[]<>-|,?!%{}^_".toCharArray()) {
            final long[] special_BITMAP = BytecodeName.SPECIAL_BITMAP;
            final int n = c >>> 6;
            special_BITMAP[n] |= 1L << c;
        }
    }
}
