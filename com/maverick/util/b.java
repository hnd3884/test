package com.maverick.util;

import java.io.IOException;
import java.io.OutputStream;

class b extends OutputStream
{
    EOLProcessor b;
    
    public b(final int n, final int n2, final OutputStream outputStream) throws IOException {
        this.b = new EOLProcessor(n, n2, outputStream);
    }
    
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.b.processBytes(array, n, n2);
    }
    
    public void write(final int n) throws IOException {
        this.b.processBytes(new byte[] { (byte)n }, 0, 1);
    }
    
    public void close() throws IOException {
        this.b.close();
    }
}
