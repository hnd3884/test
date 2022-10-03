package org.apache.taglibs.standard.util;

import java.io.IOException;
import java.io.Reader;
import javax.servlet.jsp.JspWriter;

public class EscapeXML
{
    private static final String[] ESCAPES;
    
    private static String getEscape(final char c) {
        if (c < EscapeXML.ESCAPES.length) {
            return EscapeXML.ESCAPES[c];
        }
        return null;
    }
    
    public static String escape(final String src) {
        int length = 0;
        for (int i = 0; i < src.length(); ++i) {
            final char c = src.charAt(i);
            final String escape = getEscape(c);
            if (escape != null) {
                length += escape.length();
            }
            else {
                ++length;
            }
        }
        if (length == src.length()) {
            return src;
        }
        final StringBuilder buf = new StringBuilder(length);
        for (int j = 0; j < src.length(); ++j) {
            final char c2 = src.charAt(j);
            final String escape2 = getEscape(c2);
            if (escape2 != null) {
                buf.append(escape2);
            }
            else {
                buf.append(c2);
            }
        }
        return buf.toString();
    }
    
    public static void emit(final Object src, final boolean escapeXml, final JspWriter out) throws IOException {
        if (src instanceof Reader) {
            emit((Reader)src, escapeXml, out);
        }
        else {
            emit(String.valueOf(src), escapeXml, out);
        }
    }
    
    public static void emit(final String src, final boolean escapeXml, final JspWriter out) throws IOException {
        if (escapeXml) {
            emit(src, out);
        }
        else {
            out.write(src);
        }
    }
    
    public static void emit(final String src, final JspWriter out) throws IOException {
        int end;
        int to;
        int from;
        for (end = src.length(), from = (to = 0); to < end; ++to) {
            final String escape = getEscape(src.charAt(to));
            if (escape != null) {
                if (to != from) {
                    out.write(src, from, to - from);
                }
                out.write(escape);
                from = to + 1;
            }
        }
        if (from != end) {
            out.write(src, from, end - from);
        }
    }
    
    public static void emit(final Reader src, final boolean escapeXml, final JspWriter out) throws IOException {
        int bufferSize = out.getBufferSize();
        if (bufferSize == 0) {
            bufferSize = 4096;
        }
        final char[] buffer = new char[bufferSize];
        int count;
        while ((count = src.read(buffer)) > 0) {
            if (escapeXml) {
                emit(buffer, 0, count, out);
            }
            else {
                out.write(buffer, 0, count);
            }
        }
    }
    
    public static void emit(final char[] buffer, int from, final int count, final JspWriter out) throws IOException {
        final int end = from + count;
        for (int to = from; to < end; ++to) {
            final String escape = getEscape(buffer[to]);
            if (escape != null) {
                if (to != from) {
                    out.write(buffer, from, to - from);
                }
                out.write(escape);
                from = to + 1;
            }
        }
        if (from != end) {
            out.write(buffer, from, end - from);
        }
    }
    
    static {
        final int size = 63;
        (ESCAPES = new String[size])[60] = "&lt;";
        EscapeXML.ESCAPES[62] = "&gt;";
        EscapeXML.ESCAPES[38] = "&amp;";
        EscapeXML.ESCAPES[39] = "&#039;";
        EscapeXML.ESCAPES[34] = "&#034;";
    }
}
