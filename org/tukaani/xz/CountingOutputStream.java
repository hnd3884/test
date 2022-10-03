package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;

class CountingOutputStream extends FinishableOutputStream
{
    private final OutputStream out;
    private long size;
    
    public CountingOutputStream(final OutputStream out) {
        this.size = 0L;
        this.out = out;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.out.write(n);
        if (this.size >= 0L) {
            ++this.size;
        }
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.out.write(array, n, n2);
        if (this.size >= 0L) {
            this.size += n2;
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    public long getSize() {
        return this.size;
    }
}
