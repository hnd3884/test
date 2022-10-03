package com.sshtools.net;

import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public abstract class HttpHeader
{
    protected static final String white_SPACE = " \t\r";
    Hashtable b;
    protected String begin;
    
    protected HttpHeader() {
        this.b = new Hashtable();
    }
    
    protected String readLine(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        while (true) {
            final int read = inputStream.read();
            if (read == -1) {
                throw new IOException("Failed to read expected HTTP header line");
            }
            if (read == 10) {
                continue;
            }
            if (read == 13) {
                return new String(sb);
            }
            sb.append((char)read);
        }
    }
    
    public String getStartLine() {
        return this.begin;
    }
    
    public Hashtable getHeaderFields() {
        return this.b;
    }
    
    public Enumeration getHeaderFieldNames() {
        return this.b.keys();
    }
    
    public String getHeaderField(final String s) {
        final Enumeration keys = this.b.keys();
        while (keys.hasMoreElements()) {
            final String s2 = (String)keys.nextElement();
            if (s2.equalsIgnoreCase(s)) {
                return (String)this.b.get(s2);
            }
        }
        return null;
    }
    
    public void setHeaderField(final String s, final String s2) {
        this.b.put(s, s2);
    }
    
    public String toString() {
        String s = this.begin + "\r\n";
        final Enumeration headerFieldNames = this.getHeaderFieldNames();
        while (headerFieldNames.hasMoreElements()) {
            final String s2 = headerFieldNames.nextElement();
            s = s + s2 + ": " + this.getHeaderField(s2) + "\r\n";
        }
        return s + "\r\n";
    }
    
    protected void processHeaderFields(final InputStream inputStream) throws IOException {
        this.b = new Hashtable();
        final StringBuffer sb = new StringBuffer();
        String b = null;
        while (true) {
            final int read = inputStream.read();
            if (read == -1) {
                throw new IOException("EOF returned from server but HTTP response is not complete!");
            }
            if (read == 10) {
                continue;
            }
            if (read != 13) {
                sb.append((char)read);
            }
            else {
                if (sb.length() == 0) {
                    inputStream.read();
                    return;
                }
                b = this.b(sb.toString(), b);
                sb.setLength(0);
            }
        }
    }
    
    private String b(final String s, final String s2) throws IOException {
        final char char1 = s.charAt(0);
        String lowerCase;
        String s3;
        if (char1 == ' ' || char1 == '\t') {
            lowerCase = s2;
            s3 = this.getHeaderField(s2) + " " + s.trim();
        }
        else {
            final int index = s.indexOf(58);
            if (index == -1) {
                throw new IOException("HTTP Header encoutered a corrupt field: '" + s + "'");
            }
            lowerCase = s.substring(0, index).toLowerCase();
            s3 = s.substring(index + 1).trim();
        }
        this.setHeaderField(lowerCase, s3);
        return lowerCase;
    }
}
