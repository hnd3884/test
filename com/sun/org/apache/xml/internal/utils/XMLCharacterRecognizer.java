package com.sun.org.apache.xml.internal.utils;

public class XMLCharacterRecognizer
{
    public static boolean isWhiteSpace(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }
    
    public static boolean isWhiteSpace(final char[] ch, final int start, final int length) {
        for (int end = start + length, s = start; s < end; ++s) {
            if (!isWhiteSpace(ch[s])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isWhiteSpace(final StringBuffer buf) {
        for (int n = buf.length(), i = 0; i < n; ++i) {
            if (!isWhiteSpace(buf.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isWhiteSpace(final String s) {
        if (null != s) {
            for (int n = s.length(), i = 0; i < n; ++i) {
                if (!isWhiteSpace(s.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
