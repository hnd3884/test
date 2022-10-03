package com.sun.jndi.toolkit.url;

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

public final class UrlUtil
{
    private UrlUtil() {
    }
    
    public static final String decode(final String s) throws MalformedURLException {
        try {
            return decode(s, "8859_1");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new MalformedURLException("ISO-Latin-1 decoder unavailable");
        }
    }
    
    public static final String decode(final String s, final String s2) throws MalformedURLException, UnsupportedEncodingException {
        try {
            return URLDecoder.decode(s, s2);
        }
        catch (final IllegalArgumentException ex) {
            final MalformedURLException ex2 = new MalformedURLException("Invalid URI encoding: " + s);
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    public static final String encode(final String s, final String s2) throws UnsupportedEncodingException {
        final byte[] bytes = s.getBytes(s2);
        final int length = bytes.length;
        final char[] array = new char[3 * length];
        int n = 0;
        for (int i = 0; i < length; ++i) {
            if ((bytes[i] >= 97 && bytes[i] <= 122) || (bytes[i] >= 65 && bytes[i] <= 90) || (bytes[i] >= 48 && bytes[i] <= 57) || "=,+;.'-@&/$_()!~*:".indexOf(bytes[i]) >= 0) {
                array[n++] = (char)bytes[i];
            }
            else {
                array[n++] = '%';
                array[n++] = Character.forDigit(0xF & bytes[i] >>> 4, 16);
                array[n++] = Character.forDigit(0xF & bytes[i], 16);
            }
        }
        return new String(array, 0, n);
    }
}
