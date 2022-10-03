package org.apache.tomcat.util.http.fileupload.util.mime;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public final class RFC2231Utility
{
    private static final char[] HEX_DIGITS;
    private static final byte MASK = Byte.MAX_VALUE;
    private static final int MASK_128 = 128;
    private static final byte[] HEX_DECODE;
    
    private RFC2231Utility() {
    }
    
    public static boolean hasEncodedValue(final String paramName) {
        return paramName != null && paramName.lastIndexOf(42) == paramName.length() - 1;
    }
    
    public static String stripDelimiter(final String paramName) {
        if (hasEncodedValue(paramName)) {
            final StringBuilder paramBuilder = new StringBuilder(paramName);
            paramBuilder.deleteCharAt(paramName.lastIndexOf(42));
            return paramBuilder.toString();
        }
        return paramName;
    }
    
    public static String decodeText(final String encodedText) throws UnsupportedEncodingException {
        final int langDelimitStart = encodedText.indexOf(39);
        if (langDelimitStart == -1) {
            return encodedText;
        }
        final String mimeCharset = encodedText.substring(0, langDelimitStart);
        final int langDelimitEnd = encodedText.indexOf(39, langDelimitStart + 1);
        if (langDelimitEnd == -1) {
            return encodedText;
        }
        final byte[] bytes = fromHex(encodedText.substring(langDelimitEnd + 1));
        return new String(bytes, getJavaCharset(mimeCharset));
    }
    
    private static byte[] fromHex(final String text) {
        final int shift = 4;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(text.length());
        int i = 0;
        while (i < text.length()) {
            final char c = text.charAt(i++);
            if (c == '%') {
                if (i > text.length() - 2) {
                    break;
                }
                final byte b1 = RFC2231Utility.HEX_DECODE[text.charAt(i++) & '\u007f'];
                final byte b2 = RFC2231Utility.HEX_DECODE[text.charAt(i++) & '\u007f'];
                out.write(b1 << 4 | b2);
            }
            else {
                out.write((byte)c);
            }
        }
        return out.toByteArray();
    }
    
    private static String getJavaCharset(final String mimeCharset) {
        return mimeCharset;
    }
    
    static {
        HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        HEX_DECODE = new byte[128];
        for (int i = 0; i < RFC2231Utility.HEX_DIGITS.length; ++i) {
            RFC2231Utility.HEX_DECODE[RFC2231Utility.HEX_DIGITS[i]] = (byte)i;
            RFC2231Utility.HEX_DECODE[Character.toLowerCase(RFC2231Utility.HEX_DIGITS[i])] = (byte)i;
        }
    }
}
