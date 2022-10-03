package com.maverick.util;

import java.io.IOException;
import java.io.InputStream;

class c extends InputStream
{
    EOLProcessor e;
    InputStream b;
    DynamicBuffer c;
    byte[] d;
    
    public c(final int n, final int n2, final InputStream b) throws IOException {
        this.c = new DynamicBuffer();
        this.d = new byte[32768];
        this.b = b;
        this.e = new EOLProcessor(n, n2, this.c.getOutputStream());
    }
    
    public int read() throws IOException {
        this.b(1);
        return this.c.getInputStream().read();
    }
    
    public int available() throws IOException {
        return this.b.available();
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        this.b(n2);
        return this.c.getInputStream().read(array, n, n2);
    }
    
    private void b(final int n) throws IOException {
        while (this.c.available() < n) {
            final int read = this.b.read(this.d);
            if (read == -1) {
                this.c.close();
                return;
            }
            this.e.processBytes(this.d, 0, read);
        }
    }
    
    public void close() throws IOException {
        this.b.close();
    }
}
