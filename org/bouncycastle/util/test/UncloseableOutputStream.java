package org.bouncycastle.util.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class UncloseableOutputStream extends FilterOutputStream
{
    public UncloseableOutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    @Override
    public void close() {
        throw new RuntimeException("close() called on UncloseableOutputStream");
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.out.write(array, n, n2);
    }
}
