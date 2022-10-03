package com.theorem.radius3.radutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.UnsupportedEncodingException;

public class Util
{
    private static final char[] a;
    
    public static byte[] toUTF8(final String s) {
        try {
            return s.getBytes("UTF8");
        }
        catch (final UnsupportedEncodingException ex) {
            return s.getBytes();
        }
    }
    
    public static String toUTF8(final byte[] array) {
        try {
            return new String(array, "UTF8");
        }
        catch (final UnsupportedEncodingException ex) {
            return new String(array);
        }
    }
    
    public static byte[] toUTF8(final char[] array) {
        try {
            final CharArrayReader charArrayReader = new CharArrayReader(array);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF8");
            int read;
            while ((read = charArrayReader.read()) != -1) {
                outputStreamWriter.write(read);
            }
            outputStreamWriter.flush();
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            charArrayReader.close();
            byteArrayOutputStream.close();
            outputStreamWriter.close();
            return byteArray;
        }
        catch (final Exception ex) {
            return new String(array).getBytes();
        }
    }
    
    public static String dump(final byte[] array, final int n, final int n2) {
        return ByteIterator.dump(array, n, n2);
    }
    
    public static String toHexString(final byte[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int length = array.length, i = 0; i < length; ++i) {
            sb.append(Util.a[array[i] >>> 4 & 0xF]).append(Util.a[array[i] & 0xF]);
        }
        return sb.toString();
    }
    
    public static String toHexString(final byte[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        for (int length = array.length, n3 = 0, n4 = n; n4 < length && n3 < n2; ++n4, ++n3) {
            sb.append(Util.a[array[n4] >>> 4 & 0xF]).append(Util.a[array[n4] & 0xF]);
        }
        return sb.toString();
    }
    
    public static boolean cmp(final byte[] array, final byte[] array2) {
        if (array == null || array2 == null) {
            return false;
        }
        final int length = array.length;
        if (length != array2.length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean cmp(final byte[] array, int n, final byte[] array2, int n2, final int n3) {
        if (array == null || array2 == null) {
            return false;
        }
        try {
            for (int i = 0; i < n3; ++i) {
                if (array[n++] != array2[n2++]) {
                    return false;
                }
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return false;
        }
        return true;
    }
    
    public static String dumpStack(final Throwable t) {
        final CharArrayWriter charArrayWriter = new CharArrayWriter();
        t.printStackTrace(new PrintWriter(charArrayWriter));
        return charArrayWriter.toString() + "\n";
    }
    
    public static byte[] toASCII(final String s) {
        try {
            return s.getBytes("US-ASCII");
        }
        catch (final UnsupportedEncodingException ex) {
            return s.getBytes();
        }
    }
    
    public static String toASCII(final byte[] array) {
        try {
            return new String(array, "US-ASCII");
        }
        catch (final UnsupportedEncodingException ex) {
            return new String(array);
        }
    }
    
    public static String[] splitUserName(String substring, final char c, final char c2) {
        final String[] array = new String[3];
        Arrays.fill(array, "");
        if (substring == null) {
            return array;
        }
        final int index = substring.indexOf(c2);
        int index2 = substring.indexOf(c);
        if (index2 > 0 && index > 0 && index2 > index) {
            index2 = -1;
        }
        if (index > 0) {
            array[2] = substring.substring(index + 1);
            substring = substring.substring(0, index);
        }
        else {
            array[2] = "";
        }
        if (index2 > 0) {
            array[0] = substring.substring(0, index2);
        }
        array[1] = ((index2 < 0) ? substring : substring.substring(index2 + 1));
        while (array[1].charAt(0) == c) {
            array[1] = array[1].substring(1);
        }
        return array;
    }
    
    public static byte[] hexToArray(final String s) throws IllegalArgumentException {
        final ArrayList list = new ArrayList();
        final int length = s.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("String has an odd number of characters.");
        }
        final byte[] array = new byte[length / 2];
        int n = 0;
        for (int i = 0; i < length; i += 2) {
            final String substring = s.substring(i, i + 2);
            try {
                array[n++] = (byte)Integer.parseInt(substring, 16);
            }
            catch (final NumberFormatException ex) {
                throw new IllegalArgumentException("Error parsing at position " + i);
            }
        }
        return array;
    }
    
    static {
        a = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
