package org.w3c.tidy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.Writer;

public class OutJavaImpl implements Out
{
    private Writer writer;
    private char[] newline;
    
    protected OutJavaImpl(final Configuration configuration, final String s, final OutputStream outputStream) throws UnsupportedEncodingException {
        this.writer = new OutputStreamWriter(outputStream, s);
        this.newline = configuration.newline;
    }
    
    protected OutJavaImpl(final Configuration configuration, final Writer writer) {
        this.writer = writer;
        this.newline = configuration.newline;
    }
    
    public void outc(final int n) {
        try {
            this.writer.write(n);
        }
        catch (final IOException ex) {
            System.err.println("OutJavaImpl.outc: " + ex.getMessage());
        }
    }
    
    public void outc(final byte b) {
        try {
            this.writer.write(b);
        }
        catch (final IOException ex) {
            System.err.println("OutJavaImpl.outc: " + ex.getMessage());
        }
    }
    
    public void newline() {
        try {
            this.writer.write(this.newline);
        }
        catch (final IOException ex) {
            System.err.println("OutJavaImpl.newline: " + ex.getMessage());
        }
    }
    
    public void flush() {
        try {
            this.writer.flush();
        }
        catch (final IOException ex) {
            System.err.println("OutJavaImpl.flush: " + ex.getMessage());
        }
    }
}
