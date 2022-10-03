package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.NoSuchElementException;
import java.util.List;
import java.io.IOException;

final class InternetHeaders
{
    private final FinalArrayList<Hdr> headers;
    
    InternetHeaders(final MIMEParser.LineInputStream lis) {
        this.headers = new FinalArrayList<Hdr>();
        String prevline = null;
        final StringBuilder lineBuffer = new StringBuilder();
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
            throw new MIMEParsingException("Error in input stream", ioex);
        }
    }
    
    List<String> getHeader(final String name) {
        final FinalArrayList<String> v = new FinalArrayList<String>();
        for (int len = this.headers.size(), i = 0; i < len; ++i) {
            final Hdr h = this.headers.get(i);
            if (name.equalsIgnoreCase(h.name)) {
                v.add(h.getValue());
            }
        }
        return (v.size() == 0) ? null : v;
    }
    
    FinalArrayList<? extends Header> getAllHeaders() {
        return this.headers;
    }
    
    void addHeaderLine(final String line) {
        try {
            final char c = line.charAt(0);
            if (c == ' ' || c == '\t') {
                final Hdr h = this.headers.get(this.headers.size() - 1);
                final StringBuilder sb = new StringBuilder();
                final Hdr hdr = h;
                hdr.line = sb.append(hdr.line).append("\r\n").append(line).toString();
            }
            else {
                this.headers.add(new Hdr(line));
            }
        }
        catch (final StringIndexOutOfBoundsException ex) {}
        catch (final NoSuchElementException ex2) {}
    }
}
