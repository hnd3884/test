package com.adventnet.iam.security;

import java.io.Writer;
import java.io.PrintWriter;

public class SecurityPrintWriter extends PrintWriter
{
    private static final String NEW_LINE = "\r\n";
    private StringBuilder response;
    ResponseLogRule logRule;
    
    private boolean isCopyResponseNeeded() {
        if (this.logRule != null && this.response != null) {
            final long allowedSize = this.logRule.getAllowedSize();
            if (this.response.length() < allowedSize) {
                return true;
            }
        }
        this.response = null;
        return false;
    }
    
    public SecurityPrintWriter(final PrintWriter out) {
        super(out);
        this.response = new StringBuilder();
        this.logRule = null;
    }
    
    public SecurityPrintWriter(final PrintWriter out, final ResponseLogRule logRule) {
        super(out);
        this.response = new StringBuilder();
        this.logRule = null;
        this.logRule = logRule;
    }
    
    public String getResponse() {
        if (this.response != null && this.response.length() > 0) {
            return this.response.toString();
        }
        return null;
    }
    
    @Override
    public void write(final int c) {
        if (this.isCopyResponseNeeded()) {
            this.response.append(c);
        }
        super.write(c);
    }
    
    @Override
    public void write(final char[] buf, final int off, final int len) {
        if (this.isCopyResponseNeeded()) {
            this.response.append(buf, off, len);
        }
        super.write(buf, off, len);
    }
    
    @Override
    public void write(final String s, final int off, final int len) {
        if (this.isCopyResponseNeeded()) {
            this.response.append(s);
        }
        super.write(s, off, len);
    }
    
    @Override
    public void println() {
        if (this.isCopyResponseNeeded()) {
            this.response.append("\r\n");
        }
        super.println();
    }
}
