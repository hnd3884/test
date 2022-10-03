package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.util.Map;
import java.io.OutputStream;

public class UtfHelpper
{
    static final void writeByte(final String s, final OutputStream outputStream, final Map map) throws IOException {
        byte[] stringInUtf8 = map.get(s);
        if (stringInUtf8 == null) {
            stringInUtf8 = getStringInUtf8(s);
            map.put(s, stringInUtf8);
        }
        outputStream.write(stringInUtf8);
    }
    
    static final void writeCharToUtf8(final char c, final OutputStream outputStream) throws IOException {
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
    
    static final void writeStringToUtf8(final String s, final OutputStream outputStream) throws IOException {
        final int length = s.length();
        int i = 0;
        while (i < length) {
            final char char1 = s.charAt(i++);
            if (char1 < '\u0080') {
                outputStream.write(char1);
            }
            else if ((char1 >= '\ud800' && char1 <= '\udbff') || (char1 >= '\udc00' && char1 <= '\udfff')) {
                outputStream.write(63);
            }
            else {
                int n2;
                char c2;
                if (char1 > '\u07ff') {
                    final char c = (char)(char1 >>> 12);
                    int n = 224;
                    if (c > '\0') {
                        n |= (c & '\u000f');
                    }
                    outputStream.write(n);
                    n2 = 128;
                    c2 = '?';
                }
                else {
                    n2 = 192;
                    c2 = '\u001f';
                }
                final char c3 = (char)(char1 >>> 6);
                if (c3 > '\0') {
                    n2 |= (c3 & c2);
                }
                outputStream.write(n2);
                outputStream.write(0x80 | (char1 & '?'));
            }
        }
    }
    
    public static final byte[] getStringInUtf8(final String s) {
        final int length = s.length();
        int n = 0;
        byte[] array = new byte[length];
        int i = 0;
        int n2 = 0;
        while (i < length) {
            final char char1 = s.charAt(i++);
            if (char1 < '\u0080') {
                array[n2++] = (byte)char1;
            }
            else if ((char1 >= '\ud800' && char1 <= '\udbff') || (char1 >= '\udc00' && char1 <= '\udfff')) {
                array[n2++] = 63;
            }
            else {
                if (n == 0) {
                    final byte[] array2 = new byte[3 * length];
                    System.arraycopy(array, 0, array2, 0, n2);
                    array = array2;
                    n = 1;
                }
                byte b2;
                int n3;
                if (char1 > '\u07ff') {
                    final char c = (char)(char1 >>> 12);
                    byte b = -32;
                    if (c > '\0') {
                        b |= (byte)(c & '\u000f');
                    }
                    array[n2++] = b;
                    b2 = -128;
                    n3 = '?';
                }
                else {
                    b2 = -64;
                    n3 = '\u001f';
                }
                final char c2 = (char)(char1 >>> 6);
                if (c2 > '\0') {
                    b2 |= (byte)(c2 & n3);
                }
                array[n2++] = b2;
                array[n2++] = (byte)(0x80 | (char1 & '?'));
            }
        }
        if (n != 0) {
            final byte[] array3 = new byte[n2];
            System.arraycopy(array, 0, array3, 0, n2);
            array = array3;
        }
        return array;
    }
}
