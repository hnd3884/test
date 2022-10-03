package org.apache.tika.utils;

public class StringUtils
{
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    static int PAD_LIMIT;
    
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    public static boolean isBlank(final String s) {
        return s == null || s.trim().length() == 0;
    }
    
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= StringUtils.PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        final char[] padding = new char[pads];
        final char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; ++i) {
            padding[i] = padChars[i % padLen];
        }
        return new String(padding).concat(str);
    }
    
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > StringUtils.PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }
    
    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return "";
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; --i) {
            buf[i] = ch;
        }
        return new String(buf);
    }
    
    public static String repeat(final String str, final int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= StringUtils.PAD_LIMIT) {
            return repeat(str.charAt(0), repeat);
        }
        final int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1: {
                return repeat(str.charAt(0), repeat);
            }
            case 2: {
                final char ch0 = str.charAt(0);
                final char ch2 = str.charAt(1);
                final char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; --i, --i) {
                    output2[i] = ch0;
                    output2[i + 1] = ch2;
                }
                return new String(output2);
            }
            default: {
                final StringBuilder buf = new StringBuilder(outputLength);
                for (int j = 0; j < repeat; ++j) {
                    buf.append(str);
                }
                return buf.toString();
            }
        }
    }
    
    static {
        StringUtils.PAD_LIMIT = 10000;
    }
}
