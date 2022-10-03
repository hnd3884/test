package com.zoho.cp;

import java.io.IOException;
import java.io.OutputStream;

public class MeteredOutputStream extends OutputStream
{
    private OutputStream out;
    private int bytesWritten;
    
    public MeteredOutputStream(final OutputStream out) {
        this.out = out;
    }
    
    public int getBytesWritten() {
        return this.bytesWritten;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
        ++this.bytesWritten;
    }
    
    @Override
    public int hashCode() {
        return this.out.hashCode();
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
        this.bytesWritten += b.length;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
        this.bytesWritten += len;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.out.equals(obj);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    @Override
    public String toString() {
        return this.out.toString();
    }
}
