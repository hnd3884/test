package com.adventnet.client.util.web;

import java.io.UnsupportedEncodingException;

public class HTMLUtil
{
    public static String encode(String data) {
        data = decodeHex(data, "UTF-8");
        final StringBuffer buf = new StringBuffer();
        final char[] chars = data.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            buf.append("&#" + (int)chars[i]);
        }
        return buf.toString();
    }
    
    public static String decodeHex(final String data, final String charEncoding) {
        if (data == null) {
            return null;
        }
        byte[] inBytes = null;
        try {
            inBytes = data.getBytes(charEncoding);
        }
        catch (final UnsupportedEncodingException e) {
            inBytes = data.getBytes();
        }
        final byte[] outBytes = new byte[inBytes.length];
        int j = 0;
        for (int i = 0; i < inBytes.length; ++i) {
            outBytes[j++] = inBytes[i];
        }
        String encodedStr = null;
        try {
            encodedStr = new String(outBytes, 0, j, charEncoding);
        }
        catch (final UnsupportedEncodingException e2) {
            encodedStr = new String(outBytes, 0, j);
        }
        return encodedStr;
    }
}
