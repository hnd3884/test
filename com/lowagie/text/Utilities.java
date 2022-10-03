package com.lowagie.text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URL;
import com.lowagie.text.pdf.PRTokeniser;
import java.util.Properties;
import java.util.Collections;
import java.util.Set;
import java.util.Hashtable;

public class Utilities
{
    public static Set getKeySet(final Hashtable table) {
        return (table == null) ? Collections.EMPTY_SET : table.keySet();
    }
    
    public static Object[][] addToArray(Object[][] original, final Object[] item) {
        if (original == null) {
            original = new Object[][] { item };
            return original;
        }
        final Object[][] original2 = new Object[original.length + 1][];
        System.arraycopy(original, 0, original2, 0, original.length);
        original2[original.length] = item;
        return original2;
    }
    
    public static boolean checkTrueOrFalse(final Properties attributes, final String key) {
        return "true".equalsIgnoreCase(attributes.getProperty(key));
    }
    
    public static String unEscapeURL(final String src) {
        final StringBuffer bf = new StringBuffer();
        final char[] s = src.toCharArray();
        for (int k = 0; k < s.length; ++k) {
            final char c = s[k];
            if (c == '%') {
                if (k + 2 >= s.length) {
                    bf.append(c);
                }
                else {
                    final int a0 = PRTokeniser.getHex(s[k + 1]);
                    final int a2 = PRTokeniser.getHex(s[k + 2]);
                    if (a0 < 0 || a2 < 0) {
                        bf.append(c);
                    }
                    else {
                        bf.append((char)(a0 * 16 + a2));
                        k += 2;
                    }
                }
            }
            else {
                bf.append(c);
            }
        }
        return bf.toString();
    }
    
    public static URL toURL(final String filename) throws MalformedURLException {
        try {
            return new URL(filename);
        }
        catch (final Exception e) {
            return new File(filename).toURI().toURL();
        }
    }
    
    public static void skip(final InputStream is, int size) throws IOException {
        while (size > 0) {
            final long n = is.skip(size);
            if (n <= 0L) {
                break;
            }
            size -= (int)n;
        }
    }
    
    public static final float millimetersToPoints(final float value) {
        return inchesToPoints(millimetersToInches(value));
    }
    
    public static final float millimetersToInches(final float value) {
        return value / 25.4f;
    }
    
    public static final float pointsToMillimeters(final float value) {
        return inchesToMillimeters(pointsToInches(value));
    }
    
    public static final float pointsToInches(final float value) {
        return value / 72.0f;
    }
    
    public static final float inchesToMillimeters(final float value) {
        return value * 25.4f;
    }
    
    public static final float inchesToPoints(final float value) {
        return value * 72.0f;
    }
    
    public static boolean isSurrogateHigh(final char c) {
        return c >= '\ud800' && c <= '\udbff';
    }
    
    public static boolean isSurrogateLow(final char c) {
        return c >= '\udc00' && c <= '\udfff';
    }
    
    public static boolean isSurrogatePair(final String text, final int idx) {
        return idx >= 0 && idx <= text.length() - 2 && isSurrogateHigh(text.charAt(idx)) && isSurrogateLow(text.charAt(idx + 1));
    }
    
    public static boolean isSurrogatePair(final char[] text, final int idx) {
        return idx >= 0 && idx <= text.length - 2 && isSurrogateHigh(text[idx]) && isSurrogateLow(text[idx + 1]);
    }
    
    public static int convertToUtf32(final char highSurrogate, final char lowSurrogate) {
        return (highSurrogate - '\ud800') * 1024 + (lowSurrogate - '\udc00') + 65536;
    }
    
    public static int convertToUtf32(final char[] text, final int idx) {
        return (text[idx] - '\ud800') * 1024 + (text[idx + 1] - '\udc00') + 65536;
    }
    
    public static int convertToUtf32(final String text, final int idx) {
        return (text.charAt(idx) - '\ud800') * 1024 + (text.charAt(idx + 1) - '\udc00') + 65536;
    }
    
    public static String convertFromUtf32(int codePoint) {
        if (codePoint < 65536) {
            return Character.toString((char)codePoint);
        }
        codePoint -= 65536;
        return new String(new char[] { (char)(codePoint / 1024 + 55296), (char)(codePoint % 1024 + 56320) });
    }
}
