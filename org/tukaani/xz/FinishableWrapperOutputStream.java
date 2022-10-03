package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;

public class FinishableWrapperOutputStream extends FinishableOutputStream
{
    protected OutputStream out;
    
    public FinishableWrapperOutputStream(final OutputStream out) {
        this.out = out;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.out.write(n);
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        this.out.write(array);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.out.write(array, n, n2);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
