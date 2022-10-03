package com.fasterxml.jackson.dataformat.xml.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public class StaxUtil
{
    @Deprecated
    public static <T> T throwXmlAsIOException(final XMLStreamException e) throws IOException {
        final Throwable t = _unwrap(e);
        throw new IOException(t);
    }
    
    public static <T> T throwAsParseException(final XMLStreamException e, final JsonParser p) throws IOException {
        final Throwable t = _unwrap(e);
        throw new JsonParseException(p, _message(t, e), t);
    }
    
    public static <T> T throwAsGenerationException(final XMLStreamException e, final JsonGenerator g) throws IOException {
        final Throwable t = _unwrap(e);
        throw new JsonGenerationException(_message(t, e), t, g);
    }
    
    private static Throwable _unwrap(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        return t;
    }
    
    private static String _message(final Throwable t1, final Throwable t2) {
        String msg = t1.getMessage();
        if (msg == null) {
            msg = t2.getMessage();
        }
        return msg;
    }
    
    public static String sanitizeXmlTypeName(String name) {
        int changes = 0;
        StringBuilder sb;
        if (name.endsWith("[]")) {
            do {
                name = name.substring(0, name.length() - 2);
                ++changes;
            } while (name.endsWith("[]"));
            sb = new StringBuilder(name);
            if (name.endsWith("s")) {
                sb.append("es");
            }
            else {
                sb.append('s');
            }
        }
        else {
            sb = new StringBuilder(name);
        }
        for (int i = 0, len = name.length(); i < len; ++i) {
            final char c = name.charAt(i);
            if (c <= '\u007f') {
                if (c < 'a' || c > 'z') {
                    if (c < 'A' || c > 'Z') {
                        if (c < '0' || c > '9') {
                            if (c != '_' && c != '.') {
                                if (c != '-') {
                                    ++changes;
                                    if (c == '$') {
                                        sb.setCharAt(i, '.');
                                    }
                                    else {
                                        sb.setCharAt(i, '_');
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (changes == 0) {
            return name;
        }
        return sb.toString();
    }
}
