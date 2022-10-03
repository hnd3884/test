package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.security.AccessController;
import java.io.IOException;
import java.util.Map;
import java.io.OutputStream;

public final class UtfHelpper
{
    private static final boolean OLD_UTF8;
    
    private UtfHelpper() {
    }
    
    public static void writeByte(final String s, final OutputStream outputStream, final Map<String, byte[]> map) throws IOException {
        byte[] stringInUtf8 = map.get(s);
        if (stringInUtf8 == null) {
            stringInUtf8 = getStringInUtf8(s);
            map.put(s, stringInUtf8);
        }
        outputStream.write(stringInUtf8);
    }
    
    public static void writeCodePointToUtf8(final int n, final OutputStream outputStream) throws IOException {
        if (!Character.isValidCodePoint(n) || (n >= 55296 && n <= 56319) || (n >= 56320 && n <= 57343)) {
            outputStream.write(63);
            return;
        }
        if (UtfHelpper.OLD_UTF8 && n >= 65536) {
            outputStream.write(63);
            outputStream.write(63);
            return;
        }
        if (n < 128) {
            outputStream.write(n);
            return;
        }
        int n2;
        if (n < 2048) {
            n2 = 1;
        }
        else if (n < 65536) {
            n2 = 2;
        }
        else if (n < 2097152) {
            n2 = 3;
        }
        else if (n < 67108864) {
            n2 = 4;
        }
        else {
            if (n > Integer.MAX_VALUE) {
                outputStream.write(63);
                return;
            }
            n2 = 5;
        }
        int n3 = 6 * n2;
        outputStream.write((byte)(254 << 6 - n2 | n >>> n3));
        for (int i = n2 - 1; i >= 0; --i) {
            n3 -= 6;
            outputStream.write((byte)(0x80 | (n >>> n3 & 0x3F)));
        }
    }
    
    @Deprecated
    public static void writeCharToUtf8(final char c, final OutputStream outputStream) throws IOException {
        if (c < '\u0080') {
            outputStream.write(c);
            return;
        }
        if ((c >= '\ud800' && c <= '\udbff') || (c >= '\udc00' && c <= '\udfff')) {
            outputStream.write(63);
            return;
        }
        int n2;
        int n3;
        if (c > '\u07ff') {
            final char c2 = (char)(c >>> 12);
            int n = 224;
            if (c2 > '\0') {
                n |= (c2 & '\u000f');
            }
            outputStream.write(n);
            n2 = 128;
            n3 = '?';
        }
        else {
            n2 = 192;
            n3 = '\u001f';
        }
        final char c3 = (char)(c >>> 6);
        if (c3 > '\0') {
            n2 |= (c3 & n3);
        }
        outputStream.write(n2);
        outputStream.write(0x80 | (c & '?'));
    }
    
    public static void writeStringToUtf8(final String s, final OutputStream outputStream) throws IOException {
        final int length = s.length();
        int i = 0;
        while (i < length) {
            final int codePoint = s.codePointAt(i);
            i += Character.charCount(codePoint);
            if (!Character.isValidCodePoint(codePoint) || (codePoint >= 55296 && codePoint <= 56319) || (codePoint >= 56320 && codePoint <= 57343)) {
                outputStream.write(63);
            }
            else if (UtfHelpper.OLD_UTF8 && codePoint >= 65536) {
                outputStream.write(63);
                outputStream.write(63);
            }
            else if (codePoint < 128) {
                outputStream.write(codePoint);
            }
            else {
                int n;
                if (codePoint < 2048) {
                    n = 1;
                }
                else if (codePoint < 65536) {
                    n = 2;
                }
                else if (codePoint < 2097152) {
                    n = 3;
                }
                else if (codePoint < 67108864) {
                    n = 4;
                }
                else {
                    if (codePoint > Integer.MAX_VALUE) {
                        outputStream.write(63);
                        continue;
                    }
                    n = 5;
                }
                int n2 = 6 * n;
                outputStream.write((byte)(254 << 6 - n | codePoint >>> n2));
                for (int j = n - 1; j >= 0; --j) {
                    n2 -= 6;
                    outputStream.write((byte)(0x80 | (codePoint >>> n2 & 0x3F)));
                }
            }
        }
    }
    
    public static byte[] getStringInUtf8(final String s) {
        final int length = s.length();
        int n = 0;
        byte[] array = new byte[length];
        int i = 0;
        int n2 = 0;
        while (i < length) {
            final int codePoint = s.codePointAt(i);
            i += Character.charCount(codePoint);
            if (!Character.isValidCodePoint(codePoint) || (codePoint >= 55296 && codePoint <= 56319) || (codePoint >= 56320 && codePoint <= 57343)) {
                array[n2++] = 63;
            }
            else if (UtfHelpper.OLD_UTF8 && codePoint >= 65536) {
                array[n2++] = 63;
                array[n2++] = 63;
            }
            else if (codePoint < 128) {
                array[n2++] = (byte)codePoint;
            }
            else {
                if (n == 0) {
                    final byte[] array2 = new byte[6 * length];
                    System.arraycopy(array, 0, array2, 0, n2);
                    array = array2;
                    n = 1;
                }
                int n3;
                if (codePoint < 2048) {
                    n3 = 1;
                }
                else if (codePoint < 65536) {
                    n3 = 2;
                }
                else if (codePoint < 2097152) {
                    n3 = 3;
                }
                else if (codePoint < 67108864) {
                    n3 = 4;
                }
                else {
                    if (codePoint > Integer.MAX_VALUE) {
                        array[n2++] = 63;
                        continue;
                    }
                    n3 = 5;
                }
                int n4 = 6 * n3;
                array[n2++] = (byte)(254 << 6 - n3 | codePoint >>> n4);
                for (int j = n3 - 1; j >= 0; --j) {
                    n4 -= 6;
                    array[n2++] = (byte)(0x80 | (codePoint >>> n4 & 0x3F));
                }
            }
        }
        if (n != 0) {
            final byte[] array3 = new byte[n2];
            System.arraycopy(array, 0, array3, 0, n2);
            array = array3;
        }
        return array;
    }
    
    static {
        OLD_UTF8 = AccessController.doPrivileged(() -> Boolean.getBoolean("com.sun.org.apache.xml.internal.security.c14n.oldUtf8"));
    }
}
