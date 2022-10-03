package org.bouncycastle.util;

import java.util.ArrayList;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public final class Strings
{
    private static String LINE_SEPARATOR;
    
    public static String fromUTF8ByteArray(final byte[] array) {
        int i = 0;
        int n = 0;
        while (i < array.length) {
            ++n;
            if ((array[i] & 0xF0) == 0xF0) {
                ++n;
                i += 4;
            }
            else if ((array[i] & 0xE0) == 0xE0) {
                i += 3;
            }
            else if ((array[i] & 0xC0) == 0xC0) {
                i += 2;
            }
            else {
                ++i;
            }
        }
        final char[] array2 = new char[n];
        int j = 0;
        int n2 = 0;
        while (j < array.length) {
            char c3;
            if ((array[j] & 0xF0) == 0xF0) {
                final int n3 = ((array[j] & 0x3) << 18 | (array[j + 1] & 0x3F) << 12 | (array[j + 2] & 0x3F) << 6 | (array[j + 3] & 0x3F)) - 65536;
                final char c = (char)(0xD800 | n3 >> 10);
                final char c2 = (char)(0xDC00 | (n3 & 0x3FF));
                array2[n2++] = c;
                c3 = c2;
                j += 4;
            }
            else if ((array[j] & 0xE0) == 0xE0) {
                c3 = (char)((array[j] & 0xF) << 12 | (array[j + 1] & 0x3F) << 6 | (array[j + 2] & 0x3F));
                j += 3;
            }
            else if ((array[j] & 0xD0) == 0xD0) {
                c3 = (char)((array[j] & 0x1F) << 6 | (array[j + 1] & 0x3F));
                j += 2;
            }
            else if ((array[j] & 0xC0) == 0xC0) {
                c3 = (char)((array[j] & 0x1F) << 6 | (array[j + 1] & 0x3F));
                j += 2;
            }
            else {
                c3 = (char)(array[j] & 0xFF);
                ++j;
            }
            array2[n2++] = c3;
        }
        return new String(array2);
    }
    
    public static byte[] toUTF8ByteArray(final String s) {
        return toUTF8ByteArray(s.toCharArray());
    }
    
    public static byte[] toUTF8ByteArray(final char[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            toUTF8ByteArray(array, byteArrayOutputStream);
        }
        catch (final IOException ex) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static void toUTF8ByteArray(final char[] array, final OutputStream outputStream) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            final char c = array[i];
            if (c < '\u0080') {
                outputStream.write(c);
            }
            else if (c < '\u0800') {
                outputStream.write(0xC0 | c >> 6);
                outputStream.write(0x80 | (c & '?'));
            }
            else if (c >= '\ud800' && c <= '\udfff') {
                if (i + 1 >= array.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                final char c2 = c;
                final char c3 = array[++i];
                if (c2 > '\udbff') {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                final int n = ((c2 & '\u03ff') << 10 | (c3 & '\u03ff')) + 65536;
                outputStream.write(0xF0 | n >> 18);
                outputStream.write(0x80 | (n >> 12 & 0x3F));
                outputStream.write(0x80 | (n >> 6 & 0x3F));
                outputStream.write(0x80 | (n & 0x3F));
            }
            else {
                outputStream.write(0xE0 | c >> 12);
                outputStream.write(0x80 | (c >> 6 & 0x3F));
                outputStream.write(0x80 | (c & '?'));
            }
        }
    }
    
    public static String toUpperCase(final String s) {
        boolean b = false;
        final char[] charArray = s.toCharArray();
        for (int i = 0; i != charArray.length; ++i) {
            final char c = charArray[i];
            if ('a' <= c && 'z' >= c) {
                b = true;
                charArray[i] = (char)(c - 'a' + 65);
            }
        }
        if (b) {
            return new String(charArray);
        }
        return s;
    }
    
    public static String toLowerCase(final String s) {
        boolean b = false;
        final char[] charArray = s.toCharArray();
        for (int i = 0; i != charArray.length; ++i) {
            final char c = charArray[i];
            if ('A' <= c && 'Z' >= c) {
                b = true;
                charArray[i] = (char)(c - 'A' + 97);
            }
        }
        if (b) {
            return new String(charArray);
        }
        return s;
    }
    
    public static byte[] toByteArray(final char[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (byte)array[i];
        }
        return array2;
    }
    
    public static byte[] toByteArray(final String s) {
        final byte[] array = new byte[s.length()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (byte)s.charAt(i);
        }
        return array;
    }
    
    public static int toByteArray(final String s, final byte[] array, final int n) {
        final int length = s.length();
        for (int i = 0; i < length; ++i) {
            array[n + i] = (byte)s.charAt(i);
        }
        return length;
    }
    
    public static String fromByteArray(final byte[] array) {
        return new String(asCharArray(array));
    }
    
    public static char[] asCharArray(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = (char)(array[i] & 0xFF);
        }
        return array2;
    }
    
    public static String[] split(String substring, final char c) {
        final Vector vector = new Vector();
        int i = 1;
        while (i != 0) {
            final int index = substring.indexOf(c);
            if (index > 0) {
                vector.addElement(substring.substring(0, index));
                substring = substring.substring(index + 1);
            }
            else {
                i = 0;
                vector.addElement(substring);
            }
        }
        final String[] array = new String[vector.size()];
        for (int j = 0; j != array.length; ++j) {
            array[j] = (String)vector.elementAt(j);
        }
        return array;
    }
    
    public static StringList newList() {
        return new StringListImpl();
    }
    
    public static String lineSeparator() {
        return Strings.LINE_SEPARATOR;
    }
    
    static {
        try {
            Strings.LINE_SEPARATOR = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("line.separator");
                }
            });
        }
        catch (final Exception ex) {
            try {
                Strings.LINE_SEPARATOR = String.format("%n", new Object[0]);
            }
            catch (final Exception ex2) {
                Strings.LINE_SEPARATOR = "\n";
            }
        }
    }
    
    private static class StringListImpl extends ArrayList<String> implements StringList
    {
        @Override
        public boolean add(final String s) {
            return super.add(s);
        }
        
        @Override
        public String set(final int n, final String s) {
            return super.set(n, s);
        }
        
        @Override
        public void add(final int n, final String s) {
            super.add(n, s);
        }
        
        public String[] toStringArray() {
            final String[] array = new String[this.size()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = this.get(i);
            }
            return array;
        }
        
        public String[] toStringArray(final int n, final int n2) {
            final String[] array = new String[n2 - n];
            for (int n3 = n; n3 != this.size() && n3 != n2; ++n3) {
                array[n3 - n] = this.get(n3);
            }
            return array;
        }
    }
}
