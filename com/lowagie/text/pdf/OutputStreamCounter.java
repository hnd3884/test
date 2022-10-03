package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCounter extends OutputStream
{
    protected OutputStream out;
    protected int counter;
    
    public OutputStreamCounter(final OutputStream out) {
        this.counter = 0;
        this.out = out;
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.counter += b.length;
        this.out.write(b);
    }
    
    @Override
    public void write(final int b) throws IOException {
        ++this.counter;
        this.out.write(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.counter += len;
        this.out.write(b, off, len);
    }
    
    public int getCounter() {
        return this.counter;
    }
    
    public void resetCounter() {
        this.counter = 0;
    }
}
