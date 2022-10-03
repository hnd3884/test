package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import java.io.InputStream;
import java.util.List;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;

public final class InternetHeaders
{
    private final FinalArrayList headers;
    private List headerValueView;
    
    public InternetHeaders() {
        this.headers = new FinalArrayList();
    }
    
    public InternetHeaders(final InputStream is) throws MessagingException {
        this.headers = new FinalArrayList();
        this.load(is);
    }
    
    public void load(final InputStream is) throws MessagingException {
        final LineInputStream lis = new LineInputStream(is);
        String prevline = null;
        final StringBuffer lineBuffer = new StringBuffer();
        try {
            String line;
            do {
                line = lis.readLine();
                if (line != null && (line.startsWith(" ") || line.startsWith("\t"))) {
                    if (prevline != null) {
                        lineBuffer.append(prevline);
                        prevline = null;
                    }
                    lineBuffer.append("\r\n");
                    lineBuffer.append(line);
                }
                else {
                    if (prevline != null) {
                        this.addHeaderLine(prevline);
                    }
                    else if (lineBuffer.length() > 0) {
                        this.addHeaderLine(lineBuffer.toString());
                        lineBuffer.setLength(0);
                    }
                    prevline = line;
                }
            } while (line != null && line.length() > 0);
        }
        catch (final IOException ioex) {
            throw new MessagingException("Error in input stream", ioex);
        }
    }
    
    public String[] getHeader(final String name) {
        final FinalArrayList v = new FinalArrayList();
        for (int len = this.headers.size(), i = 0; i < len; ++i) {
            final hdr h = this.headers.get(i);
            if (name.equalsIgnoreCase(h.name)) {
                v.add(h.getValue());
            }
        }
        if (v.size() == 0) {
            return null;
        }
        return v.toArray(new String[v.size()]);
    }
    
    public String getHeader(final String name, final String delimiter) {
        final String[] s = this.getHeader(name);
        if (s == null) {
            return null;
        }
        if (s.length == 1 || delimiter == null) {
            return s[0];
        }
        final StringBuffer r = new StringBuffer(s[0]);
        for (int i = 1; i < s.length; ++i) {
            r.append(delimiter);
            r.append(s[i]);
        }
        return r.toString();
    }
    
    public void setHeader(final String name, final String value) {
        boolean found = false;
        for (int i = 0; i < this.headers.size(); ++i) {
            final hdr h = this.headers.get(i);
            if (name.equalsIgnoreCase(h.name)) {
                if (!found) {
                    final int j;
                    if (h.line != null && (j = h.line.indexOf(58)) >= 0) {
                        h.line = h.line.substring(0, j + 1) + " " + value;
                    }
                    else {
                        h.line = name + ": " + value;
                    }
                    found = true;
                }
                else {
                    this.headers.remove(i);
                    --i;
                }
            }
        }
        if (!found) {
            this.addHeader(name, value);
        }
    }
    
    public void addHeader(final String name, final String value) {
        int pos = this.headers.size();
        for (int i = this.headers.size() - 1; i >= 0; --i) {
            final hdr h = this.headers.get(i);
            if (name.equalsIgnoreCase(h.name)) {
                this.headers.add(i + 1, new hdr(name, value));
                return;
            }
            if (h.name.equals(":")) {
                pos = i;
            }
        }
        this.headers.add(pos, new hdr(name, value));
    }
    
    public void removeHeader(final String name) {
        for (int i = 0; i < this.headers.size(); ++i) {
            final hdr h = this.headers.get(i);
            if (name.equalsIgnoreCase(h.name)) {
                this.headers.remove(i);
                --i;
            }
        }
    }
    
    public FinalArrayList getAllHeaders() {
        return this.headers;
    }
    
    public void addHeaderLine(final String line) {
        try {
            final char c = line.charAt(0);
            if (c == ' ' || c == '\t') {
                final hdr h = this.headers.get(this.headers.size() - 1);
                final StringBuilder sb = new StringBuilder();
                final hdr hdr = h;
                hdr.line = sb.append(hdr.line).append("\r\n").append(line).toString();
            }
            else {
                this.headers.add(new hdr(line));
            }
        }
        catch (final StringIndexOutOfBoundsException e) {}
        catch (final NoSuchElementException ex) {}
    }
    
    public List getAllHeaderLines() {
        if (this.headerValueView == null) {
            this.headerValueView = new AbstractList() {
                @Override
                public Object get(final int index) {
                    return InternetHeaders.this.headers.get(index).line;
                }
                
                @Override
                public int size() {
                    return InternetHeaders.this.headers.size();
                }
            };
        }
        return this.headerValueView;
    }
}
